def call(configFile, section, key) {
    def configMap = readJSON file: configFile
    def configSection = configMap[section]
    return configSection[key]
}