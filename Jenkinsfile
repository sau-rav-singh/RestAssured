pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn -pl RestBasics test -Dsurefire.suiteXmlFiles=RestBasics/testng.xml'
            }
        }
    }
}
