#!groovy

pipeline {
    agent any
    String mvnHome = tool "Maven"
    stages {
        stage("Build") {
            steps {
                sh "cd spock-scope-extension && ${mvnHome}/bin/mvn package"
            }
        }
        stage("Collect Results") {
            steps {
                junit "**/target/surefire-reports/TEST-*.xml"
                archive "target/*.jar"
            }
        }
    }
}
