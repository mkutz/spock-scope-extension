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

        stage("Collect Results") {
            steps {
                junit "**/target/surefire-reports/TEST-*.xml"
                archive "spock-scope-extension/target/*.jar"
            }
        }

    }
}
