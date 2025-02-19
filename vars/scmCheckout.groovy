#!/usr/bin/env groovy

def call(Map params = [:]) {
    def repoUrl = params.get('repoUrl', '')
    def branch = params.get('branch', 'main')
    def credentialsId = params.get('credentialsId', '')
    def targetDir = params.get('targetDir', '')
    def scmType = params.get('scmType', 'git') // Default to Git
    def shallowClone = params.get('shallowClone', false)
    def depth = params.get('depth', 1)
    def refSpec = params.get('refSpec', '')

    if (!repoUrl) {
        error "SCM Checkout failed: Repository URL is required!"
    }

    echo "🔍 Checking out source code from ${repoUrl} (Branch: ${branch}) into ${targetDir ?: 'default workspace'}"

    try {
        if (scmType == 'git') {
            checkout([$class: 'GitSCM',
                branches: [[name: "refs/heads/${branch}"]],
                userRemoteConfigs: [[
                    url: repoUrl,
                    credentialsId: credentialsId,
                    refspec: refSpec ?: "+refs/heads/*:refs/remotes/origin/*"
                ]],
                extensions: [
                    [$class: 'RelativeTargetDirectory', relativeTargetDir: targetDir ?: '.'],
                    shallowClone ? [$class: 'CloneOption', depth: depth, noTags: true, shallow: true] : null
                ].findAll { it != null }
            ])
        } else if (scmType == 'svn') {
            checkout([$class: 'SubversionSCM',
                locations: [[remote: repoUrl, credentialsId: credentialsId]],
                workspaceUpdater: [$class: 'UpdateUpdater']
            ])
        } else {
            error "Unsupported SCM Type: ${scmType}"
        }

        echo "✅ Checkout completed successfully from ${repoUrl}"

    } catch (Exception e) {
        error "❌ SCM Checkout failed: ${e.getMessage()}"
    }
}

