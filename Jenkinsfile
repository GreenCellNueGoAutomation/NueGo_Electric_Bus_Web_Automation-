pipeline {
    agent any

    environment {
        REPORT_DIR = "${WORKSPACE}/target/ExtentReports"
        EMAIL_RECIPIENTS = "sumedhsonwane19@gmail.com,sumedhsonwane18@gmail.com"
    }

    stages {
        stage('Checkout') {
            steps {
                // Pull code from GitHub
                git branch: 'main', url: 'https://github.com/GreenCellNueGoAutomation/NueGo_Electric_Bus_Web_Automation-.git'
            }
        }

        stage('Build & Test') {
            steps {
                // Run Maven tests
                sh 'mvn clean test'
            }
        }

        stage('Publish Report') {
            steps {
                // Publish ExtentReport inside Jenkins
                publishHTML(target: [
                    reportDir: 'target/ExtentReports',
                    reportFiles: 'NueGo_Report.html',
                    reportName: 'Extent Report',
                    keepAll: true,
                    allowMissing: false
                ])
            }
        }
    }

    post {
        always {
            // Send email with report attached
            emailext(
                to: "${EMAIL_RECIPIENTS}",
                subject: "Selenium Test Report - Build ${env.BUILD_NUMBER}",
                body: """<p>Hello,</p>
                        <p>Please find attached the latest Selenium test report for build ${env.BUILD_NUMBER}.</p>
                        <p>Thanks,</p>""",
                mimeType: 'text/html',
                attachmentsPattern: 'target/ExtentReports/NueGo_Report.html'
            )
        }
    }
}
