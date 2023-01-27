pipeline {
        agent any
        tools{
            maven 'maven-3.8.7'
        }
        stages {
            stage('package') {
                steps {
                    sh 'mvn clean package'
                }
            }
            stage('deploy') {
                steps {
                    sh 'docker-compose -f /var/jenkins_home/docker-spark-cluster-master up -d'
                    sh 'docker cp /var/jenkins_home/workspace/spark-job/target/target/spark-demo-1.0-SNAPSHOT-jar-with-dependencies.jar docker-spark-cluster-master_spark-master_1:/opt/spark'
                    sh 'docker exec -it docker-spark-cluster-master_spark-master_1 /bin/bash -c "./bin/spark-submit --class org.example.StreamingJob --master spark://localhost:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0 spark-demo-1.0-SNAPSHOT-jar-with-dependencies.jar”'

                }
            }
        }
}
