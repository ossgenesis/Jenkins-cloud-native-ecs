// vars/checkAndModifyConnectors.groovy

def call(String devTfvarsPath, String devStaticTfvarsPath) {
    def devTfvarsContent = readFile(devTfvarsPath)
    def devStaticTfvarsContent = readFile(devStaticTfvarsPath)
    
    def sinkConnectorsCommented = devTfvarsContent.contains("#sink_connectors")
    def sinkConnectorsNotFound = !devTfvarsContent.contains("sink_connectors")
    def sinkConnectorsStaticExists = devStaticTfvarsContent.contains("sink_connectors_static")

    def sourceConnectorsCommented = devTfvarsContent.contains("#source_connectors")
    def sourceConnectorsNotFound = !devTfvarsContent.contains("source_connectors")
    def sourceConnectorsStaticExists = devStaticTfvarsContent.contains("source_connectors_static")

    if ((sinkConnectorsCommented || sinkConnectorsNotFound) && sinkConnectorsStaticExists) {
        echo "sink_connectors are commented out or not found. Removing sink_connectors_static from ${devStaticTfvarsPath}."
        devStaticTfvarsContent = devStaticTfvarsContent.replaceAll(/(?s)sink_connectors_static\s*=\s*\[[^\]]*\]/, "")
        writeFile(file: devStaticTfvarsPath, text: devStaticTfvarsContent)
        return true
    }

    if ((sourceConnectorsCommented || sourceConnectorsNotFound) && sourceConnectorsStaticExists) {
        echo "source_connectors are commented out or not found. Removing source_connectors_static from ${devStaticTfvarsPath}."
        devStaticTfvarsContent = devStaticTfvarsContent.replaceAll(/(?s)source_connectors_static\s*=\s*\[[^\]]*\]/, "")
        writeFile(file: devStaticTfvarsPath, text: devStaticTfvarsContent)
        return true
    }
    
    return !sinkConnectorsStaticExists || !sourceConnectorsStaticExists
}
