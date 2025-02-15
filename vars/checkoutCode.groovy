#def call(String repoUrl, String branch){
#   def workingDir = "${env.WORKSPACE}"
#   sh "git clone ${repoUrl} ${workingDir}"
#   sh "git checkout ${branch}"
#   return workingDir
#}


def call(Map config = [:]) {
    def repoUrl = config.repoUrl ?: error("Missing 'repoUrl'")
    def branch = config.branch ?: 'main'
    def credentialsId = config.credentialsId ?: 'github-token'

    checkout([
        $class: 'GitSCM',
        branches: [[name: "*/${branch}"]],
        doGenerateSubmoduleConfigurations: false,
        extensions: [],
        submoduleCfg: [],
        userRemoteConfigs: [[
            url: repoUrl,
            credentialsId: credentialsId
        ]]
    ])
}
