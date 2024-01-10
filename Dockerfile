# Use an official OpenJDK runtime
FROM openjdk:17-jdk

# Set the working directory to /app
WORKDIR /app

# Copy the application JAR file into the container at /app
COPY target/fedex-0.0.1-SNAPSHOT.jar /app/

# Make port 8080 available to the world outside this container
EXPOSE 8081

# Run the application when the container launches
CMD ["java", "-jar", "fedex-0.0.1-SNAPSHOT.jar"]
