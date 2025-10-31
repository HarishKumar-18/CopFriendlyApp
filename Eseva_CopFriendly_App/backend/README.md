# CopFriendly Backend

A Spring Boot backend application for user authentication with MongoDB integration.

## Features

- User Registration and Login
- JWT-based Authentication
- MongoDB Database Integration
- Password Encryption
- CORS Support
- Input Validation
- Global Exception Handling

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MongoDB running on localhost:27017

## Installation

1. Clone the repository
2. Navigate to the backend directory
3. Install dependencies:
   ```bash
   mvn clean install
   ```

## Configuration

The application is configured to connect to MongoDB using the connection string:
```
mongodb://localhost:27017/Testing
```

JWT configuration is set in `application.properties`:
- Secret key for JWT signing
- Token expiration time (24 hours by default)

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

### Authentication Endpoints

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User"
}
```

#### Login User
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

#### Test Auth Endpoint
```
GET /api/auth/test
```

### User Endpoints

#### Get User Profile
```
GET /api/user/profile
Authorization: Bearer <jwt_token>
```

#### Test User Endpoint
```
GET /api/user/test
Authorization: Bearer <jwt_token>
```

## Response Format

### Successful Authentication Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "testuser",
  "email": "test@example.com",
  "fullName": "Test User",
  "role": "USER"
}
```

### Error Response
```json
{
  "error": "Error message"
}
```

## Security

- Passwords are encrypted using BCrypt
- JWT tokens are used for authentication
- CORS is configured to allow frontend connections
- Input validation is implemented for all endpoints

## Database Schema

### Users Collection
```json
{
  "_id": "ObjectId",
  "username": "String (unique)",
  "email": "String (unique)",
  "password": "String (encrypted)",
  "fullName": "String",
  "role": "String (default: USER)",
  "active": "Boolean (default: true)",
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

## Development

The application uses:
- Spring Boot 3.2.0
- Spring Security
- Spring Data MongoDB
- JWT (jjwt library)
- Maven for dependency management

## Testing

You can test the endpoints using tools like Postman or curl:

```bash
# Test registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123","fullName":"Test User"}'

# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

