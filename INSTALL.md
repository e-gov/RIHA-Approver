## Before install

 * Install karma for JS unit tests
 ```
 npm install karma-jasmine karma-junit-reporter karma-jasmine-jquery karma-jasmine-ajax karma-coverage karma-phantomjs2-launcher
 ```
 * Install Java 8

 ## Package
 ```mvn package```

 ## Configure
Into application.properties file:
```
server.port=8080
infosystems.url=http://ec2-35-160-53-79.us-west-2.compute.amazonaws.com:8081/systems.json
```

 ## Run
 ```
 java -jar target/<generated jar> --spring.config.location=file://application.properties >> system.out 2>>system.err &
```
