# Authentication API

A REST API for user authentication built with **Spring Boot 3** and **Java 21**. Handles registration, login, JWT access tokens, refresh tokens, and password reset via email.

---

## Features

- User registration & login
- JWT authentication (HS256)
- Refresh token management
- Password reset by email (Mailtrap)
- Role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- BCrypt password hashing
- Centralized error handling

---

## Prerequisites

- **JDK** 21+
- **Maven** 3.8+
- **MySQL** 8+
- A **Mailtrap** account (for email)

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/authentication_api_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=yourpassword

# JWT
jwt.security.key=your_base64_secret_key
jwt.expiration=86400000
jwt.refreshExpirationMs=86400000

# Mail (Mailtrap)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=your_mailtrap_username
spring.mail.password=your_mailtrap_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> ⚠️ Before running, fix the 2 blocking bugs listed at the bottom of this README.

---

## Running the Application

```bash
cd authentication_api
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## API Endpoints

All routes are prefixed with `/api/v1/auth`.

### Register

```
POST /api/v1/auth/register
```

```json
{
  "name": "Jean Dupont",
  "username": "jean",
  "email": "jean@example.com",
  "password": "secret123"
}
```

### Login

```
POST /api/v1/auth/login
```

```json
{
  "email": "jean@example.com",
  "password": "secret123"
}
```

Response:
```json
{
  "responseCode": 200,
  "responseMessage": "SUCCESS",
  "data": {
    "username": "jean",
    "email": "jean@example.com",
    "roles": ["ROLE_USER"],
    "accessToken": "<jwt>",
    "tokenType": "Bearer"
  }
}
```

### Refresh Token

```
POST /api/v1/auth/refresh
```

```json
{ "refreshToken": "<your_refresh_token>" }
```

### Logout

```
POST /api/v1/auth/logout
```

```json
{ "refreshToken": "<your_refresh_token>" }
```

### Request Password Reset

```
POST /api/v1/auth/redeem-password
```

```json
{ "email": "jean@example.com" }
```

Sends a reset link to the email address (valid 24 hours).

### Reset Password

```
POST /api/v1/auth/reset-password
```

```json
{
  "token": "<token_from_email>",
  "newPassword": "newSecret123"
}
```

### Get All Users *(Admin only)*

```
GET /api/v1/auth/users
Authorization: Bearer <jwt>
```

---

## Project Structure

```
authentication_api/
└── src/main/java/com/authenticationsystem/apiauthentication/
    ├── web/
    │   └── AuthController.java
    ├── services/
    │   ├── AuthService.java
    │   ├── RefreshTokenService.java
    │   └── EmailService.java
    ├── security/
    │   ├── WebSecurityConfig.java
    │   ├── UserDetailsImpl.java
    │   └── UserDetailsServiceImpl.java
    ├── securityJwt/
    │   ├── JwtUtils.java
    │   ├── AuthTokenFilter.java
    │   └── AuthEntryPointJwt.java
    ├── models/
    │   ├── User.java
    │   ├── Role.java
    │   ├── Erole.java
    │   ├── RefreshToken.java
    │   └── PasswordResetToken.java
    ├── repositories/
    ├── dto/
    ├── utils/
    └── advice_web/
```

---

## Tech Stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | MySQL + Spring Data JPA |
| Email | Spring Mail (Mailtrap) |
| Utilities | Lombok, Bean Validation |
| Build | Maven |

---

## Known Bugs to Fix Before Running

### 1. Invalid test dependencies in `pom.xml`

The following artifacts don't exist on Maven Central and will break the build:

```xml
<!-- ❌ Remove these -->
<dependency>spring-boot-starter-data-jpa-test</dependency>
<dependency>spring-boot-starter-validation-test</dependency>
<dependency>spring-boot-starter-webmvc-test</dependency>
<dependency>spring-boot-starter-security-test</dependency>

<!-- ✅ Replace with -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. `AuthTokenFilter` instantiated with `null` in `WebSecurityConfig`

```java
// ❌ Current (jwtUtils is null — will throw NullPointerException)
return new AuthTokenFilter(null, userDetailsServiceImpl);

// ✅ Fix: inject AuthTokenFilter as a field instead
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final AuthTokenFilter authTokenFilter;
    ...
    http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
}
```
