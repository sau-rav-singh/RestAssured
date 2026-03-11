pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                dir('RestBasics') {
                    sh 'mvn clean install'
                }
            }
        }
        stage('Test') {
            steps {
                dir('RestBasics') {
                    sh 'mvn test -Dsurefire.suiteXmlFiles=testng.xml'
                }
            }
        }
    }
}
