# BloggingApp

BloggingApp is a full-featured blog application built with Spring Boot. It provides functionality for user authentication, post creation, commenting, and more.

## Features

- **User Authentication**: Secure signup, login, logout, and token-based authentication.
- **User Management**: Manage user profiles and retrieve authenticated user details.
- **Blog Posts**: Create, read, update, and delete blog posts.
- **Comments**: Add, edit, and delete comments on posts.
- **Like and Comment Tracking**: Track posts liked and commented on by users.

## Getting Started

### Prerequisites

- Java 22
- Maven 3.9.8
- PostreSql
- Redis

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/BloggingApp.git
   cd BloggingApp

2. Configure the database:

   Create a MySQL database.
   Update the `application.properties` file with your database credentials.

3. Configure Redis:

   Install Redis on your machine.
   Ensure Redis is running and update the `application.properties` file with your Redis configuration.

4. Update `application.properties`:

   Configure `application.properties` with the following placeholders:

   ```properties
   # Application Name
   spring.application.name=${SPRING_APPLICATION_NAME}

   # Database Configuration for PostgreSQL
   spring.datasource.url=${SPRING_DATASOURCE_URL}
   spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
   spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.jpa.open-in-view=false

   # Hibernate Properties
   spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO}
   spring.jpa.show-sql=${SPRING_JPA_SHOW_SQL}

   # Tomcat Server Configuration (optional)
   server.port=${SERVER_PORT}

   # Spring JWT Authentication Configuration
   security.jwt.secret-key=${SECURITY_JWT_SECRET_KEY}
   security.jwt.expiration-time=${SECURITY_JWT_EXPIRATION_TIME}

   # Configuration for Redis
   spring.data.redis.host=${SPRING_REDIS_HOST}
   spring.data.redis.port=${SPRING_REDIS_PORT}

   # Configuration for Mail
   spring.mail.host=${SPRING_MAIL_HOST}
   spring.mail.port=${SPRING_MAIL_PORT}
   spring.mail.username=${SPRING_MAIL_USERNAME}
   spring.mail.password=${SPRING_MAIL_PASSWORD}

   # Refresh Token
   app.jwt.refresh-token-expiration=${APP_JWT_REFRESH_TOKEN_EXPIRATION}
   ```

   Replace the placeholders (e.g., ${SPRING_APPLICATION_NAME}, ${SPRING_DATASOURCE_URL}, etc.) with your actual configuration values.

5. Build and run the application:
    ```bash
    mvn clean install
    mvn spring-boot:run

## API Endpoints

### User Management Endpoints

1. **Register a New User**
   - **URL:** `/auth/signup`
   - **Method:** `POST`
   - **Description:** Register a new user in the system.
   - **Request Body:**
     ```json
     {
       "emailId": "string",
       "userName": "string",
       "password": "string"
     }
     ```

2. **Authenticate User and Get JWT Token**
   - **URL:** `/auth/login`
   - **Method:** `POST`
   - **Description:** Authenticate a user and obtain a JWT token for subsequent requests.
   - **Request Body:**
     ```json
     {
       "emailId": "string",
       "password": "string"
     }
     ```
3. **Logout**
   - **URL:** `/auth/logout`
   - **Method:** `POST`
   - **Description:**  Log out the currently authenticated user by invalidating their access token and deleting their refresh token. The user's identity is determined based on the authorization token provided in the request header.

### Post Management Endpoints

1. **Create a New Post**
   - **URL:** `/api/v1/posts/create`
   - **Method:** `POST`
   - **Description:** Create a new blog post.
   - **Request Body:**
     ```json
     {
       "title": "string",
       "content": "string"
     }
     ```

2. **Retrieve All Posts**
   - **URL:** `/api/v1/posts/all`
   - **Method:** `GET`
   - **Description:** Fetch all blog posts.

3. **Retrieve a Single Post by ID**
   - **URL:** `/api/v1/posts/{id}`
   - **Method:** `GET`
   - **Description:** Get details of a specific post by its ID.

4. **Update an Existing Post**
   - **URL:** `/api/v1/posts/edit/{id}`
   - **Method:** `PUT`
   - **Description:** Update an existing blog post by its ID.
   - **Request Body:**
     ```json
     {
       "title": "string",
       "content": "string"
     }
     ```

5. **Delete a Post by ID**
   - **URL:** `/api/v1/posts/delete/{id}`
   - **Method:** `DELETE`
   - **Description:** Delete a specific post by its ID.

6. **Get All Comments from Post Id**
   - **URL** `/api/v1/posts/comments/{postId}`
   - **Method:** `GET`
   - **Description:** Fetch all comments from particular post by its ID.
   
### Comment Management Endpoints

1. **Add a Comment to a Post**
   - **URL:** `/api/v1/comments/create`
   - **Method:** `POST`
   - **Description:** Add a new comment to a specified post.
   - **Request Body:**
     ```json
     {
       "content": "string",
       "postId": 1
     }
     ```

2. **Retrieve Comments for a Post**
   - **URL:** `/api/v1/posts/comments/{postId}`
   - **Method:** `GET`
   - **Description:** Fetch all comments associated with a particular post.

3. **Delete a Comment**
   - **URL:** `/api/v1/comments/posts/{postId}/comments/{commentId}`
   - **Method:** `DELETE`
   - **Description:** Delete a specific comment by its ID.

4. **Update a Comment**
   - **URL:** `/api/v1/comments/edit/{commentId}`
   - **Method:** `PUT`
   - **Description:** Update a specific comment by its ID.

### Token Management Endpoints

1. **Refresh JWT Token**
   - **URL:** `/auth/refresh-token`
   - **Method:** `POST`
   - **Description:** Refresh an existing JWT token to extend its validity.
   - **Request Body:**
     ```json
     {
       "token": "string"
     }
     ```

### User Profile Endpoints

1. **Get User Profile by auth token*
   - **URL:** `/api/v1/users/me`
   - **Method:** `GET`
   - **Description:** Retrieve the profile details of the currently authenticated user. The request must include a valid JWT token in the Authorization header.

2. **Get Posts Commented By User**
   - **URL:** `/api/v1/users/posts/commented`
   - **Method:** `GET`
   - **Description:** Retrieve all posts that the currently authenticated user has commented on. The request must include a valid JWT token in the Authorization header.

3. **Delete User Profile**
   - **URL:** `/api/v1/users/delete`
   - **Method:** `DELETE`
   - **Description:** - **URL:** `/auth/login`
   - **Method:** `POST`
   - **Description:** Permanently delete the user profile of the currently authenticated user. The user's identity is determined based on the authorization token provided in the request header.
     
4. **Get All Posts Created by Authenticated User**
   - **URL:** `/api/v1/users/posts`
   - **Method:** `GET`
   - **Description:**  Retrieve all posts created by the currently authenticated user.
   - **Authentication:** Required
   - **Request** Headers: Authorization: Bearer {access_token} (Include the JWT token in the Authorization header for authentication)

5. **Get Posts Liked By User**
   - **URL:** `/api/v1/users/posts/liked`
   - **Method:** `GET`
   - **Description:** Retrieve all posts that the currently authenticated user has liked on. The request must include a valid JWT token in the Authorization header.

6. **Get All Users**
   - **URL:** `/api/v1/users/getAll`
   - **Method:** `GET`
   - **Description:**  Retrieve all Users.
   

### Like Management Endpoints

1. **Like a Post**
   - **URL:** `/api/v1/likes/like`
   - **Method:** `POST`
   - **Description:** Like a specified post.
   - **Request Body:**
     ```json
     {
       "postId": 1
     }
     ```

2. **Unlike a Post**
   - **URL:** `/api/v1/likes/posts/{postId}/unlike`
   - **Method:** `DELETE`
   - **Description:** Remove a like from a specified post by its ID.
