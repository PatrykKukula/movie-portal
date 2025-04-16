# Movie Review Portal

Movie Review Portal is a monolithic REST API built with Spring Boot that serves as a platform for managing movie-related data, including actors and directors. It allows users to register, rate movies, and participate in discussions about each title.
The API supports full CRUD operations on movies, actors, and directors (admin only), while enabling users to create discussion topics on movies. The system implements role-based access control, email verification, and HTTP basic authentication to secure user interactions.


## Features
* User registration with account verification and password reset
* Create/update/delete movie with its details
* Create/update/delete actors and directors with their details
* Fetch movies/actors/directors data with their details
* Rating any movie and update the score
* Discussion section

## Technologies
- Java 23
- Spring Boot
   - Spring data JPA with Hibernate
   - Spring Security
   - Spring Validation
- Lombok
- JUnit5
- Mockito
- PostgreSQL for service
- H2 database for testing
- Docker

## How to run service
**1. Download docker-compose.yml or clone repository**
```bash
git clone https://github.com/PatrykKukula/movie-review-portal.git
```



**2.  Run the container**
Navigate to the folder containing **docker-compose.yml** file , for example
```bash
cd F:
cd moviereviewportal
```
And then:
```bash
docker compose up -d
```

**3. Send requests using Postman**

Download postman here https://www.postman.com/downloads/ if you haven't already, create account and log in.

Download Postman collection from this repository -  ***MovieReviewPortal.postman_collection.json***

And then import it to the Postman to easily navigate through enpoints.

Click on the import button in Postman, and paste the file url, raw text or drop the file to the window:

![import](https://github.com/user-attachments/assets/59c73cd6-8200-45e0-a42e-5f0a50602361)


Collection has every endpoint set up, with prepared authority, request body and parameters. You just need to provide data you want.
List of endpoints below. 

![package](https://github.com/user-attachments/assets/403081c5-c958-4df7-b53a-c9e5d3f2d5b7)

> **Note**
> 
>**Any GET Request** does not need authentication. Anyone can fetch movies, actors and directors data.
> 
>**User** can set rate for any movie, create topic under each movie topic section and add comment in any topic.
> 
>**Admin** can do anything that user can do but also edit other user topics and comments, create/update/delete movies, actors and directors.

To perform request that need authentication, you must provide credentials under Auth section:

![authentication](https://github.com/user-attachments/assets/0ce82fd4-ece7-48b3-acf5-9fd489db20b7)


> **Note**  
> In the service, you can only create an user with the USER role.  
> To use endpoints allowed only for ADMIN role, use the pre-created admin account:  
> **Email:** admin@admin.pl  
> **Password:** Admin123!

## **Endpoints**

**Auth**
  
Endpoints to menage users. All endpoints do not need any authentication. 

  - **POST** */auth/register* - register new user with role USER
  - **POST** */auth/register/confirm* - verify account by providing token to set account enabled
  - **POST** */auth/reset* - reset password by providing token
  - **GET** */auth/reset* - generate password reset token
  - **GET** *auth/register/sendToken* - generate verification token

**Actor**
  
Endpoints to menage actors.

  - **POST** */actors* - create new actor
  - **PATCH** */actors/{actorId}* - update existing actor
  - **DELETE** */actors/{actorId}* - delete existing actor
  - **GET** */actors/{actorId}* - fetch actor data by id
  - **GET** */actors* - fetch all actors data, define sorting, search by name or last name
 
**Director**
  
Endpoints to menage directors.

  - **POST** */directors* - create new director
  - **PATCH** */directors/{directorId}* - update existing director
  - **DELETE** */directors/{directorId}* - delete existing director
  - **GET** */directors/{directorId}* - fetch director data by id
  - **GET** */directors* - fetch all directors data, define sorting, search by name or last name

**Movies**
  
Endpoints to menage movies.

  - **POST** */movies* - create new movie
  - **DELETE** */movies/{movieId}* - delete movie
  - **POST** */movies/{movieId}/actors/{actorId}* - add actor do movie
  - **DELETE** */movies/{movieId}/actors/{actorId}* - remove actor from movie
  - **PATCH** */movies/{movieId}* - update existing movie
  - **GET** */movies/{movieId}* - fetch movie data by id
  - **GET** */movies/search* - fetch all movies data, define sorting, find by movie title
  - **GET** */movies* - fetch all movies data, define sorting
  - **POST** */movies/rate* - add rate to movie
  - **DELETE** */movies/{movieId}/rate* - remove rate from movie
    
**Topic**
  
Endpoints to menage topic.

- **POST** */topics* - create new topic
- **PATCH** */topics/{topicId}* - update existing topic
- **DELETE** */topics/{topicId}* - delete existing topic
- **GET** */topics/{topicId}* - fetch  topic data by id
- **GET** */topics* - fetch all topics data, define sorting
- **GET** */topics/search* - fetch all topics data, define sorting, search by topic title
    
**Comment**
  
Endpoints to menage comments.

- **POST** */comments* - create new comment
- **PATCH** */comments/{commentId}* - update existing comment
- **DELETE** */comments/{commentId}* - delete existing comment
- **GET** */comments* - fetch all comments data, define sorting
- **GET** */comments/topic/{topicId}* - fetch all comments data for topic, define sorting
- **GET** */comments/user/{username}* - fetch all comments data for user

Every request has prepared body. You just need to insert data you want and send request.

![body](https://github.com/user-attachments/assets/770f6c04-b690-427d-a649-836686f6666b)


Same with request parameters. Every parameter is prepared and described.


![parameters](https://github.com/user-attachments/assets/7eaaacb6-503b-4061-8c56-a32a322676ef)

### Database client

You can use database terminal like **Sqlectron** to operate directly on the database.

- **Download Sqlectron** from https://sqlectron.github.io
- Open Sqlectron and configure database connetion like in the image below. Make sure to run the container first.

  ![connect to sqlectron](https://github.com/user-attachments/assets/d2957743-9af3-4fa4-916a-4f268a899838)

- Switch to the MoviePortal database and write queries.

![sqlectron db](https://github.com/user-attachments/assets/259e46d1-146c-468b-addd-256b5e4dad44)

## Common issues

Most common issue you can see is that one of ports on your machine is busy. This service run on ports:
- 5433 - Database
- 8082 - Movie Review Portal Service

If there is an occurence of any of these ports being occupied, try to identify the service that currently is using this port:

```bash
netstat -ano |findstr :{portnumber}
```
and then terminate that process:

```bash
taskkill /F /PID {processId}
```








