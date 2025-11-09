pipeline {
    agent any

    environment {
        REPORT_DIR = "${WORKSPACE}/target/ExtentReports"
        EMAIL_RECIPIENTS = "sumedhsonwane19@gmail.com,sumedhsonwane18@gmail.com"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: '<your_repo_url>'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Publish Report') {
            steps {
                publishHTML(target: [
                    reportDir: 'target/ExtentReports',
                    reportFiles: 'NueGo_Report.html',
                    reportName: 'Extent Report'
                ])
            }
        }
    }

    post {
        always {
            emailext(
                to: "${EMAIL_RECIPIENTS}",
                subject: "Selenium Test Report - Build ${env.BUILD_NUMBER}",
                body: "Please find attached the latest Selenium test report.",
                attachFiles: "${REPORT_DIR}/NueGo_Report.html"
            )
        }
    }
}
