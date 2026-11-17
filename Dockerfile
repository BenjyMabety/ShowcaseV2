# Stage 1: Build the GWT application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Stage 2: Run the application on Apache Tomcat
FROM tomcat:9-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
# Copy the generated WAR file into Tomcat webapps as ROOT.war
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]