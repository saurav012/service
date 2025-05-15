# Weather Service API

This Spring Boot application demonstrates how to configure and use multiple `WebClient` beans to call external weather APIs — specifically, **Weatherstack** and **OpenWeatherMap**.

## 📦 Features

- REST API integration using Spring WebClient
- Reactive programming using Project Reactor (`Mono`)

### Prerequisites
* Git
* JDK 17 or later
* Maven 3.0 or later

## Clone
To get started you can simply clone this repository using git:
```
git clone https://github.com/saurav012/service.git
cd service
```

### Build an executable JAR
You can run the application from the command line using:
```
mvn spring-boot:run
```
Or you can build a single executable JAR file that contains all the necessary dependencies, classes, and resources with:
```
mvn clean package
```

### Build Docker image

```
mvn install dockerfile:build
```

### Run the service in a container
```
docker run -p 9080:9080 -t /service
```


