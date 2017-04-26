#!groovy

pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh "cd spock-scope-extension"
                sh "mvn package"
            }
        }
    }
    post {
        always {
            junit "**/target/TEST-*.xml"
        }
    }
}
