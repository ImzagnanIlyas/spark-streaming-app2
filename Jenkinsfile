pipeline {
        agent any
        tools{
            maven 'maven-3.8.7'
        }
        stages {
            stage('package') {
                steps {
                    echo '1'
                    sh 'mvn package'

                }
            }
            stage('Header_2') {
                steps {
                    echo '2'
                }
            }
        }
}
