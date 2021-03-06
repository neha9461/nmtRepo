# Note Management Tool (NMT) - Back End Prototype

# Pre-requisite 

1. Eclipse (latest available)
2. Install [Spring Tools ](https://marketplace.eclipse.org/content/spring-tools-aka-spring-ide-and-spring-tool-suite)
3. Import settings/NMT_Profile.xml from Eclipse Settings available under java->Code Style -> Formatter -> import
4. Install Lombok plugin 
	1.1 Download lombok.jar from https://projectlombok.org/download
	1.2 $java -jar lomobok.jar will launch the installer to install the plugin.
	1.3 if the above does not work, then modify the eclipse.ini file to include -javaagent:lomobk.jar and place the lomobok.jar at the same location as the eclipse.exe
5. Java 8 Runtime (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
6. Mongo DB (https://www.mongodb.com/download-center#community)

# Instructions

1. Create a user in MongoDB, use the code available in scripts/createMongoUser.sh
2. Configure the MongoDB credentials in config/application.properties appropriately if you want to change it.
3. Setup the environment using setenv.sh or setenv.bat depending on the platform. i.e. $. ./setenv.sh or CMD>setenv.bat
4. Build and run the code(use build.bat or build.sh scripts folder depending on the platform), the build script would do the following.
    * Check the code for style errors and executes unit tests

        ./gradlew check
 

    * Execute unit tests
       ./gradlew runUnitTests
    * Build and Run the code

        ./gradlew bootRun

5. REST API will be exposed on http://localhost:8080/
        
6. Swagger UI URL: http://localhost:8080/swagger-ui.html, base path: http://localhost:8080/api

7. Creating the distribution, this command will create a dist folder containing the jar, version.txt and start.sh startup script
	$./gradlew -x test build

# Eclipse

1. Update the eclispe project based on the build.gradle run following command

        ./gradlew eclipse

2. Import the project in Eclipse as an existing project.

#Details
1. The project is based on Spring framework and uses Spring Boot to bootstrap the system.
2. The project has unit tests for all layers, i.e repository, service as well as controller
3. The coverage report is generated and placed in  build/reports/jacoco/html
4. The checkstyle report is placed in build/reports/checkstyle
5. The unit tests report is placed in build/reports/tests




