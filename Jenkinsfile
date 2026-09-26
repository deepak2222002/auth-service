pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t auth-service .'
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'db-remoteuser',
                    usernameVariable: 'DB_USERNAME',
                    passwordVariable: 'DB_PASSWORD'
                )]) {
                    sh '''
                        docker stop auth-service || true
                        docker rm auth-service || true

                        docker run -d \
                        --name auth-service \
                        --network backend_default \
                        --restart unless-stopped \
                        -p 8091:8443 \
                        -e DB_URL="jdbc:sqlserver://sqlserver:1433;databaseName=jobportal;trustServerCertificate=true" \
                        -e DB_USERNAME="$DB_USERNAME" \
                        -e DB_PASSWORD="$DB_PASSWORD" \
                        -e KAFKA_BOOTSTRAP_SERVERS="kafka:9092" \
                        auth-service
                    '''
                }
            }
        }

    }
}