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
            steps {
                junit "**/target/surefire-reports/TEST-*.xml"
                archive "target/*.jar"
            }
        }
    }
}
