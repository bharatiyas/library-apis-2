pipeline {

    agent any

    tools {
        maven "Maven"
    }

    stages {

        stage("Build a branch") {
            steps {
                sh "mvn clean package"
            }
            post {
                always {
                    junit '**/target/surefire-reports/**/*.xml'
                }
            }
        }

        stage("Release to maven repository") {
            when { branch 'develop' }
            steps {
                sh "mvn -B release:prepare"
                sh "mvn -B release:perform"
            }
        }

        stage("Deploy to Test") {
            // When any feature branch
            when { not { anyOf { branch 'develop'; branch 'master' } } }
            steps {
                // TBD: Deploy to Test steps
            }
        }

        stage("Deploy to Stage") {
            when {  branch 'develop' }
            steps {
                // TBD: Deploy to Stage steps
            }
        }

        stage("Deploy to Production") {
            when { branch 'master' }
            steps {
                // TBD: Deploy to Production steps
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}