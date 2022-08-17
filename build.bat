rem Workaround for hung / out of memory issues when building 1.19 and 1.19.1 fabric in same process
gradlew.bat bp-fabric:1-19:build
gradlew.bat bp-fabric:1-19-1:build
gradlew.bat build
