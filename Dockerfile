# Fetching latest version of Java
FROM openjdk:17

# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY ./target/cars-*.jar /app/cars.jar

# Exposing port 8080
EXPOSE 8080

# Starting the application
CMD ["java", "-jar", "cars.jar"]