## CO2 Monitoring Service
A service for collecting data from sensors and publish metrics depending on the CO2 concentrations levels

### Table of Contents
- [Setup & Run Instructions](#Setup-&-Run-Instructions)
- [Testing](#Testing)
- [API documentation](#API-Documentation)
- [Architecture](#Architecture)
- [Design Decisions](#Design-Decisions)
- [Future Improvements](#Future-Improvements)

### Setup & Run Instructions
Detailed steps to get your development environment running:

#### Prerequisites:
- Java JDK 17 or higher
- Maven

#### Clone the Repository
```shell
git clone https://github.com/Ahmad-alsanie/co2-monitoring-service.git
cd co2-monitoring-service
```

#### Without Docker
#### Build & Run the Service
```shell
mvn clean install
```

```shell
 mvn spring-boot:run
```

#### With Docker
#### Build & Run the Service
```shell
docker build -t sanie/co2-monitoring-service .
```

```shell
docker run -p 8080:8080 -d sanie/co2-monitoring-service
```

### Testing
For your convenience and aside from the implemented unit and integration tests, you can play around with the curls below or use postMan to perform some requests.

1- Add a sensor

```shell
curl -X POST http://localhost:8080/api/v1/sensors \
     -H "Content-Type: application/json" \
     -d '{"sensorId": "f31aa992-4ea0-4900-a59f-9e862cc4114f", "status": "OK"}'
```

2- Add three consecutive above threshold measures to trigger an alert

```shell
curl -X POST http://localhost:8080/api/v1/measurements \
     -H "Content-Type: application/json" \
     -d '{"sensorId": "f31aa992-4ea0-4900-a59f-9e862cc4114f", "co2Level": 2001}'

curl -X POST http://localhost:8080/api/v1/measurements \
     -H "Content-Type: application/json" \
     -d '{"sensorId": "f31aa992-4ea0-4900-a59f-9e862cc4114f", "co2Level": 2002}'

curl -X POST http://localhost:8080/api/v1/measurements \
     -H "Content-Type: application/json" \
     -d '{"sensorId": "f31aa992-4ea0-4900-a59f-9e862cc4114f", "co2Level": 2003}'

```

3- Navigate to [Sensor-status](http://localhost:8080/api/v1/sensors/f31aa992-4ea0-4900-a59f-9e862cc4114f/status) to view your sensor

4- Navigate to [Alerts](http://localhost:8080/api/v1/sensors/f31aa992-4ea0-4900-a59f-9e862cc4114f/alerts) to view current alerts

Note: DB can be accessed at [h2-db](http://localhost:8080/h2-console)

### API Documentation
Navigate to [Swagger API documentation](http://localhost:8080/swagger-ui/index.html) to view swagger documentation of `alerts`, `sensor` and `measurments` endpoints.

| API         | supported methods | onSuccess   | onFailure |
|-------------|-------------------|-------------|-----------|
| Measurement | POST              | 201         | 404       | 
| Sensor      | GET & POST        | 200 & 201   | 404       | 
| Alert       | GET               | 200         | 204       | 
 | Metrics     | GET               | 200         | 404       |


### Architecture
Key components of our service:
- Controller: serves as the entry point for HTTP requests and handles Restful endpoints ```measurements```,```metrics``` , ```alerts``` and ```sensors```
- Service: contains the core business logic, manages the storage and process events
- Repository: hosts our DAOs for robust data handling capabilities while keeping the domain logic clean and decoupled from the persistence layer
- Model: contains a representation of our main entities ```Alert```, ```Measurement```, ```Sensor``` and ```Status```
- Configuration: manages main sensor threshold properties 
- DTO: to separate our external API structure from our internal database model so changes to our database model won't directly impact our API
- Tests: we have both unit tests under [unit](./src/test/java/unit) and [integration](./src/test/java/integration) covering all edge cases and providing assurance

### Design Decisions
Based on the following functional requirements:
1. If the CO2 level exceeds 2000 ppm the sensor status should be set to WARN
2. If the service receives 3 or more consecutive measurements higher than 2000  the sensor status should be set to ALERT
3. When the sensor reaches to status ALERT an alert should be stored
4. When the sensor reaches to status ALERT it stays in this state until it receives 3 consecutive measurements lower than 2000; then it moves to OK
5. The service should provide the following metrics about each sensor:
   ◦ Average CO2 level for the last 30 days
   ◦ Maximum CO2 Level in the last 30 day
6. It is possible to list all the alerts for a given sensor

#### Choice of Technology
- Spring Boot for rapid development features and simplifies the creation of stand-alone, production ready microservices with its autoconfigured components & IOC container.
- Spring JPA for an out-of-the-box support for dealing with DB through its ORM implementation for easier data access and manipulation
- H2 DB used for minimal setup, and out-of-the-box supports for SQL features
- Mockito & junit for a complete and comprehensive + 80% coverage for unit testing
- Docker for containerization allowing us to package and run our application in a loosely isolated environment

### Future Improvements

- Securing the api using ```API_KEY```
- Exporting configuration to a config repo for rapid development
- Rate limiting the API not to allow abuse
- Handle concurrent requests and ensure thread safety
- Adjust the returned http status code to be more descriptive and meaningful
- The service cannot scale horizontally due to SensorStateRepository using in memory DS. This can be easily replaced with a distributed storage system that can be accessed and modified by all service instances. This could be a distributed cache (like Redis, Hazelcast), a database (SQL or NoSQL), or another form of shared storage that ensures consistency and availability of data across instances.

### Out of scope 
Decoupling the `SensorService` and `MeasurementService` from `AlertService` and `SensorService` requires a design approach that allows the sensor status update logic to function independently of alert notification concerns and sensor concerns. 
This can be achieved using event-driven architecture. Events to be published whenever a sensor status changes to ALERT, and any interested parties can listen to these events and act accordingly. This decouples the sensor status management from the alerting mechanism.
Implementing events is out of scope of this service and not a requirement.

Happy coding!