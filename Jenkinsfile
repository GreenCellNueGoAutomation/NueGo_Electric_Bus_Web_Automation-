pipeline {
    agent any

    tools {
        jdk 'JDK-17'
        maven 'Maven-3.9.9'
    }

    environment {
        GIT_URL = 'https://github.com/sumedhsonawane-cell/NueGoAutomations.git'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: "${GIT_URL}"
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile -B'
            }
        }

        stage('Run Tests') {
            steps {
                bat 'mvn test -B'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'target/**/*.html, target/**/*.xml, target/screenshots/**/*.png', allowEmptyArchive: true
            }
        }
    }

    post {
        success { echo '✅ Build SUCCESS' }
        failure { echo '❌ Build FAILED' }
    }
}
