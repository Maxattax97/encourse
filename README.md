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
 
 Once Docker is installed (it expects Linux containers), you can run `> docker-compose up` to start the database

### Spring

 Add the following properties to your `application-secret.properties`:

    local.postgres.username=
    local.postgres.password=
    spring.security.user.name=
    spring.security.user.password=
 
 You must make the postgres credentials here the same as the files created for the Postgres setup. The credentials afterwards are for any REST call made until authentication is finished.
