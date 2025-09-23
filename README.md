# Movie Review Portal

A web application built with Spring Boot and Vaadin, following an MVC-inspired architecture, featuring both UI components and a REST API layer. It serves as a platform for aggregating and discussing movie-related content, like films, actors, and directors.

The application is secured with Spring Security and uses PostgreSQL as the persistence layer, with Spring Data JPA for data access. Hibernate is used as the underlying ORM provider.

Core features include user authentication and authorization (handled at the server level), movie ratings, discussion threads, user avatars, posters, password reset functionality, and more. Content and discussions are moderated by administrators, who have the ability to edit users’ posts and ban users when necessary.

The application includes unit and integration tests implemented using JUnit 5 and Mockito.


## Core features

* Role based access. User registration with account verification, password reset 
* Managing content
* Image upload (posters, avatars)
* Rating system.
* Discussion section with functionality like creating topics, posting comments, replying to other comments.
* User profiles with Individual statistics, rated content, posted comments etc.

More specific information under following sections.

## Technologies
- Java 23
- Spring Boot
   - Spring data JPA with Hibernate
   - Spring Security
   - Spring Validation
- PostgreSQL for service
- H2 database for testing
- JUnit5 & Mockito
- Lombok
- Caffeine
- Docker

> **IMPORTANT**
>
> For simplicity, in this README will by mostly UI screens. **Full list of available REST API endpoints is available at the end, and full Postman collection is**
> **available to download from this repository**


## Security & Role based access
Application uses **role based access** executed both at the backend (Spring Security config & method level security) and frontend (components visibility depends on current logged in user and it's roles, each page secured with @RolesAllowed). 

Different components are using different **Security Configurations**.
UI is secured with VaadinWebSecurity and is using standard **session mechanism**.

REST API is secured with Security Configuration that uses **JWT token authentication** style - user has to log in to generate JWT token, and provide this token in each request header (if authentication is required). JWT token generator  uses synchronous key encryption and uses SHA256 algorithm with 256 bit secret key to generate tokens.

Each config has configured access rules on each possible endpoint. REST API config is first in order to execute and to check for any request with /api/** prefix in url.
### Roles and authorities
USER - basic role that each account has on creation. It allows to rate content and take part in discussion threads.

MODERATOR - additionally can manage content (add, delete, update)

ADMIN - additionaly can manage users (banning users, adding roles)

If user is not logged in, he can only interact with public content (GET requests for REST API) and create an account.

### Here are just few examples out of many.

Under discussion section there is **create topic** button only if there is authenticated user logged in.

Without being logged in:
<img width="1062" height="800" alt="image" src="https://github.com/user-attachments/assets/9b729edb-14a3-47e8-aad2-7d2d69657ecd" />

With being logged in:
<img width="1034" height="792" alt="image" src="https://github.com/user-attachments/assets/caa374b6-9cc1-4175-b454-4b60c8a3353e" />

In actor page, there are many more components (like edit actor data, delete actor, upload actor picture) when active user has ADMIN role than when USER is active

While being logged in as ADMIN:

<img width="1086" height="777" alt="image" src="https://github.com/user-attachments/assets/41c9c66c-926a-4358-a683-dcae31991bb4" />

While being logged in as USER:

<img width="1048" height="742" alt="image" src="https://github.com/user-attachments/assets/3b0f1d89-c2ad-44a3-94bb-07f6b12c386f" />

## Form and request validation

Forms in UI and REST API requests are validated and **proper information is displayed in a browser or sent in http response if validation failed**. **Different validation rules are applied for different content**. Validation is applied and handled with tools **like bean validation, global exception handler and security filters** (AccesDeniesHandler & AuthenticationEntryPoint via ExceptionTranslationFilter).

UI gives validation information under the invalid form field or as an dialog window.

REST API send response with error message and additional information like path, status code, occurence time etc.
For example, valid username must contain at least one capital letter, number, special character and be at least 8 character long. 

### Here is example

While adding movie,**there is information under invalid field**, like in picture below release fate is in the future. 

<img width="604" height="814" alt="image" src="https://github.com/user-attachments/assets/140765e0-68f3-46fb-85bf-0a0291f85fbe" />

There is also **dialog window to display error** when attempt to save entity with invalid fields occurs:

<img width="640" height="800" alt="image" src="https://github.com/user-attachments/assets/76b86d2f-d63c-42d2-a649-c2427f06992d" />

Same request sent by **Postman** repspond accordingly with proper body and status code:

<img width="1482" height="722" alt="image" src="https://github.com/user-attachments/assets/816e25b4-56cd-4c5e-a12e-cff20233a18f" />

## Caching

Most common fetched data is cached and stored with different rules **using Caffeine**, to optimize future requests and reduce database traffic. For example method to fetch top rated actors has long storing time but method to fetch topic has a short storing time. 

Also there are implemented functions like cache evict under some operations, to avoid returning outdated data.

## Testing

App is tested with **unit tests and @SpringBoot integration tests** for various test cases with **JUnit5 & Mockito.**


## UI Interface

### Let's explore some of the application functions

#### Registration & user account

To register user need to fill registration form or send post request with valid body.
After registration user can change it's information (except username) and can upload avatar, that will be displayed in certain places.

After creating account, **user needs to verify** it to be able to use all functions. 

<img width="628" height="578" alt="image" src="https://github.com/user-attachments/assets/752c5181-b920-43b9-a651-a3f08cc871d3" />


> **note**
> 
> **Current implementation requires user to click link after register or make post request with token, however it is planned to implement sending email with link.**

#### Login

To be able to use funtions that need role base access, user need to log in

<img width="492" height="516" alt="image" src="https://github.com/user-attachments/assets/d5dec09b-622e-4f78-b6f3-4deeb133e4ca" />



#### Password reset

If user forget password, there is possibility to reset it by clicking **forgot password?** button and providing email. After that there is link to redicret to password reset form. 

<img width="420" height="502" alt="image" src="https://github.com/user-attachments/assets/46f5c05a-c485-4322-958f-c9366db13f90" />


> **note**
> 
> **Current implementation requires user to click link after register or make post request with token, however it is planned to implement sending email with link.**

#### My account

In my account tab user can change data about himself and upload avatar.

<img width="528" height="818" alt="image" src="https://github.com/user-attachments/assets/30d5510e-d099-4b8e-ad76-9e8f3376474b" />


#### Admin panel

Admin has acces to **admin panel** where he can browse every created account and make actions like **ban user, remove ban or add different role**

List of users is lazily initialized while scrolling through and can be filtered.

<img width="1903" height="847" alt="image" src="https://github.com/user-attachments/assets/e49229a8-9789-4b49-9883-fa723dc7be93" />

#### User profiles

Users have public profile with it’s basic information and various statistics like rated content, top rated movie category etc. By clicking specific buttons there is redirection to different page for example, you can get redirected to all movies rated or to all comments posted by user.


<img width="1084" height="830" alt="image" src="https://github.com/user-attachments/assets/fc07486d-419b-4315-973a-0a01b19227dc" />











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








