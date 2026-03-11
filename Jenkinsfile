pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'cd RestBasics'
                sh 'pwd'
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -pl RestBasics test -Dsurefire.suiteXmlFiles=RestBasics/testng.xml'
            }
        }
    }
}
