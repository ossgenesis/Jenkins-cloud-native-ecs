import groovy.json.JsonSlurper
import com.cloudbees.groovy.cps.NonCPS
import java.net.URLEncoder

/**
 * Reads kafka_cluster_name (or returns existing kafka_cluster_id) from a tfvars file,
 * resolves the Confluent Kafka cluster ID via Cloud API, and returns [clusterId: String].
 * Does NOT modify the tfvars file.
 *
 * Args:
 *   tfvarsPath     - path to tfvars (e.g., "dev.tfvars")
 *   environmentId  - Confluent environment id (e.g., "env-052r5")
 *   credsId        - Jenkins credentialsId for Cloud API key/secret (default: "confluentCloudApiKey")
 *   apiBase        - API base (default: "https://api.confluent.cloud")
 */
def call(Map args = [:]) {
  final String tfvarsPath     = (args.tfvarsPath ?: '').trim()
  final String environmentId  = (args.environmentId ?: '').trim()
  final String credsId        = (args.credsId ?: 'confluentCloudApiKey').trim()
  final String apiBase        = (args.apiBase ?: 'https://api.confluent.cloud').trim()

  if (!tfvarsPath)    error "convertKafkaClusterNameToId: 'tfvarsPath' is required"
  if (!environmentId) error "convertKafkaClusterNameToId: 'environmentId' is required"
  if (!fileExists(tfvarsPath)) error "convertKafkaClusterNameToId: tfvars not found: ${tfvarsPath}"

  String content = readFile(tfvarsPath)

  // If there is already an ID, return it
  String existingId = extractValue(content, 'kafka_cluster_id')
  if (existingId?.startsWith('lkc-')) {
    echo "convertKafkaClusterNameToId: found existing kafka_cluster_id='${existingId}', skipping API lookup."
    return [clusterId: existingId]
  }

  // Otherwise read the name
  String clusterName = extractValue(content, 'kafka_cluster_name')
  if (!clusterName) {
    def preview = content.readLines().findAll { it?.trim() }.take(30).join('\n')
    error """convertKafkaClusterNameToId: Could not find kafka_cluster_name in ${tfvarsPath}.
Preview:
${preview}
"""
  }
  echo "convertKafkaClusterNameToId: kafka_cluster_name='${clusterName}'"

  // Call Confluent Cloud API (Cloud API key/secret required)
  String encodedName = URLEncoder.encode(clusterName, 'UTF-8')
  String url = "${apiBase}/cmk/v2/clusters?environment=${environmentId}&spec.display_name=${encodedName}&page_size=100"
  echo "convertKafkaClusterNameToId: querying Confluent API (env=${environmentId})"

  withCredentials([usernamePassword(credentialsId: credsId,
                                    usernameVariable: 'CONFLUENT_API_KEY',
                                    passwordVariable: 'CONFLUENT_API_SECRET')]) {
    String result = withEnv(["CLOUD_URL=${url}"]) {
      sh(
        script: '''
          set -e
          tmpfile="$(mktemp)"
          code="$(curl -sS -u "$CONFLUENT_API_KEY:$CONFLUENT_API_SECRET" \
                     -H 'Accept: application/json' \
                     -o "$tmpfile" -w '%{http_code}' \
                     "$CLOUD_URL" || true)"
          echo "HTTP_CODE=$code"
          echo "BODY_START"
          cat "$tmpfile"
          echo "BODY_END"
          rm -f "$tmpfile"
        ''',
        returnStdout: true
      ).trim()
    }

    // Parse status and body safely
    def codeMatch = (result =~ /HTTP_CODE=(\d+)/)
    if (!codeMatch.find()) error "convertKafkaClusterNameToId: Unable to parse HTTP code. Raw:\n${result}"
    String httpCode = codeMatch.group(1)

    String body = result.replaceAll("(?s).*BODY_START\\n", "")
                        .replaceAll("(?s)\\nBODY_END.*", "")

    if (httpCode != '200') {
      if (['401','403'].contains(httpCode)) {
        error """convertKafkaClusterNameToId: API returned ${httpCode}.
Likely causes:
  • Using a Kafka API key instead of a Cloud API key
  • Key not scoped to environment '${environmentId}'
  • Service account lacks EnvironmentAdmin/CloudClusterAdmin
Body:
${body}"""
      } else if (httpCode == '404') {
        error "convertKafkaClusterNameToId: 404 from API. Ensure base URL is https://api.confluent.cloud and env '${environmentId}' is correct.\nBody:\n${body}"
      } else {
        error "convertKafkaClusterNameToId: Confluent API error (HTTP ${httpCode}).\nBody:\n${body}"
      }
    }

    def json = new JsonSlurper().parseText(body ?: '{}')
    def cluster = json?.data?.find { it?.spec?.display_name == clusterName }
    if (!cluster) error "convertKafkaClusterNameToId: cluster '${clusterName}' not found in environment '${environmentId}'."

    String clusterId = cluster.id as String
    echo "convertKafkaClusterNameToId: resolved '${clusterName}' -> '${clusterId}'"
    return [clusterId: clusterId]
  }
}

/**
 * Robust tfvars parser for a single key:
 * - accepts double quotes, single quotes, or bare values
 * - ignores trailing comments (# or //)
 * - tolerant of spaces and tabs
 */
@NonCPS
private String extractValue(String content, String key) {
  for (String rawLine : content.readLines()) {
    String line = rawLine?.trim()
    if (!line) continue
    // strip comments
    int sharp = line.indexOf('#'); if (sharp >= 0) line = line.substring(0, sharp).trim()
    int sl   = line.indexOf('//'); if (sl   >= 0) line = line.substring(0, sl).trim()
    if (!line) continue

    // match "key = value"
    def eq = line.indexOf('=')
    if (eq < 0) continue
    String k = line.substring(0, eq).trim()
    if (k != key) continue

    String v = line.substring(eq + 1).trim()
    if (!v) return null
    // drop surrounding quotes if present
    if ((v.startsWith('"') && v.endsWith('"')) || (v.startsWith("'") && v.endsWith("'"))) {
      v = v.substring(1, v.length() - 1)
    }
    return v.trim()
  }
  return null
}
