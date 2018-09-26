# encourse
 
## Server
 
### Installations on Ubuntu Server Edition
 
 Ensure that the following packages are installed and up to date:
 
 1. Java -    `apt-get install default-jdk`
 2. Maven -   `apt-get install maven`
 3. Node.js - `apt-get install nodejs`
 4. NPM -     `apt-get install npm`
 
### Commands to Serve Client Files
 
 In directory encourse/client, run the following commands:
 
 1. Create production build -    `npm run-script build`
 2. Begin serving on port 5000 - `serve -s build`
 
### Commands to Run Spring Server
 
 In directory encourse/server, run the following commands:
 
 1. Build and test maven project - `mvn clean package install`
 2. Run server on port 40797 -     `mvn spring-boot:run`
 
## Frontend
 Setup File Watcher :
  1. npm install -g node-sass

  2. In Webstorm by clicking File -> Settings -> Tools -> File Watchers

  3. Top right click the + sign

  4. Name : SCSS
  5. Program : node-sass
  6. Arguments : $FileName$ $ProjectFileDir$/client/src/css/$FileNameWithoutExtension$.css -r --output-style compact

## Backend

### Postgres

 Create files `pgdb_user.txt` and `pgdb_password.txt` that contain the admin credentials for the PostgreSQL database.  
 Once Docker is installed (it expects Linux containers) you can run `docker-compose up` to start the database  
 If you have issues with running Docker after multiple runs, you can run `./armaggedon.sh` to remove volumes and networks to start fresh. Note that this will make Docker re-download images for building and delete any saved data.


### Spring

 Add the following properties to your `application-secret.properties`:

    local.postgres.username=
    local.postgres.password=
    spring.security.user.name=
    spring.security.user.password=
 
 You must make the postgres credentials here the same as the files created for the Postgres setup. The credentials afterwards are for any REST call made until authentication is finished.
 
 If there are compile errors related to security or xml binding then try the following:  
  If you are running JDK 8, ensure that your distro has JAXB and remove the following lines from your `pom.xml`:  
  If you are running JDK 9, add the following lines to your `pom.xml`:  
  
    <dependency>
        <groupId>javax.xml.bind</groupId>
        <artifactId>jaxb-api</artifactId>
        <version>2.3.0</version>
    </dependency>
    <dependency>
        <groupId>com.sun.xml.bind</groupId>
        <artifactId>jaxb-core</artifactId>
        <version>2.3.0</version>
    </dependency>
    <dependency>
        <groupId>com.sun.xml.bind</groupId>
        <artifactId>jaxb-impl</artifactId>
        <version>2.3.0</version>
    </dependency>
    <dependency>
        <groupId>javax.activation</groupId>
        <artifactId>activation</artifactId>
        <version>1.1.1</version>
    </dependency>
    
 ### Authentication
 
 1. Hash a new password here: [BCrypt](https://www.dailycred.com/article/bcrypt-calculator)  
 2. Change the password to your account in `StartupFeed.java` 
    
