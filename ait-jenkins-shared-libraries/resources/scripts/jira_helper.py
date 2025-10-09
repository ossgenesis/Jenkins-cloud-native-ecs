#!/usr/bin/env python3
import requests
import json
import os
import re
import sys

# ---- Config ----
JIRA_BASE_URL = "https://woolworths.atlassian.net/rest/api/3"
JIRA_USERNAME = os.getenv("JIRA_USERNAME")
JIRA_API_TOKEN = os.getenv("JIRA_API_TOKEN")

HEADERS = {"Accept": "application/json", "Content-Type": "application/json"}

APPROVERS = {
    n.strip().lower()
    for n in [
        "bibin skaria", "amanda erlank", "nkosinathingcobo", "rikus swanepoel",
        "abdul atta", "anton titus", "prasant moharana", "andries rossouw"
    ]
}

# ---- Helpers ----
def normalize_json_string(s: str) -> str:
    """Normalize curly quotes and non-breaking spaces so json.loads works."""
    s = re.sub(r"[“”]", '"', s)
    s = re.sub(r"[‘’]", "'", s)
    s = s.replace("\u00a0", " ").strip()
    return s

def extract_plain_text_from_adf(adf) -> str:
    """Extract plain text from Jira ADF (rich text) or pass strings unchanged."""
    if isinstance(adf, str):
        return adf
    texts = []
    blocks = adf.get("content", []) if isinstance(adf, dict) else (adf if isinstance(adf, list) else [])
    for block in blocks:
        for node in block.get("content", []) if isinstance(block, dict) else []:
            t = node.get("text")
            if t:
                texts.append(t)
    return "\n".join(texts).strip()

def search_issues(auth, summary_search_term):
    """
    Jira Cloud search:
      - Prefer POST /search/jql with {"jql": "..."}  (your tenant requires this)
      - Fallback to POST /search with {"jql": "..."} if needed
    """
    jql = f'summary ~ "{summary_search_term}" ORDER BY created DESC'
    body = {"jql": jql, "maxResults": 20, "fields": ["summary", "status", "description"]}

    url = f"{JIRA_BASE_URL}/search/jql"
    r = requests.post(url, headers=HEADERS, auth=auth, json=body)
    if r.status_code == 200:
        return r.json().get("issues", [])

    # Fallback (some tenants still allow it)
    url_legacy = f"{JIRA_BASE_URL}/search"
    r2 = requests.post(url_legacy, headers=HEADERS, auth=auth, json=body)
    if r2.status_code == 200:
        return r2.json().get("issues", [])

    return {
        "error": "Search failed on both /search/jql and /search.",
        "search_jql_attempt": {"code": r.status_code, "body": r.text},
        "legacy_search_attempt": {"code": r2.status_code, "body": r2.text},
    }

# ---- Main logic ----
def get_repository_details(summary_search_term: str) -> str:
    """Find latest Approved issue (approved by allowed approver) and return its JSON description."""
    if not JIRA_USERNAME or not JIRA_API_TOKEN:
        return json.dumps({"error": "Missing Jira credentials"})

    auth = (JIRA_USERNAME, JIRA_API_TOKEN)

    issues = search_issues(auth, summary_search_term)
    if isinstance(issues, dict) and "error" in issues:
        return json.dumps({"error": "Unable to fetch Jira issues", "detail": issues})
    if not issues:
        return json.dumps({"error": "No Jira tickets found matching the query."})

    for issue in issues:
        key = issue["key"]
        status = (issue["fields"]["status"]["name"] or "").lower()
        if status != "approved":
            continue

        # Validate latest transition to Approved (from allowed statuses, by allowed approver)
        cl_url = f"{JIRA_BASE_URL}/issue/{key}"
        cl = requests.get(cl_url, headers=HEADERS, auth=auth, params={"expand": "changelog"})
        if cl.status_code != 200:
            print(f"[SKIP] {key}: changelog fetch HTTP {cl.status_code}", file=sys.stderr)
            continue

        histories = cl.json().get("changelog", {}).get("histories", [])
        transitions = []
        for h in histories:
            for it in h.get("items", []):
                if it.get("field") == "status":
                    transitions.append({
                        "from": (it.get("fromString") or "").lower(),
                        "to":   (it.get("toString") or "").lower(),
                        "author": (h.get("author", {}).get("displayName") or "").lower(),
                        "ts": h.get("created")
                    })
        transitions.sort(key=lambda x: x["ts"], reverse=True)
        latest_to_approved = next((t for t in transitions if t["to"] == "approved"), None)
        if not latest_to_approved:
            print(f"[SKIP] {key}: no transition to 'approved'", file=sys.stderr)
            continue
        if latest_to_approved["from"] not in {"in progress", "backlog"}:
            print(f"[SKIP] {key}: came from '{latest_to_approved['from']}'", file=sys.stderr)
            continue
        if latest_to_approved["author"] not in APPROVERS:
            print(f"[SKIP] {key}: approver '{latest_to_approved['author']}' not allowed", file=sys.stderr)
            continue

        # Parse description JSON
        description_adf = issue["fields"].get("description", {})
        desc_text = extract_plain_text_from_adf(description_adf)
        if not desc_text:
            print(f"[SKIP] {key}: empty description", file=sys.stderr)
            continue

        desc_text = normalize_json_string(desc_text)
        try:
            desc_json = json.loads(desc_text)
        except json.JSONDecodeError as e:
            print(f"[SKIP] {key}: invalid JSON in description ({e})", file=sys.stderr)
            continue

        return json.dumps({"jiraIssueKey": key, "description": desc_json})

    print(json.dumps({"error": "No valid Jira ticket found"}))
    return json.dumps({"error": "No valid Jira ticket found"})

if __name__ == "__main__":
    term = sys.argv[1] if len(sys.argv) > 1 else ""
    print(get_repository_details(term))
