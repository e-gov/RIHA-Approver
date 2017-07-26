# Build process

## Prerequisites
These prerequisites are not strict but reflect an actual build environment:

 - Ubuntu 16.04.2 LTS
 - OpenJDK Java 1.8.0_131
 - Apache Maven 3.3.9

 ## Package
 ```mvn clean package```

# Deployment process

During build step, an Spring Boot executable jar should have been produced. Jar contains Spring Boot application that can be easily started as Unix/Linux service using either `init.d` or `systemd`. For complete documentation please see [Installing Spring Boot applications](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-install).

### Install as a service
Install as a `init.d` service
~~~bash
sudo ln -s /var/riha-approvertarget/riha-approver.jar /etc/init.d/riha-approver
~~~
Here `riha-approver` will become a service name that will be used system wide.

Configure automatic start (optional)
~~~bash
sudo update-rc.d riha-approver defaults <priority>
~~~

Secure jar file and restrict its modification (optional)
~~~bash
sudo chmod 500 riha-approver.jar
sudo chattr +i riha-approver.jar
~~~

#### Run
Service will be executed as a user that owns the jar file even when started automatically at boot. Please make sure to change jar file owner to anything but `root`

Start application service. **Note!** Service will be started as the user that owns the jar file
~~~bash
sudo service riha-approver start
~~~

#### Logs
Logs are written to `/var/log/riha-approver.log`

#### PID file
Application`s PID is tracked using `/var/run/riha-approver/riha-approver.pid`

#### Init script configuration
It is possible to configure init script without modifying jar file. Script can be customized by creating configuration file is expected to be placed next to the jar file with same name as jar but suffixed with `.conf`. For example if jar path is `/var/riha-approver/target/riha-approver.jar`, then configuration should be placed to `/var/riha-approver/target/riha-approver.conf` file.

### Application configuration
Sensible application default properties are packaged inside jar with name `application.properties`.

Spring Boot provides numerous ways to configure application like command line arguments, configuration JSON embedded in an environment variable or system property, JNDI attributes, etc. One of the easiest ways to configure Spring Boot application is to place configuration file named `application.properties` next to the jar file.

Please refer to Spring Boot [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config) documentation for more information.
