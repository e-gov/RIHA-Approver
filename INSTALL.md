## Before install
See the Ubuntu example for details
 * Install karma for JS unit tests
 * Install Java 8

**NB!** Make sure the internet is reachable using the git:// protocol (TCP on 9418), some of the karma packages attempt a github clone and fail silently, if network is not available

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

## Authentication and authorization


### Requirements

 * Apache server (needed to get the user's certificate string with person SSN)  
 * LDAP server (needed for authentication and authorization) 

### Process description

 1. When anonymous user tries to access any page that requires authentication / authorization (s)he is automatically redirected to login form. 
 2. There is a link (see ```idCardAuthUrl``` parameter) on the /login.html that redirects user to Apache server.
 3. Apache server asks for ID card PIN and if: 
   * entered PIN is correct: user's certificate is added to HTTP header (see ```personalCertificateHeaderField```) and the user is redirected to index.html page of the application
   * entered PIN is incorrect / PIN request cancelled: Apache server should display an error and authentication process stops
 4. In case there is a valid certificate present in the HTTP header, application tries to authenticate the user using LDAP. ```ldap.auth.userSearchBase``` 
 directory is queried using ```ldap.auth.userSearchFilter``` to determine the user and if: 
    * the user is found then the authorization process starts
    * the user is not present in LDAP directory the authentication process stops.
 5. Authorization process scans ```ldap.auth.groupSearchBase``` directory and determines all groups the user has membership in.
  Role names are constructed by prefixing ```ROLE_``` with uppercased value of  ```ldap.auth.groupRoleAttribute``` group attribute.
  In case the user is not present in any groups (s)he is granted with ```ROLE_USER``` role

### Apache server related configuration

All configuration takes place in ```src/main/resources/application.properties``` file

Related configuration parameters are:

 * ```personalCertificateHeaderField``` defines the HTTP header name that contains user's certificate (default value  ```SSL_CLIENT_S_DN```)
 * ```idCardAuthUrl``` this should point to properly configured Apache server (default value ```https://localhost/login```)


### LDAP server related configuration

All configuration takes place in ```src/main/resources/application.properties``` file
 
Related configuration parameters are:
  
  * ```ldap.host, ldap.port, ldap.login, ldap.password, ldap.baseDn``` those parameters define the user which will access LDAP server 
  * ```ldap.auth.userSearchBase``` defines the directory (relative to ldap.baseDn) where to search for users
  * ```ldap.auth.userSearchFilter``` defines how to search for users inside the ```userSearchBase``` directory (default value ```pager={0}```. Note that ```{0}``` will be substituted with person SSN number)  
  * ```ldap.auth.groupSearchBase``` defines the directory (relative to ldap.baseDn) where to search for user groups
  * ```ldap.auth.groupRoleAttribute``` defines the name of the group's attribute to construct user's role from



**NOTE!**  There is a sample LDAP.ldif file located in ```src/test/resources/test-server.ldif```