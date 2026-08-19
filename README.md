##CatCritic


CatCritic is a web application where animal lovers can discover, rate, and share cats. Registered users can browse cat profiles, leave star ratings, and even submit their own cats to the platform. Each cat profile includes a photo, name, breed, and a short description.

##What does it do?
Depending on their account type, users can do different things:

-Cat Viewer: browse cats
-Cat Owner: browse, rate, upload cats
-Admin: browse, rate, upload cats as well as access admin panel

A default admin account is created automatically when the application starts:
-Username: admin
 Password: admin123

It is strongly recommended to change the admin password before deploying to any environment that is accessible to others.

##Running the Project
Please make sure you have the following installed on your computer before tryingto run the application:
Java 17
Maven
MySQL
If you are just running the application locally for the first time, you only need Java 17 and Maven.

CatCritic has two different ways it can run, called profiles. Think of profiles like different modes: one is set up for easy local development and testing, and the other is set up for a real deployment.

Profile 1: dev (Local Development )
This is the default mode. It uses an H2 in-memory database, which means the database lives entirely in memory while the app is running and does not require you to install or configure anything extra. The database is wiped and re-seeded fromscratch every time the application restarts.

To start the application in dev mode, open a terminal in the project's root folder and run:
mvn spring-boot:run

Navigate to your browser of choice and type:
http://localhost:8081



You can also inspect the database directly in your browser while the app is running by visiting:
http://localhost:8081/h2-console

Use these credentials to log in to the H2 console:
JDBC URL	jdbc:h2:mem:catcritic
Username	sa
Password	(leave blank)

##Profile 2: prod / qa (MySQL)
This mode connects to a real MySQL database and is intended for when the application is being shared with others or deployed to a server. The database is persistent, meaning data is saved between restarts.
-Log in to MySQL and create a database for the application:
sql
CREATE DATABASE catcritic;
-Rather than typing your database username and password directly into any file, CatCritic reads them from environment variables on your machine. This keeps sensitive credentials out of the project files.
On Mac/Linux, run these in your terminal before starting the app:

export SPRING\_DATASOURCE\_USERNAME=your\_mysql\_usernameexport SPRING\_DATASOURCE\_PASSWORD=your\_mysql\_password

On Windows (Command Prompt), run:

set SPRING\_DATASOURCE\_USERNAME=your\_mysql\_usernameset SPRING\_DATASOURCE\_PASSWORD=your\_mysql\_password
-Run the following to enter the production profile: 

mvn spring-boot:run -Dspring-boot.run.profiles=prod

Or for the qa profile:

mvn spring-boot:run -Dspring-boot.run.profiles=qa

Hibernate will create all necessary schemas during first launch, and the project has seeded data in order to get the complete experience upon opening.


## The CatCritic Team


Scott designed and implemented the core application logic, including REST endpoints, Spring Security configuration, and service layers. He also managed the Git repository, maintaining branch structure and overseeing code integration.

Daniel built all Thymeleaf templates, CSS styling, and UI components, handling the full frontend from initial design through to final implementation.

Michael designed the database schema, authored YAML configuration across all deployment profiles, implemented data validation, and handled bug fixing across the codebase.
