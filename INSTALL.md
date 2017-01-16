## Before install

 * Install karma for JS unit tests
 ```
 npm install karma-jasmine karma-junit-reporter karma-jasmine-jquery karma-jasmine-ajax karma-coverage karma-phantomjs2-launcher jasmine-core
 ```
 * Install Java 8

 ## Package
 ```mvn clean package```

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

## Specifics
### Ubuntu
 * Ubuntu 14.04 will have problems regarding node: the version from the main repo is too old to work properly
 Try this:
 
```
sudo apt-get install nodejs
sudo apt-get install npm
sudo apt-get install maven
sudo ln -s /usr/bin/nodejs /usr/bin/node
sudo npm install -g npm

# This is important!
sudo npm install -g npm
sudo npm install karma --save-dev
sudo npm install -g karma-cli

# This is not documented anywhere
sudo apt-get install libfontconfig
sudo npm install karma karma-jasmine jasmine

sudo npm install karma-jasmine karma-junit-reporter karma-jasmine-jquery karma-jasmine-ajax karma-coverage karma-phantomjs2-launcher

sudo apt-get install openjdk-8-jdk
```
