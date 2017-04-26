#!groovy

pipeline {
    agent any
    stages {
        stage("Build") {
            steps {
                sh "cd spock-scope-extension && ${tool "Maven"}/bin/mvn package"
            }
        }
        stage("Collect Results") {
            junit "**/target/surefire-reports/TEST-*.xml"
            archive "target/*.jar"
        }
    }
}
