#!groovy

pipeline {
    agent any

    tools {
        maven "Maven"
    }

    stages {
        stage("Build") {
            steps {
                ansiColor("vga") {
                    sh "cd spock-scope-extension && mvn package"
                }
            }
        }
    }
    
    post {
        always {
            junit "**/target/surefire-reports/TEST-*.xml"
        }
        success {
            archive "spock-scope-extension/target/*.jar"
        }
    }
}
