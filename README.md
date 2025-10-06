
# AstroDreamers Backend - Air Quality Monitoring System

A Spring Boot application that provides real-time air quality monitoring and personalized alert management by integrating NASA TEMPO satellite data with OpenAQ ground-based sensor measurements.

**[IMAGE: System architecture diagram showing data flow from OpenAQ API → Backend → PostgreSQL → Frontend]**

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Environment Variables](#environment-variables)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Deployment](#deployment)
- [Project Structure](#project-structure)

---

## Overview

The AstroDreamers backend serves as the core engine for air quality monitoring, handling:
- User authentication and authorization (JWT)
- Real-time air quality data retrieval from OpenAQ API
- Location subscription management
- Custom alert threshold configuration
- Email notifications for air quality alerts


---

## Features

- **User Management**: Secure authentication with JWT tokens
- **Location Subscriptions**: Users can subscribe to multiple monitoring locations
- **Real-time Data**: Fetches live pollutant measurements (SO₂, NO₂, PM2.5, PM10, O₃, CO)
- **Alert System**: Customizable thresholds with quiet hours (10 PM - 8 AM)
- **RESTful API**: Clean, documented endpoints for frontend integration
- **CORS Configuration**: Secure cross-origin requests

---

## Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Database**: PostgreSQL 17.6
- **ORM**: Hibernate / JPA
- **Security**: Spring Security + JWT
- **Email**: Spring Mail (SMTP)
- **API Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven
- **Deployment**: Docker + Render.com

---

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git
- (Optional) Docker

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/astrodreamers-backend.git
cd astrodreamers-backend
```

### 2. Install Dependencies

```bash
./mvnw clean install
```

---

## Database Configuration

### Local Development Setup

**Step 1: Create PostgreSQL Database**

```bash
# Access PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE tempo;

# Create user (optional)
CREATE USER tempo_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE tempo TO tempo_user;

# Exit
\q
```

**Step 2: Database Schema**

The application uses Hibernate's `ddl-auto=update` mode, which automatically creates tables on first run:

- `users` - User accounts and authentication
- `subscriptions` - Location subscriptions per user
- `alerts` - Alert configurations for each subscription

**[IMAGE: Database ER diagram showing relationships between users, subscriptions, and alerts tables]**

**Manual Schema (Optional)**

If you prefer manual control:

```sql
-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subscriptions table
CREATE TABLE subscriptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    location_id VARCHAR(255) NOT NULL,
    location_name VARCHAR(255),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Alerts table
CREATE TABLE alerts (
    id SERIAL PRIMARY KEY,
    subscription_id INTEGER REFERENCES subscriptions(id) ON DELETE CASCADE,
    sensor_id VARCHAR(255) NOT NULL,
    alert_enabled BOOLEAN DEFAULT TRUE,
    threshold DECIMAL(10,2),
    quiet_start TIME,
    quiet_end TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Environment Variables

Create a `.env` file in the project root (for local development):

```env
# Application
SPRING_APPLICATION_NAME=tempoapp

# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/tempo
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# JWT Security
SECURITY_JWT_SECRET_KEY=your-256-bit-secret-key-here

# Email Configuration (Gmail)
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# OpenAQ API
API_OPENAQ_KEY=your-openaq-api-key

# OpenAI (optional, for future features)
SPRING_AI_OPENAI_API_KEY=your-openai-key

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

**Getting API Keys:**

- **OpenAQ API**: Register at [openaq.org](https://openaq.org)
- **Gmail App Password**:
    1. Enable 2FA on your Google account
    2. Go to Google Account → Security → App passwords
    3. Generate password for "Mail"

---

## Running the Application

### Local Development

```bash
# Using Maven
./mvnw spring-boot:run

# Or run the JAR
./mvnw clean package
java -jar target/tempoapp-0.0.1-SNAPSHOT.jar
```

Application will start at `http://localhost:8080`

**Verify it's running:**
- Swagger UI: `http://localhost:8080/swagger-ui`
- Health check: `http://localhost:8080/actuator/health` (if enabled)

**[IMAGE: Terminal showing successful Spring Boot startup logs]**

### Docker (Optional)

```bash
# Build Docker image
docker build -t astrodreamers-backend .

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL="jdbc:postgresql://host.docker.internal:5432/tempo" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="your_password" \
  astrodreamers-backend
```

---

## API Endpoints

### Authentication

```
POST /auth/signup          - Register new user
POST /auth/login           - Login and get JWT token
```

### Subscriptions

```
GET    /subscriptions              - Get all user subscriptions
POST   /subscriptions              - Subscribe to a location
GET    /subscriptions/{locationId} - Get single subscription
DELETE /subscriptions/{locationId} - Unsubscribe
```

### Sensors

```
GET /sensors/{locationId}          - Get sensors for a location
```

### Alerts

```
GET    /subscriptions/{locationId}/alerts                    - Get all alerts
POST   /subscriptions/{locationId}/alerts                    - Create/update alert
PATCH  /subscriptions/{locationId}/alerts/{sensorId}/enable  - Enable alert
PATCH  /subscriptions/{locationId}/alerts/{sensorId}/disable - Disable alert
DELETE /subscriptions/{locationId}/alerts/{sensorId}         - Delete alert
```

**[IMAGE: Postman collection or Swagger screenshot showing example API requests]**

**Example Request:**

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123"}'

# Response
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

# Get subscriptions (use token)
curl -X GET http://localhost:8080/subscriptions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Deployment

### Deploying to Render.com

**Step 1: Create Dockerfile**

Already included in the repository:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.andy.tempoapp.TempoappApplication"]
```

**Step 2: Create PostgreSQL Database on Render**

1. Go to Render Dashboard → New → PostgreSQL
2. Name: `tempo-db`
3. Copy the **Internal Database URL**
4. Change `postgres://` to `jdbc:postgresql://`

**Step 3: Deploy Backend Service**

1. New → Web Service
2. Connect GitHub repository
3. Configure:
    - **Build Command**: (leave blank, Dockerfile handles it)
    - **Start Command**: (leave blank, Dockerfile handles it)
4. Add environment variables (see Environment Variables section above)
5. Add `DATABASE_URL` from Step 2
6. Deploy

Your backend will be live at: `https://your-app.onrender.com`

---

## Project Structure

```
astrodreamers-backend/
├── src/
│   ├── main/
│   │   ├── java/com/andy/tempoapp/
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── SecurityConfiguration.java
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthenticationController.java
│   │   │   │   ├── SubscriptionController.java
│   │   │   │   ├── SensorController.java
│   │   │   │   └── AlertController.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Subscription.java
│   │   │   │   └── Alert.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── SubscriptionRepository.java
│   │   │   │   └── AlertRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AuthenticationService.java
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   └── OpenAQService.java
│   │   │   └── TempoappApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── .mvn/
├── Dockerfile
├── pom.xml
└── README.md
```

---

## Configuration Files

### application.yml

```yaml
spring:
  application:
    name: tempoapp
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/tempo}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${SPRING_MAIL_USERNAME}
    password: ${SPRING_MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

server:
  port: ${PORT:8080}

security:
  jwt:
    secret-key: ${SECURITY_JWT_SECRET_KEY}
    expiration-time: 3600000

api:
  openaq:
    url: https://api.openaq.org
    key: ${API_OPENAQ_KEY}

cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

---

## Troubleshooting

**Database Connection Issues**
- Verify PostgreSQL is running: `psql -U postgres`
- Check DATABASE_URL format: must start with `jdbc:postgresql://`
- Ensure database exists: `\l` in psql

**Port Already in Use**
- Change port in application.yml: `server.port: 8081`
- Or kill existing process: `lsof -ti:8080 | xargs kill`

**JWT Token Errors**
- Ensure SECRET_KEY is at least 256 bits (32 characters)
- Check token expiration time

**Email Not Sending**
- Use Gmail App Password, not regular password
- Enable "Less secure app access" or use OAuth2

---

## Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

---

## License

This project is licensed under the MIT License.

---

## Contact

**Team AstroDreamers**
- Frontend: https://astrodreamers.netlify.app
- Backend API: https://tempo-backend-rzn2.onrender.com
- API Docs: https://tempo-backend-rzn2.onrender.com/swagger-ui

---

## Acknowledgments

- NASA TEMPO Mission for atmospheric data
- OpenAQ for real-time ground sensor data
- NASA Space Apps Challenge 2025

---
