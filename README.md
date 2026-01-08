# Auth_Project_Using_Spring_Boot

Auth_Project_Using_Spring_Boot is a backend authentication service built with Spring Boot.
It provides secure user authentication, OTP-based verification, and password reset features.
This project is designed as a reusable auth module for any frontend or backend system.

---

## Overview

This project handles complete user authentication and account security flows.
Users can register, log in, verify their account using OTP, reset passwords, and access protected APIs using JWT.
Email notifications are sent using SMTP for OTP and account-related actions.

---

## Features

* User registration with validation
* JWT-based login and authentication
* Protected APIs using Spring Security
* Check authenticated user status
* Read current user profile
* Email verification using OTP
* Password reset using OTP
* Secure OTP storage and validation
* Email notifications via SMTP (Brevo)
* Stateless security configuration

---

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* JWT (JSON Web Token)
* Spring Data JPA
* MySQL (or any relational DB)
* Java Mail Sender (SMTP)
* Maven

---

## Base API URL

```
http://localhost:8080/api/v1.0
```

---

## API Structure (Postman with Gmail Notifications )

### Register / Login / Authentication

* `POST /register`


  <img width="921" height="413" alt="register in authify app" src="https://github.com/user-attachments/assets/6c03d262-b3bb-4bb7-97b4-2529bcfdab6f" />
  
  *  `User Registration and Email Notification`
 
    
     <img width="1037" height="226" alt="welcome on email" src="https://github.com/user-attachments/assets/4ebe3535-913e-468d-a610-7ac22c89f942" />


* `POST /login`


  <img width="938" height="415" alt="Login" src="https://github.com/user-attachments/assets/1e344498-ad02-4950-b68e-321948c919ef" />


* `GET /is-authenticated`


  <img width="932" height="277" alt="authenticated " src="https://github.com/user-attachments/assets/49cdea9f-5439-4d05-8c9d-6109ffc596c6" />

* `GET /profile`

  <img width="936" height="390" alt="Read profile" src="https://github.com/user-attachments/assets/a30d9e96-55da-4e3b-9d71-2ba58a97e7c6" />


### Verify Account / OTP and Password Reset

* `POST /send-otp` (send verification OTP to logged-in user)

  <img width="936" height="319" alt="send verify otp" src="https://github.com/user-attachments/assets/e75e1e8a-24c7-467f-803a-297b6f472c49" />

  * ` OTP received on email `

    <img width="1034" height="139" alt="account verifey OTP on email" src="https://github.com/user-attachments/assets/b70d2afc-a81e-4a06-b692-9a5552cb7ccc" />

* `POST /verify-otp` (verify OTP)

  
   <img width="934" height="292" alt="otp verifed" src="https://github.com/user-attachments/assets/759496b3-3a09-460b-b0c4-ee3af8b70abf" />

   * ` Verified Account Status in User Profile `

     <img width="931" height="380" alt="status account verifed" src="https://github.com/user-attachments/assets/5a92a848-a2b2-42fc-bb8c-beab73082286" />

     

* `POST /send-reset-otp` (Postman send-reset-otp request)

   <img width="939" height="289" alt="reset password opt" src="https://github.com/user-attachments/assets/43067a6e-14f9-4c3f-93fe-c921c2e2fb2c" />

   * ` Reset OTP received on email `

     <img width="1035" height="181" alt="send opt on email for resetting  password" src="https://github.com/user-attachments/assets/0e00429b-b8c5-467f-a9db-7b9242ac6c4c" />



* `POST /reset-password` (reset password using email, OTP, new password)

  <img width="938" height="331" alt="change password with new password " src="https://github.com/user-attachments/assets/7c0bc748-a904-42f7-9841-822ae2382c36" />

  * `Login With Old Password (Before Reset) `

    <img width="939" height="374" alt="login with old password " src="https://github.com/user-attachments/assets/d2c74911-37dd-48cb-ac5c-8563eaf5d039" />


  * `Login With New Password (After Reset) `
  
    <img width="934" height="398" alt="Login with new password" src="https://github.com/user-attachments/assets/d33ce1e1-6526-40b1-b31e-897bba2669bf" />


---

## Authorization (JWT Bearer Token)

The JWT token is generated after a successful login.
Paste this token in Postman under **Authorization → Bearer Token** to access protected APIs.


<img width="924" height="334" alt="image" src="https://github.com/user-attachments/assets/98f44b98-56ae-4b24-b472-9a04ee1c8d25" />


---

## Authentication Flow

1. User registers using email and password.
2. Welcome email is sent to the user.
3. User logs in and receives a JWT token.
4. JWT token is required for secured endpoints.
5. User can request OTP for verification or password reset.
6. OTP is validated before sensitive actions.

---

## Mail and OTP Workflow

* Emails are sent using Brevo SMTP.
* OTPs are generated securely and stored in the database.
* OTPs are validated before verification or password reset.
* Proper error handling is implemented for invalid or expired OTPs.

---


## How to Run Locally

1. Clone the repository.
2. Create a database in MySQL.
3. Copy `application.properties.example` to `application.properties`.
4. Update database and mail credentials.
5. Run the application using IDE or `mvn spring-boot:run`.
6. Import Postman collection and test APIs.

---

## Example application.properties

```properties
spring.application.name=authify
spring.datasource.url=jdbc:mysql://localhost:3306/authify_app
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

jwt.secret.key =addLongWithcomplexKey

server.servlet.context-path=/api/v1.0



#Mail details
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=example@email.com
spring.mail.password=your_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.from=your_email_account


```

---

## Environment Variables

You can use environment variables instead of hardcoding secrets:

* `DB_URL`
* `DB_USER`
* `DB_PASS`
* `MAIL_HOST`
* `MAIL_USER`
* `MAIL_PASS`
* `JWT_SECRET`

---


---

## Tests

APIs were tested using Postman.

<img width="254" height="283" alt="Auth structure" src="https://github.com/user-attachments/assets/14ba2dda-da09-41ba-9dd1-1d7892dfcf73" />

---

## Deployment Notes

* Do not commit secrets or credentials.
* Use environment variables in production.
* Use a managed database in production.
* Configure SMTP provider correctly for emails.

---

## Future Enhancements

* Add refresh token support.
* Add rate limiting for OTP APIs.
* Add account lock on multiple failed attempts.
* Add admin user management APIs.

---

## License

This project is licensed for personal and educational use only.
Commercial use is not permitted without permission.

---

## Contact

Name: Ankit  
Project: Auth_Project_Using_Spring_Boot  
Email:  ankitk.software@gmail.com  
GitHub: https://github.com/Ankit-kumar8-1


