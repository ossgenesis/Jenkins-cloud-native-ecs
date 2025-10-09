import groovy.json.JsonSlurper

def call(String clusterName, String environmentId) {
    withCredentials([usernamePassword(credentialsId: 'confluentCloudApiKey', usernameVariable: 'CONFLUENT_API_KEY', passwordVariable: 'CONFLUENT_API_SECRET')]) {
        def apiUrl = "https://api.confluent.cloud/cmk/v2/clusters?environment=${environmentId}"

        // Use curl to securely retrieve data from the API
        def response = sh(script: """
            curl -s -u "\${CONFLUENT_API_KEY}:\${CONFLUENT_API_SECRET}" "${apiUrl}"
        """, returnStdout: true).trim()

        // Parse the JSON response
        def jsonResponse = new JsonSlurper().parseText(response)
        def clusters = jsonResponse.data

        // Find the cluster by display name
        def cluster = clusters.find { it.spec.display_name == clusterName }

        if (cluster) {
            def clusterId = cluster.id
            def kafkaBootstrapEndpoint = cluster.spec.kafka_bootstrap_endpoint
            def httpEndpoint = cluster.spec.http_endpoint
            echo "Cluster found: ${cluster.spec.display_name} with ID ${clusterId}, Kafka Bootstrap Endpoint ${kafkaBootstrapEndpoint}, and HTTP Endpoint ${httpEndpoint}"

            // Return clusterId, kafkaBootstrapEndpoint, and httpEndpoint
            return [clusterId: clusterId, kafkaBootstrapEndpoint: kafkaBootstrapEndpoint, httpEndpoint: httpEndpoint]
        } else {
            error "Cluster name '${clusterName}' not found in environment '${environmentId}'."
        }
    }
}
