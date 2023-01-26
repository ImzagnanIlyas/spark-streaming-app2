pipeline {
    agent any

    stages {
        stage('Header_1') {
            steps {
                echo '1'
            }
        }
        stage('Header_2') {
            steps {
                echo '2'
            }
        }
        
        stage('Parallel') { // add this
            parallel {
                stage('First Parallel Stage') {
                    environment {
                        TEST = 3
                    }
                    
                    steps {
                        echo "$TEST"
                    }
                }
                
                stage('Execute this together') { // add this
                    stages {
                        stage('Another_One') {
                            steps {
                                echo "4"
                            }
                        }
                        
                        stage('Yet Another_One') {
                            steps {
                                echo "5"
                            }
                        }
                    }
                }
            }
        }
    }
}
