def call(section) {
    //configFileName = "config.json"
    configFileName = "getJenkinsAssumeRole.json"
    writeFile file: configFileName, text: libraryResource(configFileName)
    def configMap = readJSON file: configFileName
    def configSection = configMap[section]
    return configSection[env.BRANCH_NAME]
}
