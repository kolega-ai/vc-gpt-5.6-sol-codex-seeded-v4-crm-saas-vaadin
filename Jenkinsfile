pipeline {
  agent any
  tools { jdk 'jdk-21' }
  stages {
    stage('Verify wrapper') { steps { sh './gradlew --version' } }
    stage('Build and test') { steps { sh './gradlew clean test bootJar --no-daemon' } }
  }
  post {
    always {
      junit allowEmptyResults: false, testResults: 'build/test-results/test/*.xml'
      jacoco execPattern: 'build/jacoco/test.exec', classPattern: 'build/classes/java/main', sourcePattern: 'src/main/java'
      archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true, allowEmptyArchive: true
    }
  }
}
