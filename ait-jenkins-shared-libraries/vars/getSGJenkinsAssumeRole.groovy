def call(section) {
    //configFileName = "config.json"
    configFileName = "getSGJenkinsAssumeRole.json"
    writeFile file: configFileName, text: libraryResource(configFileName)
    def configMap = readJSON file: configFileName
    def configSection = configMap[section]
    return configSection[env.BRANCH_NAME]
}
