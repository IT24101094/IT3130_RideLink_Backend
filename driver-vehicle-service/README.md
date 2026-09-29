# RideLink – Driver & Vehicle Service

**Primary Owner:** IT24101097 – Vithanage R.V.C.S.

## 1. Overview

The Driver & Vehicle Service is an independent backend microservice in the RideLink platform.

It is responsible for managing driver operational profiles and associated vehicle information, including:

- Driver identity and licence information
- Vehicle details
- Driver availability
- Driver service area
- Simulated current location
- Retrieval of eligible available drivers
- Driver information required by other RideLink microservices

The service exposes RESTful JSON APIs and owns its own MongoDB database.

---

## 2. Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application programming language |
| Spring Boot | Microservice framework |
| Spring Web | REST API implementation |
| Spring Data MongoDB | MongoDB persistence |
| Jakarta Validation | Request validation |
| SpringDoc OpenAPI | Swagger/OpenAPI documentation |
| Maven | Build and dependency management |
| JUnit | Automated testing |
| Mockito | Service-layer unit testing |
| Postman | API testing and demonstration |
| MongoDB | Driver Service database |

---

## 3. Service Configuration

### Application

```text
Service Name: driver-vehicle-service
Port: 8082
```

### MongoDB

```text
MongoDB URI: mongodb://localhost:27017
Database: ridelink_driver_db
Collection: drivers
```

The Driver & Vehicle Service owns this database. Other RideLink microservices must not directly access its MongoDB collections.

Inter-service access must take place through the Driver Service REST API.

---

## 4. Project Structure

```text
driver-vehicle-service/
├── postman/
│   └── RideLink-Driver-Vehicle-Service.postman_collection.json
├── src/
│   ├── main/
│   │   ├── java/com/ridelink/driver_vehicle_service/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ridelink/driver_vehicle_service/
├── pom.xml
├── mvnw
└── README.md
```

---

## 5. Domain Model

### Driver

The driver document contains:

```text
id
name
licenseNumber
available
serviceArea
currentLocation
vehicle
```

### Location

```text
latitude
longitude
```

### Vehicle

```text
registrationNumber
make
model
type
color
```

---

## 6. API Endpoints

Base URL:

```text
http://localhost:8082
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/drivers` | Create a driver |
| GET | `/api/drivers/{id}` | Get compact driver information |
| GET | `/api/drivers/{id}/profile` | Get full driver operational profile |
| GET | `/api/drivers/available` | Get all available drivers |
| GET | `/api/drivers/available?serviceArea={area}` | Get available drivers by service area |
| PUT | `/api/drivers/{id}` | Update driver and vehicle information |
| PATCH | `/api/drivers/{id}/availability` | Update driver availability |
| DELETE | `/api/drivers/{id}` | Delete a driver |

---

## 7. Create Driver

### Request

```http
POST /api/drivers
Content-Type: application/json
```

Example:

```json
{
  "name": "Nimal Perera",
  "licenseNumber": "B1234567",
  "available": true,
  "serviceArea": "Colombo",
  "currentLocation": {
    "latitude": 6.9271,
    "longitude": 79.8612
  },
  "vehicle": {
    "registrationNumber": "CAB-1234",
    "make": "Toyota",
    "model": "Prius",
    "type": "Car",
    "color": "White"
  }
}
```

Successful response:

```text
HTTP 201 Created
```

Example response:

```json
{
  "id": "generated-driver-id",
  "name": "Nimal Perera",
  "licenseNumber": "B1234567",
  "vehicleRegistrationNumber": "CAB-1234"
}
```

---

## 8. Inter-Service Driver Contract

Other RideLink services can retrieve the minimum driver information required for integration using:

```http
GET /api/drivers/{id}
```

Response:

```json
{
  "id": "String",
  "name": "String",
  "licenseNumber": "String",
  "vehicleRegistrationNumber": "String"
}
```

This compact response is intentionally separate from the full operational profile.

Other services should consume this API rather than reading the Driver Service database directly.

---

## 9. Full Driver Profile

```http
GET /api/drivers/{id}/profile
```

Example response:

```json
{
  "id": "generated-driver-id",
  "name": "Nimal Perera",
  "licenseNumber": "B1234567",
  "available": true,
  "serviceArea": "Colombo",
  "currentLocation": {
    "latitude": 6.9271,
    "longitude": 79.8612
  },
  "vehicle": {
    "registrationNumber": "CAB-1234",
    "make": "Toyota",
    "model": "Prius",
    "type": "Car",
    "color": "White"
  }
}
```

---

## 10. Available Driver Retrieval

### All Available Drivers

```http
GET /api/drivers/available
```

Only drivers with:

```text
available = true
```

are returned.

### Filter by Service Area

```http
GET /api/drivers/available?serviceArea=Colombo
```

This retrieves eligible available drivers operating in the requested service area.

Service-area matching is case-insensitive.

---

## 11. Update Driver Availability

```http
PATCH /api/drivers/{id}/availability
Content-Type: application/json
```

Mark unavailable:

```json
{
  "available": false
}
```

Mark available:

```json
{
  "available": true
}
```

When a driver is marked unavailable, the driver is excluded from available-driver retrieval.

---

## 12. Update Driver

```http
PUT /api/drivers/{id}
```

The update operation can modify the driver's operational profile and associated vehicle information.

---

## 13. Delete Driver

```http
DELETE /api/drivers/{id}
```

Successful deletion:

```text
HTTP 204 No Content
```

A subsequent request for the deleted driver returns:

```text
HTTP 404 Not Found
```

---

## 14. Validation

The service validates required driver information.

Required fields include:

```text
name
licenseNumber
serviceArea
```

Example invalid request:

```json
{
  "name": "",
  "licenseNumber": "",
  "available": true,
  "serviceArea": ""
}
```

Example validation response:

```json
{
  "status": 400,
  "error": "Validation Failed",
  "messages": {
    "name": "Driver name is required",
    "licenseNumber": "License number is required",
    "serviceArea": "Service area is required"
  }
}
```

---

## 15. Error Handling

If a driver does not exist, the service returns:

```text
HTTP 404 Not Found
```

Example:

```json
{
  "error": "Not Found",
  "message": "Driver not found with id: UNKNOWN",
  "status": 404
}
```

Validation failures return:

```text
HTTP 400 Bad Request
```

---

## 16. Running the Service

### Prerequisites

Install:

- Java 21 or later
- MongoDB
- Git
- Postman (optional for API testing)

The project includes the Maven Wrapper, so a separate Maven installation is not required.

### Start MongoDB

Ensure MongoDB is available on:

```text
localhost:27017
```

### Run Driver Service

From:

```text
IT3130_RideLink_Backend/driver-vehicle-service
```

execute:

```bash
./mvnw spring-boot:run
```

The service starts on:

```text
http://localhost:8082
```

---

## 17. Swagger / OpenAPI

With the service running, Swagger UI is available at:

```text
http://localhost:8082/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8082/v3/api-docs
```

Swagger can be used to inspect and manually execute the Driver Service endpoints.

---

## 18. Automated Tests

Run:

```bash
./mvnw clean test
```

The test suite covers service behavior including:

- Driver creation
- Driver retrieval
- Missing-driver handling
- Driver update
- Driver deletion
- Available-driver retrieval
- Service-area filtering
- Full driver profile retrieval
- Availability updates
- Spring application context loading

---

## 19. Postman Collection

Collection:

```text
postman/RideLink-Driver-Vehicle-Service.postman_collection.json
```

Import the collection into Postman and run it against:

```text
http://localhost:8082
```

The collection automatically stores the generated driver ID in:

```text
{{driverId}}
```

and uses it for subsequent requests.

The collection tests the complete lifecycle:

```text
Create Driver
      ↓
Get Integration View
      ↓
Get Full Profile
      ↓
Retrieve Available Drivers
      ↓
Filter by Service Area
      ↓
Update Driver
      ↓
Set Driver Unavailable
      ↓
Verify Availability Filtering
      ↓
Set Driver Available
      ↓
Delete Driver
      ↓
Verify 404 After Deletion
```

The verified collection run executed:

```text
11 API requests
19 tests passed
0 failed
0 skipped
0 errors
```

---

## 20. Microservice Data Ownership

The Driver & Vehicle Service follows independent microservice data ownership.

```text
Driver & Vehicle Service
          │
          ▼
ridelink_driver_db
          │
          ▼
drivers collection
```

Other services must communicate with the Driver Service through REST APIs.

They must not directly query or modify `ridelink_driver_db`.

This reduces coupling between microservices and allows the Driver Service to control its own domain data.

---

## 21. Integration Responsibilities

The Driver Service provides driver information required by other RideLink services.

For example:

```text
Ride Management Service
          │
          │ REST/JSON
          ▼
Driver & Vehicle Service
          │
          ▼
ridelink_driver_db
```

Stable driver IDs are used when referencing a driver across service boundaries.

The compact endpoint:

```http
GET /api/drivers/{id}
```

is provided specifically for inter-service driver lookup.

---

## 22. Demonstration Checklist

Before demonstrating the service:

1. Start MongoDB.
2. Start Driver Service on port `8082`.
3. Open Swagger UI.
4. Demonstrate driver creation.
5. Demonstrate compact driver retrieval.
6. Demonstrate the full operational profile.
7. Demonstrate available-driver retrieval.
8. Demonstrate service-area filtering.
9. Change availability to `false`.
10. Show that the unavailable driver is excluded.
11. Change availability back to `true`.
12. Run the automated Maven tests.
13. Run the Postman collection.
14. Show successful Postman test results.
15. Explain that the Driver Service owns `ridelink_driver_db`.

---

## 23. Primary Contributor

**IT24101097 – Vithanage R.V.C.S.**

Primary implementation responsibility: **Driver & Vehicle Service**