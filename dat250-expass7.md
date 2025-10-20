# DAT250: Experiment Assignment 7 

## Setup
Docker was already installed and running.  
I verified that Docker works using:
```bash
docker system info
```
---
##  Creating the Dockerfile
I created a new file named **Dockerfile** in the root of my project (`expass4`):

```dockerfile
# ====== BUILD STAGE ======
FROM gradle:8.5-jdk21 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon

# ====== RUNTIME STAGE ======
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```
---

## Building the Docker Image
In the project directory, I built the image:
```bash
docker build -t expass4-app .
```
---
## Running the Container
To run the application:
```bash
docker run -p 8080:8080 expass4-app
```
The application started successfully, showing the typical Spring Boot startup logs. 
Only problem i had was that since i built upon expass 6 gradle got confused by trying to connect to RabbitMQ. 
I could then access the app at:

```
http://localhost:8080
```

---

## Notes
- The app attempts to connect to RabbitMQ on `localhost:5672`.
- Since no RabbitMQ instance was running, it showed `Connection refused` messages.
- The main application still runs correctly, so this can be safely ignored for this experiment.

---
## Optional Extensions
If needed, I could later:
- Add a `docker-compose.yml` with RabbitMQ service.

