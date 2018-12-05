Table of Contents
=================

   * [Introduction](#introduction)
   * [Server Setup](#server-setup)
      * [File Structure](#file-structure)
      * [Accessing Repositories](#accessing-repositories)
      * [Necessary Installations](#necessary-installations)
         * [Java Development Kit](#java-development-kit)
         * [Maven Package Manager](#maven-package-manager)
         * [Node.js Runtime Environment](#nodejs-runtime-environment)
         * [NPM Package Manager](#npm-package-manager)
         * [Postgresql Database](#postgresql-database)
         * [Other Installations](#other-installations)
      * [Setting up the Database](#setting-up-the-database)
      * [Setting up Screens](#setting-up-screens)
      * [Setting up the Spring Server](#setting-up-the-spring-server)
      * [Setting up the React Server](#setting-up-the-react-server)
      * [Port Redirection](#port-redirection)
      * [Testing Accounts](#testing-accounts)
   * [Using the Application](#using-the-application)
      * [Course Selection Page](#course-selection-page)
      * [Course Homepage](#course-homepage)
         * [Overview](#overview)
         * [Options](#options)
         * [Charts](#charts)
         * [Statistics](#statistics)
         * [Students Summary](#students-summary)
         * [Last Sync &amp; Last Test Ran](#last-sync--last-test-ran)
         * [Projects](#projects)
         * [Actions](#actions)
      * [Manage Teaching Assistants Page](#manage-teaching-assistants-page)
      * [Course Academic Dishonesty Report Page](#course-academic-dishonesty-report-page)
         * [Overview](#overview-1)
         * [Options](#options-1)
         * [Charts](#charts-1)
         * [Students Summary](#students-summary-1)
         * [Actions](#actions-1)
      * [Student Course Page](#student-course-page)
         * [Overview](#overview-2)
         * [Charts](#charts-2)
         * [Statistics](#statistics-1)
         * [Feedback](#feedback)
         * [Commit History](#commit-history)
         * [Projects](#projects-1)
         * [Actions](#actions-2)
      * [Student Course Academic Dishonesty Report Page](#student-course-academic-dishonesty-report-page)
         * [Overview](#overview-3)
         * [Charts](#charts-3)
         * [Last Sync &amp; Last Test Ran](#last-sync--last-test-ran-1)
         * [Actions](#actions-3)
      * [Preferences Page](#preferences-page)
         * [Overview](#overview-4)
         * [Actions](#actions-4)
      * [Administrator Role Management Page](#administrator-role-management-page)
      * [Administrator Course Management Page](#administrator-course-management-page)
   * [Understanding the Code](#understanding-the-code)
      * [Introduction](#introduction-1)
      * [Frontend Organization](#frontend-organization)
         * [Frontend File Tree](#frontend-file-tree)
      * [Backend Organization](#backend-organization)
         * [Java Overview](#java-overview)
            * [Java File Tree](#java-file-tree)
            * [Config Component](#config-component)
               * [Filter Configuration](#filter-configuration)
               * [Resource Configuration](#resource-configuration)
               * [Security Configuration](#security-configuration)
               * [Scheduling Configuration](#scheduling-configuration)
               * [Database Configuration and Initialization](#database-configuration-and-initialization)
            * [Controller Component](#controller-component)
            * [Database Component](#database-component)
            * [Domain Component](#domain-component)
            * [Service Component](#service-component)
            * [Util Component](#util-component)
         * [Bash Directory](#bash-directory)
         * [Python Directory](#python-directory)



# Introduction

The purpose of this manual is to explain to the developer how to set up, use, and continue with development of EnCourse. This manual will be split into three main sections, with the main focus being first on set up, then usage for the application, and finally an explanation on how to continue coding for the application. This is not meant to be a general user’s guide to the application, nor is it meant to be a setup guide for the application. While those topics will be covered in this guide, this document will go into far more detail about those topics than is required. For a user’s guide, see the help sections on the web interface. For a server setup guide, refer to the `README` on the [github repository](https://github.com/Killian-LeClainche/encourse). Instead, this guide is meant to explain every detail needed for a developer to understand and work with the application.

# Server Setup

This section will serve as an in depth guide for server setup, and specifically in the context of a virtual machine (VM) accessing data on Purdue’s server hosted on data.cs.purdue.edu. Parts of this guide may not apply in general for other universities, nor would it apply if the application is directly hosted on a university server. The differences that cannot be generalized are restricted to the bash scripts used by the application, so any changes that are needed to make the application fit other environments should be restricted to bash scripts. Otherwise, the Java, JavaScript, and Python code are already generalized and should not pose an issue for portability.

## File Structure

The first topic that shall be discussed for this section is file structure. All course repositories will use the path ``/sourcecontrol/{CourseID}/{Semester}/``, where `CourseID` is the university’s designated code for a course, for example `cs252`, while semester is the term and year that the course data pertains to, for example `Fall2018`. If a course has multiple semesters where the application is used, then it will have multiple subdirectories under `/sourcecontrol/{CourseID}/`, one for each semester. An example of a full path to course repositories will thus be `/sourcecontrol/cs252/Fall2018/`. Under this directory, every student in the course will have a directory containing all of the cloned git repositories for that student in the course. Additionally, there are `testcases`, `hidden_testcases`, and `makefiles` directories which contain scripts necessary for testing code. The testcases directory has a subdirectory for every project assigned in using the application, and within each of these directories are the visible test scripts uploaded by the professor. Similarly, `hidden_testcases` contains hidden scripts, ie scripts that do not have their results shown to students, for each product. The makefiles directory contains the Makefile for building each assigned project.

## Accessing Repositories

Next, proper setup needs to be done in order to automate cloning and pulling git repositories. Since the application does these operations over SSH, the account that is used to run the application should have SSH keys setup to access the server containing the .git files, so that a password is not required for SSH. Also, the scripts used for cloning and pulling, namely `cloneRepositories.sh` and `pullRepositories.sh` in the directory `encourse/server/src/main/bash/` should be changed to the username of the account being SSH’s into on the university server. This setup is required for the application to clone and pull repositories, however it may be desirable to manually initiate cloning and pulling the repositories, which could be done with simple shell scripts. Examples of such scripts can be found in the home directory `reed226` on the VM. 

## Necessary Installations

Encourse requires many software packages to run the React and Spring Servers. The following subsections detail the command necessary for installing each tool, the reason for its installation, and alternative packages, if any exist.

### Java Development Kit

The [Java Development Kit](https://www.oracle.com/technetwork/java/javase/overview/index.html) is required to run the Spring server. It can be installed using the following command:

```Bash
apt-get install default-jdk
```

Encourse was developed using Java 8, the most recent Java version with long term support. You may wish to upgrade to Java 11 at some point, since it will be the next version with long term support and may have features which are not present in Java 8. The product was developed using  `OpenJDK Runtime Environment (build 1.8.0_181-8u181-b13-0ubuntu0.16.04.1-b13)`.

### Maven Package Manager

[Maven](https://maven.apache.org/) is a dependency manager for Java, and is required to run the Spring Server. It can be installed using the following command:

```
apt-get install maven
```

Maven will manage all of the other packages needed for the backend server, including Spring, Apache Tomcat, and the Postgresql database management. Gradle is an alternative package manager that is compatible with Spring. The product was developed using  `Apache Maven 3.3.9`.

### Node.js Runtime Environment

The Encourse client is built and served using [nodejs](https://nodejs.org/en/). To install it, run the following command:

```
apt-get install nodejs
```

This environment is necessary for some of the dependencies used by the frontend code, so this installation is a requirement and has no alternatives. The product was developed using version `v10.11.0`.

### NPM Package Manager

The client also requires [npm](https://docs.npmjs.com/about-npm/), which can be installed using the following command:

```
apt-get install npm
```

This is the package manager used for the frontend JavaScript code, and will handle all of the other dependencies such as [React](https://reactjs.org/) and [ESLint](https://eslint.org/). [Yarn](https://yarnpkg.com/en/) is an alternative package manager that is compatible with React. The product was developed using version `6.4.1`.

### Postgresql Database

Encourse uses [Postgresql](https://www.postgresql.org/) for the database.  It can be installed using the following command:

```
apt-get install postgresql postgresql-contrib
```

This is the database that is used by the Spring server. It is an SQL database, so most other SQL databases could be used as alternatives, since many of the schemas and configurations will apply to SQL databases. You should not change to a NoSQL database since this may require significant changes in the schemas and configuration. The product was developed using `PostgreSQL 10.5`.

### Other Installations

As a final disclaimer, the nature of the application requires that the VM be setup to mimic the environment of the university’s server in order to accurately run the test cases. This may require additional installations not detailed in this documentation. For example, the standard C and C++ libraries may change with updates to the GNU compiler, and students may use some of the newer additions in their projects, so the compiler version should exactly match the version used by the university server.

Additionally, while version numbers were listed in this documentation, the latest compatible version of each dependency should be used.

## Setting up the Database

Before the backend server can start, Postgresql must be running and have ports open for the backend server to access. This can be done with;

```bash
sudo -u postgres psql
```

This  will start the database, if it is not already running, and open a terminal to query the database. If this is a fresh installation of Postgresql, you will need to create databases for the application:

```plsql
CREATE DATABASE encourse; 
CREATE DATABASE encourse_production;
```

 These create the testing and production databases respectively. The application also requires a user to access the database, so create an account for an existing Linux user with the following command:

```bash
sudo -u postgres createuser --interactive
```

Then enter the user’s name and make the user a superuser for the database. Once this is done, type `\q` to close the terminal. The database will continue to run in the background so that the backend server can access it.

## Setting up Screens

In order to run the Spring and React servers, screens should be set up to run the commands without needing to have an active SSH session. A screen can be created with the command `screen -S name`, where name is the desired name for the screen. The names currently used for the VM are:

- `production` - Runs the production Spring server
- `client` - Runs the production React server
- `testing` - Runs the test Spring server
- `test_client` - Runs the test React server

After creating a screen, type `Ctrl-A` and then `D` to detach the screen, meaning that it runs independently from the SSH client, and will continue running until the screen is exitted. Then, `screen -r` will list the available screens, and `screen -r name` will access the screen with a specific `name`. Always be sure to use `Ctrl-A` and then `D` after starting a server on a screen so that it can keep running after terminating the SSH session and is able to be accessed later by other developers.

## Setting up the Spring Server

First, some properties need to be defined for Spring in a file named application-secret.properties under the directory “encourse/server/src/main/resources/”. If the file does not already exist, create it. The following list are all of the properties that need to be defined, along with example or actual values to put there:

```Bash
# Database Credentials and Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/encourse
local.postgres.username={username for postgresql}
local.postgres.password={password for postgresql}

# SSL Certification Settings
encourse.ssl.key-store=classpath:encourse-keystore.p12
encourse.ssl.key-store-password={password for keystore}
encourse.ssl.key-alias=tomcat
encourse.ssl.key-store-type=PKCS1

# Email Cettings
local.smtp.host=smtp.gmail.com
local.smtp.username={username for email}
local.smtp.password={password for email}
```

Replace the phrases enclosed in braces`{}` with the developer's own account usernames and passwords.  Note that the default settings might need to be changed, for example set `sprint.datasource.url=jdbc:postgresql://localhost:5432/encourse_production` to use the production server, and` local.smtp.host` can be set to other email servers. 

Once the secret properties are set, the server can be built and tested by going to the `encourse/server/` directory. The following command will builds the Spring application and runs test cases, failing the build if any test case fails:

```bash
 mvn clean package install
```

The next command builds and starts the backend server:

```Bash
mvn spring-boot:run
```

We recommend that you only run this command on the screen named `production` for the production server, and `testing` for the test server, to avoid confusion. At this point, the backend server should now be running. 

* To access the test server, use the URL `vm2.cs.purdue.edu:18443`, which maps to port `443` on the VM. 

* To access the production server, use the URL `vm2.cs.purdue.edu:44389`, which maps to port `443` on the VM. We then redirect this to port `40797` so that the server can listen to the port without root privileges. 

For a different server, the available ports may be different, so redirect ports accordingly. These URLs should be placed in `encourse/client/src/server.js` so that the client can connect to the server.

## Setting up the React Server

The React project’s static files are also served from the VM. First, we need to install the application's dependencies, and start the client. For the testing client, use the screen `test_client`, and change directory to `encourse/client/`. Next, run the following commands:

```Bash
npm install
npm start
```

This client can be accessed from a web browser with the URL `vm2.cs.purdue.edu:18080`, which maps to port `8080` on the VM and then redirected to port `3000` which the client then uses. 

For the production client, use the screen `client`, change directory to `encourse/client/`, then run the following commands:

```Bash
npm install
npm run-script build
serve -s build
```

This will create a production client to serve, and begin serving the client files. This client can be accessed from a web browser with the URL `vm2.cs.purdue.edu:8089`, which maps to port `80` on the VM and then redirected to port `5000` which the client then uses. 

## Port Redirection

Use the following commands to properly redirect ports so that the production and testing servers for React and Spring are able to access the port:

```Bash
sudo iptables -A PREROUTING -t nat -p tcp --dport 80 -j REDIRECT --to-port 5000
sudo iptables -A PREROUTING -t nat -p tcp --dport 443 -j REDIRECT --to-port 40797
sudo iptables -A PREROUTING -t nat -p tcp --dport 8080 -j REDIRECT --to-port 3000
```

The first command maps port `80` to the port that the production React server listens to, the second maps port `443` to the port that the production Spring server listens to, and the final command maps port `8080` to the port that the testing React server listens to. A port does not need to be redirected for the Spring test server since it can directly listen to port `8443`.

## Testing Accounts

Each course that uses the application needs a course account and a course group, where the course group is the course identifier, such as `cs252`, and the course account is the group appended by `-account`, such as `cs252-account`. This is necessary since test scripts are run by the course account instead of the main account that runs the servers. This ensure that faulty test scripts can only cause damage to the projects made for that course, while protecting all other courses and users on the VM. Also note that the primary group of the course account should be the course group, since the group permission allows the course account to execute tests in the corresponding directory.

# Using the Application

## Course Selection Page

## Course Homepage

### Overview

The course page is designed to be the main navigation page for a given course. Therefore, it is expected that most time spent on the site will be on a course's homepage.

### Options

To access the course options, click the title named **<COURSE_NAME>** in the top center panel on the page. This will produce a modal that is fixed in the center of the screen. Inside the modal you are provided a few options that you can alter:

1. Student Repositories Directory: The location that all of the course projects will be located. For now, leave this entry blank because there is no guarantee that the value inside this will be validated.
2. Repositories Update Interval: The number of hours between automatic test execution. The default value is 12 hours.

To exit the options modal, click the X icon in the top right. If you would like to save your changes, click the bottom right checkmark icon.

### Charts

To view the course charts, scroll the center panel to the top. Just underneath the **<COURSE_NAME>** title will be a section titled **“Course Charts Summary”**. This is a list of the available anonymous chart data, which are provided to all teaching assistants and professors who have access to the course. The information is the aggregate of all students in the course rather than a subsection of the students. The following are descriptions of the current cumulative charts:

1. **Student Progress Bar Chart**: Every student has an associated progress metric for each project in a course. These progress metrics are the estimated grade of the student if they were to be graded at that moment. This chart is a collection of each student's progress metrics, placed into separate bins by percentage of project completion (for example, 20-40%). These bins are dynamically selected by the server and represented in the front-end. The x-axis contains the bins, which are [inclusive, exclusive) and increasing in value. The y-axis is the number of students that fall into each bin.
2. **Student Test Progress Bar Chart**: Similar to the Student Progress Bar Chart, the progress metrics are derived from the sum of each passed test, listed individually. This chart uses each test's aggregate (the number of students who passed that test) and displays them in separate bars for each test. The x-axis is the specific test, and the y-axis is the number of students that passed that test.

### Statistics

### Students Summary

To view the students summary, scroll the center panel past the **“Course Statistics”** to the section titled **“Students Summary"**. This is a collection of student preview cards that provide a quick overview of each student. The overview consists of the following:

1. **Student Name**: Every student in the database has a first and last name. We display this in the student previews to help professors and teaching assistants to associate the preview to a student. It is not necessary to provide a real set of student’s names. Instead, just provide any unique identifier for their first and last name and these will populate as indicated.
2. **Time**: Given the currently selected project, the server runs analysis on the server to estimate the time spent by a student on the project. This estimate is then displayed in the preview to identify who has spent large or small amounts of time. The value is displayed in hours
3. **Commits**: Given the currently selected project, the server pulls git information from that project's repository. The commit count should allow professors to quickly determine how many commits students have made, which may play a role in identifying academic dishonesty.
4. **Horizontal Bar**: This partially filled green bar indicates a student's progress on the current project. On the right side, one can also see that there includes a raw percentile.
5. **Selecting the Student Preview**: When hovering over any student preview card, there will be a checkbox input in the top right of the card. Clicking this checkbox will cause a checkmark to appear and the preview to turn blue. This means that the card is now selected. Selecting at least one student preview will make all student preview cards show their respective checkbox input in the top right.
6. **Clicking the Student Preview**: Clicking anywhere in the student preview card, except for the checkbox, will open that specific students summary page. When you hover over a student preview, a blue outline should appear.

###Filtering and Sorting

You can also use the filter and sort students using the interface just below the header **“Students Summary”**. 

* The first option is **“Sort by …”** in which you can sort students by their full name, time spent, commit count, or progress made for the current selected project. 
* The second option, **“... Order”**, allows you to sort the students in ascending or descending order. 
* For filtering, you can filter by commit count, time spent, or progress. 

### Last Sync & Last Test Ran

The **Last Sync** and **Last Test Ran** values float at the top right section of the screen. The  **Last Sync** value is the last time the server pulled updates to this course's project repositories. The **Last Test Ran** value is the last time when the server ran test cases on all students.

### Projects

All projects are located in the top left section of the screen. 

**Switch Projects** - click on any of the available project to display that project's data. 

**Add A New Project** - click the title section named **“Projects”** or the gear icon. This will open the project settings page for this particular course.

### Actions

All actions are located on the left side of the screen. It will be below the **“Projects”** section and titled **“Actions”**. Inside the course homepage action section will be four different actions:

1. **Manage Teaching Assistants**: Clicking this action will open the **Manage Teaching Assistants** page.
2. **Sync Project Repositories**: Clicking this action will manually tell the server to pull all the students repositories for the currently selected project.
3. **Run Project Tests**: Clicking this action will manually tell the server to run the test all on all the students repositories for the currently selected project.
4. **Academic Dishonesty Report**: Clicking this action will open the **Course Academic Dishonesty Report** page.

## Manage Teaching Assistants Page

## Course Academic Dishonesty Report Page

### Overview

### Options

N/A

### Charts

To view the course charts, scroll the center panel to the top. Just underneath the **<COURSE_NAME> -  Academic Dishonesty Report** title will be a section called **“Course Charts Summary”**. This a list of the available anonymous chart data, which are provided to all teaching assistants and professors who have access to the course. The information is the aggregate of all students in the course rather than a subsection of the students. The following is a list of charts and their descriptions:

1. **Student Similarity Scatter Plot**: N/A

### Students Summary

To view the students summary, scroll the center panel past the **“Course Statistics”** to the section titled **“Students Summary”**. This is a collection of student preview cards that provide a quick overview of each student. This overview is identical to the one on the main course project page.

### Actions

## Student Course Page

### Overview

### Charts

### Statistics

### Feedback

### Commit History

### Projects

All projects are located in the top left section of the screen, fixed there so that scrolling the center panel does not affect its location. Clicking any of the available projects will swap the current screen to utilize the associated data of the project. To add new projects, click the title section named “Projects” or the gear icon. This will open the project settings page for this particular course.

### Actions

## Student Course Academic Dishonesty Report Page

### Overview

### Charts

### Last Sync & Last Test Ran

### Actions

## Preferences Page

### Overview

### Actions

## Administrator Role Management Page

## Administrator Course Management Page





# Understanding the Code

## Introduction

To maintain the backend server code, a developer should have a solid understanding of Java, Python, and Bash. Likewise, JavaScript, CSS, and HTML are the main languages used on the frontend client. The frontend code, backend Java, Bash, and Python each utilize different coding styles, which will be documented below. As such, an explanation of the code will also be divided between these sections. 

**For the remainder of this documentation**:

* **Frontend code** refers to code beneath the directory `encourse/client/`

* **Backend code** refers to code beneath the directory `encourse/server/`

## Frontend Organization

### Frontend File Tree

Frontend Directory Tree:

```
client/src
├── components
│   ├── chart
│   ├── modal
│   │   └── util
│   ├── navigation
│   └── panel
│       ├── admin
│       ├── common
│       ├── course
│       │   └── chart
│       ├── course-dishonesty
│       │   └── chart
│       ├── manage-ta
│       ├── preference
│       ├── project
│       ├── student
│       │   └── chart
│       └── student-dishonesty
│           └── chart
├── redux
│   ├── actions
│   ├── reducers
│   ├── retrievals
│   └── state-peekers
├── resources
├── styles
│   ├── css
│   └── scss
│       ├── base-scss
│       ├── login-scss
│       └── main-scss
│           ├── main
│           ├── navigation
│           └── panel
│               └── util
└── tests
    ├── int
    └── util
```

Expanded File Tree:

```
client/src
├── components
│   ├── App.js
│   ├── ChangePassword.js
│   ├── chart
│   │   └── chartUtils.js
│   ├── Helpers.js
│   ├── Login.js
│   ├── Main.js
│   ├── modal
│   │   ├── CourseDishonestyModal.js
│   │   ├── Modal.js
│   │   └── util
│   │       └── TestScriptList.js
│   ├── navigation
│   │   ├── ActionNavigation.js
│   │   ├── ProjectNavigation.js
│   │   ├── TANavigation.js
│   │   └── TopNavigation.js
│   └── panel
│       ├── admin
│       │   ├── AccountPreview.js
│       │   └── CoursePreview.js
│       ├── AdminPanel.js
│       ├── common
│       │   ├── CommitHistory.js
│       │   ├── HistoryText.js
│       │   ├── PreviewCard.js
│       │   ├── SelectableCardSummary.js
│       │   ├── ShareReportModal.js
│       │   ├── Statistics.js
│       │   └── TaskModal.js
│       ├── course
│       │   ├── chart
│       │   │   ├── CompletionProgress.js
│       │   │   ├── CustomTooltipContent.js
│       │   │   ├── index.js
│       │   │   └── TestCaseProgress.js
│       │   ├── CourseAnonCharts.js
│       │   ├── CourseCharts.js
│       │   ├── CourseModal.js
│       │   ├── CourseStatistics.js
│       │   ├── index.js
│       │   ├── StudentFilter.js
│       │   └── StudentSummary.js
│       ├── course-dishonesty
│       │   ├── chart
│       │   │   ├── CourseIdenticalLines.js
│       │   │   └── CustomTooltipContent.js
│       │   ├── CourseDishonestyCharts.js
│       │   ├── StudentReportFilter.js
│       │   └── StudentReportSummary.js
│       ├── CourseDishonestyPanel.js
│       ├── CoursePanel.js
│       ├── course-selection
│       │   ├── CoursesFilter.js
│       │   └── CoursesSummary.js
│       ├── CourseSelectionPanel.js
│       ├── index.js
│       ├── manage-ta
│       │   ├── SectionPreview.js
│       │   └── StudentAssignPreview.js
│       ├── ManageTAPanel.js
│       ├── preference
│       │   └── ChangePasswordModal.js
│       ├── PreferencePanel.js
│       ├── project
│       │   ├── index.js
│       │   ├── ProjectInfo.js
│       │   ├── ProjectModal.js
│       │   ├── ProjectTestFilter.js
│       │   ├── ProjectTestModal.js
│       │   └── ProjectTestSummary.js
│       ├── ProjectPanel.js
│       ├── student
│       │   ├── chart
│       │   │   ├── CodeChanges.js
│       │   │   ├── CommitFrequency.js
│       │   │   └── StudentProgress.js
│       │   ├── index.js
│       │   ├── StudentCharts.js
│       │   ├── StudentCommitHistory.js
│       │   └── StudentStatistics.js
│       ├── student-dishonesty
│       │   ├── chart
│       │   │   ├── StudentVelocityPerCommit.js
│       │   │   └── StudentVelocityPerTime.js
│       │   └── StudentDishonestyCharts.js
│       ├── StudentDishonestyPanel.js
│       └── StudentPanel.js
├── defaults.js
├── fuzz.js
├── index.js
├── redux
│   ├── actions
│   │   ├── admin.js
│   │   ├── auth.js
│   │   ├── control.js
│   │   ├── course.js
│   │   ├── fetch.js
│   │   ├── index.js
│   │   ├── projects.js
│   │   ├── student.js
│   │   └── teaching_assistant.js
│   ├── reducers
│   │   ├── admin.js
│   │   ├── auth.js
│   │   ├── control.js
│   │   ├── course.js
│   │   ├── index.js
│   │   ├── projects.js
│   │   ├── reducer-utils.js
│   │   ├── student.js
│   │   └── teaching_assistant.js
│   ├── refreshMiddleware.js
│   ├── retrievals
│   │   ├── course.js
│   │   ├── retrieval-utils.js
│   │   └── student.js
│   ├── state-peekers
│   │   ├── admin.js
│   │   ├── control.js
│   │   ├── course.js
│   │   ├── projects.js
│   │   └── student.js
│   └── store.js
├── registerServiceWorker.js
├── resources
│   ├── back.svg
│   ├── checkmark.svg
│   ├── encourse-logo-large.png
│   ├── loading.svg
│   ├── logout.svg
│   ├── plus.svg
│   ├── search.svg
│   ├── settings.svg
│   ├── sync.svg
│   ├── trash.svg
│   └── x.svg
├── server.example.js
├── server.js
├── styles
│   ├── css
│   │   ├── base.css
│   │   ├── login.css
│   │   └── main.css
│   └── scss
│       ├── base-scss
│       │   └── base.scss
│       ├── login-scss
│       │   └── login.scss
│       └── main-scss
│           ├── _definitions.scss
│           ├── _helpers.scss
│           ├── main
│           │   ├── _action.scss
│           │   ├── _breakline.scss
│           │   ├── _card.scss
│           │   ├── _chart.scss
│           │   ├── _checkbox.scss
│           │   ├── _dropdown.scss
│           │   ├── _exit.scss
│           │   ├── _filter.scss
│           │   ├── _image.scss
│           │   ├── _list.scss
│           │   ├── _modal.scss
│           │   ├── _panel.scss
│           │   ├── _progress-bar.scss
│           │   ├── _summary.scss
│           │   └── _title.scss
│           ├── main.scss
│           ├── navigation
│           │   ├── _actions-nav.scss
│           │   ├── _list-nav.scss
│           │   └── _nav.scss
│           └── panel
│               ├── _course.scss
│               ├── _preferences.scss
│               ├── _project.scss
│               ├── _student.scss
│               └── util
│                   ├── _commit-history.scss
│                   ├── _student-preview.scss
│                   └── _test-scripts.scss
└── tests
    ├── int
    │   └── ClassProgressHistogram.int.test.js
    └── util
        └── reduxTestUtils.js
```

## Backend Organization

### Java Overview

Since the backend server is built using Spring, which is a Java framework, most of the Java code is based heavily around Spring conventions. Most of the conventions used in this application are those seen in typical Spring applications, so for a more in depth guide about developing for a Spring project, see the [Spring documentation](https://spring.io/docs). For this manual, the focus will be on quick guidelines to continue development for the Java code, and will be split into explanations for each subcomponent in the Java code. The file tree for the Java components is shown starting on the next page. Each component subdirectory will then be explained in detail.



#### Java File Tree

Java Directory Tree:

```
server/src/main/java/edu/purdue/cs/encourse
├── config
├── controller
├── database
├── domain
│   └── relations
├── service
│   ├── helper
│   └── impl
└── util
```

Expanded File Tree:

```
server/src/main/java/edu/purdue/cs/encourse
├── EncourseApplication.java
├── config
│   ├── AuthServerOAuth2Config.java
│   ├── CORSFilter.java
│   ├── Encoders.java
│   ├── HibernateConfig.java
│   ├── OptionFilter.java
│   ├── ResourceServerConfig.java
│   ├── SSLConfig.java
│   ├── Scheduler.java
│   ├── SchedulingConfig.java
│   ├── ServerSecurityConfig.java
│   └── StartupFeed.java
├── controller
│   ├── AuthController.java
│   ├── DeleteController.java
│   ├── ReadController.java
│   └── WriteController.java
├── database
│   ├── AccountRepository.java
│   ├── AdminRepository.java
│   ├── AuthorityRepository.java
│   ├── ProfessorCourseRepository.java
│   ├── ProfessorRepository.java
│   ├── ProjectRepository.java
│   ├── ProjectTestScriptRepository.java
│   ├── ReportRepository.java
│   ├── SectionRepository.java
│   ├── StudentProjectDateRepository.java
│   ├── StudentProjectRepository.java
│   ├── StudentProjectTestRepository.java
│   ├── StudentRepository.java
│   ├── StudentSectionRepository.java
│   ├── TeachingAssistantCourseRepository.java
│   ├── TeachingAssistantRepository.java
│   ├── TeachingAssistantSectionRepository.java
│   ├── TeachingAssistantStudentRepository.java
│   └── UserRepository.java
├── domain
│   ├── Account.java
│   ├── Authority.java
│   ├── CollegeAdmin.java
│   ├── Professor.java
│   ├── Project.java
│   ├── Report.java
│   ├── Section.java
│   ├── Student.java
│   ├── TeachingAssistant.java
│   ├── User.java
│   └── relations
│       ├── ProfessorCourse.java
│       ├── ProjectTestScript.java
│       ├── StudentProject.java
│       ├── StudentProjectDate.java
│       ├── StudentProjectTest.java
│       ├── StudentSection.java
│       ├── TeachingAssistantCourse.java
│       ├── TeachingAssistantSection.java
│       └── TeachingAssistantStudent.java
├── service
│   ├── AccountService.java
│   ├── AdminService.java
│   ├── CourseService.java
│   ├── EmailService.java
│   ├── HelperService.java
│   ├── ProfessorService.java
│   ├── ReportService.java
│   ├── TeachingAssistantService.java
│   ├── helper
│   │   ├── StreamGobbler.java
│   │   └── TestExecuter.java
│   └── impl
│       ├── AccountServiceImpl.java
│       ├── AdminServiceImpl.java
│       ├── CourseServiceImpl.java
│       ├── EmailServiceImpl.java
│       ├── HelperServiceImpl.java
│       ├── ProfessorServiceImpl.java
│       ├── ReportServiceImpl.java
│       ├── TeachingAssistantServiceImpl.java
│       └── UserDetailsServiceImpl.java
└── util
    ├── ConfigurationManager.java
    └── JSONReturnable.java
```

#### Config Component

The config component refers to all Java interfaces beneath `encourse/server/src/main/java/edu/purdue/cs/encourse/config`. These classes have no object-orient relationships, they are simply used to set up to Spring server. Each class will thus be discussed in further detail:

##### Filter Configuration

This configuration is set up by two files, `CORSFilter.java` and `OptionFilter.java`. The first class handles cross-origin resource sharing which allows React to call Spring despite having a different domain. This is required for most modern browsers including Google Chrome. The second class handles `HTTP OPTION` request types as React will send an `OPTION` request before every call to the API and expects certain HTTP headers to exist in the response.

##### Resource Configuration

There are two virtual servers managed by Spring in this application: resource and security. The file `ResourceServerConfig.java` specifies which API endpoints can be accessed after authenticating with the security server. Currently, all endpoints containing `/api/**` will expect an authenticated user or they will filter out the unauthenticated request and return an HTTP Forbidden status.

##### Security Configuration

The second virtual server managed by Spring is the security server. The file `ServerSecurityConfig.java` specifies an authentication manager that manages users and encrypts passwords using the algorithms specified in `Encoders.java`. Another file named `AuthServerOAuth2Config.java` is responsible for populating the authentication manager with a token store service that handles all of the logic for standard OAuth2 authentication. The token store generates tokens using the algorithms specified in `Encoders.java`. The following steps can be used to test the authentication aspect of the project:

1. Use the current client authority or update` V4_insert_authentication.sql` with a new row that has the password encrypted using BCrypt with 4 rounds. Use [this site](https://www.dailycred.com/article/bcrypt-calculator) to generate an encrypted password with rounds set to 4. The rest of this guide will assume you are using the authority named `encourse-client` with the unencrypted password set to `encourse-password`

2. Create a new HTTP POST request with the path set to `/oauth/token` and Basic Auth token with the following credentials:

   ```
   username=encourse-client
   password=encourse-password
   ```

   unless you specified otherwise in step 1. Here it is in Postman:

   ![img](https://lh3.googleusercontent.com/n54ZvlPK96L7vtPwBImkBoZlLppii9BdZnyWc_BsVZLe_LHFRxcnYm7BXN77YLON7rl7NuzuqWt6hoSaLkVTqJAgYWMIADOYxD89eGKenGug_R4RVLbA4oNtCncICyspJBopyk54)

   Note: The request should be saved as a header as shown below:

   ![img](https://lh4.googleusercontent.com/kx9BrANYoEq2OY1apEef_WHAPY6Fg70vSzouvujimEJJdntsI6cQIEQj-tE9FClIEK_1I2QuWz1eorlEftWfz1ZOHNwehdSBPtjw1wY4-5KOYGQ5ekWtSn0wmzzIT02WWvU2mWdI)

3. Set the body of the request to a form-data type with the data:

   ```
   grant_type=password
   username=	//your User username specified in StartupFeed.java
   password=	//the unencrypted password of the encrypted password in StartupFeed.java
   client_id=encourse-client //or the client name you specified in step 1
   ```

   Note: Postman form-data information and response body containing tokens

   ![img](https://lh3.googleusercontent.com/gZXPSLh2rRM7pR1LN42oMZ-f5XeptKO9bUB53TalNy4pQAFu7_BBPh32CnuuA-_DTCJVoZ-FcveBXETDvFUDM68r-9Trdy9b2XmwtI35-AwkJWU_CfDQGvMNcj2vvBHF73_Vzl3U)

4. To make an authenticated request to the resource server, use the returned value of "access_token" in the request. You should concatenate “Bearer “ + the access token and put it in an HTTP header named “Authorization” as shown below.
   ![img](https://lh4.googleusercontent.com/_Z7ic9BZvzd3GGFiRV3lZ-wL5eJzy_4tZhJhpRgcCvzLTezAjtUF4mTP1aHdVlIQZzzF2dgiSmh9Crv01_MpR9YypKIF7WRCzrncpAJ8sr452mm8-RpEsvbIPo7GXSuDkQE41_kv)

5. When an access_token expires, as shown in the seconds counter “expires_in”, a request to obtain a new access_token must be made. To do this, simply change the form-data to have grant_type=refresh_token and have a new variable refresh_token=//the refresh token obtained from the initial POST request as shown below.
   ![img](https://lh6.googleusercontent.com/2L3dJ55p-2v42K6ll_ta5pFyCe-Wle3xLsyraW8InojHj_cUdRrQcEFOOeXQjuyZlLz7e7NLDinjzLODWoZM_z2SzH7MrNwivLsE2eNBnECzqzEpIj2JC6aZaaEsIH0hOKdtF0QP)



##### Scheduling Configuration

This configuration is set up by two files, `Scheduler.java` and `SchedulingConfig.java`. The main logic is in `Scheduler.java`, and all this class does is run certain functions on an interval similar to cron jobs. Tasks can be scheduled to start at a fixed rate, at a fixed delay between one task ending and another starting, and with a cron expression. Refer to [this document ](https://www.baeldung.com/spring-scheduled-tasks)for further details on scheduling in Spring.

##### Database Configuration and Initialization

There are multiple files that assist in the setup of the database. Firstly, `HibernateConfig.java` specifies the JPA implementation we will use for the application which is Hibernate. [Hibernate](http://hibernate.org/orm/) handles the serialization and deserialization of data structures between Spring and PostgreSQL. Secondly, `StartupFeed.java` populates the database with initial users and accounts for those users. Although it currently is used for testing purposes, least one admin account must be created here in order to authenticate before new accounts are created. 

When creating a new account here, use a password encrypted with BCrypt for 4 rounds (use [this site](https://www.dailycred.com/article/bcrypt-calculator) to generate one). Authorities are specified as roles that can be enumerated from Account.Role_Names.

 Lastly, there are SQL files beneath `encourse/server/src/main/resources/db.migration.postgresql` to setup the security server to persist user and token data. Currently when Spring is first initialized, a migration tool called [Flyway](https://flywaydb.org/) attempts to recreate the tables used by the security server. More information about Flyway will be added in the future if we extend its capabilities to the resource server tables as well. The security server tables are defined in the following files:

1. `V1_create_hibernate_sequence.sq` - required by Hibernate to generate unique IDs
2. `V2_create_oauth2.sql` - creates tables used to persist resource and refresh tokens
3. `V3_create_user.sql` - creates tables used to persist User information and authorities
4. `V4_insert_authentication.sql` - inserts client access scope used by OAuth2 standards
5. `V5_insert_authorites.sql` - inserts User authorities that will be used to grant access

#### Controller Component

The controller component refers to all Java files beneath `encourse/server/src/main/java/edu/purdue/cs/encourse/controller`

There are four java files here named to specify what functions they contain. Most HTTP GET request will be handled by the `ReadController.java` and most HTTP POST requests will be handled by `WriteController.java`. The only exception to this is AuthController.java handles all requests surrounding authentication. 

Within these controller classes are methods annotated to handle most of the heavy lifting for the API endpoints. These annotations are described below with a brief description to aid the development process:

1. `@PreAuthorize` - determines which authorities are allowed access to the endpoint
2. `@RequestMapping` - determines the url, method type, and return type
3. `@RequestParam` - pulls the HTTP parameters that are needed for the endpoint
4. `@RequestBody` - pulls the HTTP body that is needed for the endpoint
5. `@ResponseBody` - injects the return of this method into the HTTP response

#### Database Component

The database component refers to all Java interfaces beneath `encourse/server/src/main/java/edu/purdue/cs/encourse/database`

The first thing to note about this component is that each Java file is just an interface. The implementations for these interfaces are then generated by Spring based on the name of the functions in the interface. For example, `AccountRepository.java` contains the declaration `Account findByUserID(@NonNull String userID);`, so Spring will implement this function by generating an SQL query to find an account by `userID`, or more explicitly, `SELECT * FROM ACCOUNT WHERE userID=userID;`, and parsing the results of the query to create the `Account` object. This means that a developer does not need knowledge of SQL queries to work on this application, simply creating these interfaces with function declarations specifying exactly what will be retrieved is sufficient to add more queries for the application. For further reading, refer to the [Spring documentation on JPA](https://spring.io/guides/gs/accessing-data-jpa/). All functions which access the database should be found under this component.

#### Domain Component

The domain component refers to all Java classes beneath `encourse/server/src/main/java/edu/purdue/cs/encourse/domain`

This component contains all of the object files used on the backend, so any new objects or relations should be added in this directory. Additionally, the subdirectory named `relations` is also found here, which contains the objects the representing the relations in the database. It is advised that the main (non-relation) objects use a single primary key for accessing it from the database, while the relations have a set of attributes for the key. 

A single key can represent a set of keys by using the` @EmbeddableId` annotation, which marks an attribute as a key and also allows the attributes within the key to be referenced using `IdAttribute`. For example, to find a list of `StudentProject` with the same student ID, add the function `findByIdStudentID(@NonNull String studentID)` in the corresponding interface. Additionally, relations may also use other attributes such as dates in the key for storing multiple records for the same relation, such as a student project score for each date the project was active. Also note that lombok annotations, [documented here](https://projectlombok.org/features/all), are used for all objects so that boilerplate code such as getters and setters may be avoided. With these annotations, the getters and setters generated are the attribute name prepended by get/set in camelcase, for example, the `@Getter` annotation generates `getDate()` for the attribute date. Otherwise, all object files under this directory follow standard Java conventions.

#### Service Component

The service component refers to all Java classes beneath `encourse/server/src/main/java/edu/purdue/cs/encourse/service`

This component serves as the logic which connects all of the other components, Python, and Bash, so it can be considered to provide the main functionality of the backend. Most of the files under this directory follow the Spring conventions for providing services, which requires an interface for accessing each service. As a result all services have an interface, found directly under this path, and a corresponding class with a name ending in "Impl", found in the `impl` subdirectory. 

Beneath `impl` is another subdirectory named `helper`, which contains helper classes for the services. For the Impl classes, note that all of the necessary interfaces from the database component are injected using the `@Autowired` annotation. This allows the class to not have a constructor, so that the services themselves can be injected into the controller component. All logic which accesses the database should be placed here, as well as the logic that wraps around Python and Bash scripts so that Java can prep data for the scripts to consume and send the output to the frontend. 

A good way to determine what logic should go into the services is considering if objects must be retrieved from database and/or modified. If so, the logic goes into a service. If not, it might be more fitting to place the code in a script or endpoint. For details on all of the logic currently provided, refer to the Javadocs for EnCourse.

#### Util Component

### Bash Directory

The bash directory is located at `encourse/server/src/main/bash`, and contains all of the bash scripts which are executed by the Java code. 

Bash scripts meant to executed independently should not be placed in this directory, they should be placed in a separate directory on the VM. The main purpose of these scripts is to run git commands and modify the state of the database which could not easily be done in Java. For example, bash scripts are used to execute test scripts as another user so that any error in the test scripts will not be catastrophic to the server due to restricted resources and privileges. Otherwise, the bash scripts are fairly simple compared to Java and Python, so should be easy to understand. 

**NOTE**: Be cautious about setting permissions and modifying the student repositories, since any failure to correctly maintain permissions or preserving the state of the student’s commit tree can disrupt their projects or expose their data. This is the only part of the project which directly accesses the student work, and as a result is the only part capable of disrupting students' work. Be sure to test any changes to these scripts separately and thoroughly before applying them to the production server.

### Python Directory
