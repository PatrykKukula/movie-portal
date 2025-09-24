# Movie Portal

A web application built with Spring Boot and Vaadin, following an MVC-inspired architecture, featuring both UI components and a REST API layer. It serves as a platform for aggregating and discussing movie-related content, like movies, actors and directors.

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

ADMIN - additionaly can manage users (banning users, adding roles, editing user's posts)

If user is not logged in, he can only interact with public content (GET requests for REST API) and create an account.

#### Here is an example how it looks in UI.

In actor page, there are many more components (like edit actor data, delete actor, upload actor picture) when active user has ADMIN role than when USER is active

**While being logged in as ADMIN you can see aditional buttons and upload avatar component:**


<img width="1086" height="777" alt="image" src="https://github.com/user-attachments/assets/41c9c66c-926a-4358-a683-dcae31991bb4" />


**While being logged in as USER these components are not visible:**


<img width="1048" height="742" alt="image" src="https://github.com/user-attachments/assets/3b0f1d89-c2ad-44a3-94bb-07f6b12c386f" />


## Form and request validation

Forms in UI and REST API requests are validated and **proper information is displayed in a browser or sent in http response if validation failed**. **Different validation rules are applied for different content**. Validation is applied and handled with tools **like bean validation, global exception handler and security filters** (AccesDeniesHandler & AuthenticationEntryPoint via ExceptionTranslationFilter).

UI gives validation information under the invalid form field or as an dialog window.

REST API send response with error message and additional information like path, status code, occurence time etc.

### Here is example

Erorr messages are **displayed under form field** and in the **dialog window** when you try to save invalid entity:


<img width="640" height="800" alt="image" src="https://github.com/user-attachments/assets/76b86d2f-d63c-42d2-a649-c2427f06992d" />


Using **Postman** repsponse is with proper body and status code:


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

After creating account, **user needs to verify** it to be able to use all functions - link to account verification will be displatyed after successful registration event. 

<img width="428" height="574" alt="image" src="https://github.com/user-attachments/assets/96a09eb8-d1de-4239-aa99-84188555fecc" />


> **note**
> 
> **Current implementation requires user to click link after register or make post request with token, however it is planned to implement sending email with link.**

#### Login

To be able to use funtions that need role base access, user need to **log in:**

<img width="492" height="516" alt="image" src="https://github.com/user-attachments/assets/d5dec09b-622e-4f78-b6f3-4deeb133e4ca" />

In Postman user need to send **post request on /api/auth/login** with credentials, and will get token value in response:

<img width="1160" height="618" alt="image" src="https://github.com/user-attachments/assets/26885e0c-6774-4c8e-bc64-dd67f3914ca4" />

And that token value need to be send **via request header for secured endpoints:**

<img width="1052" height="370" alt="image" src="https://github.com/user-attachments/assets/d6bbcadb-e782-433a-b6f0-7e83200e982b" />



#### Password reset

If user forget password, there is possibility to reset it by clicking **forgot password?** button and providing email. After that there is link to redicret to password reset form. 

<img width="420" height="502" alt="image" src="https://github.com/user-attachments/assets/46f5c05a-c485-4322-958f-c9366db13f90" />


> **note**
> 
> **Current implementation requires user to click link after register or make post request with token, however it is planned to implement sending email with link.**

#### My account

In my account tab user can change data about himself and upload avatar. Avatar is displated in various places like in user profile or in posted comments. If there is no avatar the placeholder image will be displayed.

<img width="600" height="780" alt="image" src="https://github.com/user-attachments/assets/a76bf6fd-9b4e-4328-9753-62c8ebb26ecf" />


#### Admin panel

Admin has acces to **admin panel** where he can browse every created account and make actions like **ban user, remove ban or add different role**

List of users is lazily fetched while scrolling through and can be filtered.

<img width="1903" height="847" alt="image" src="https://github.com/user-attachments/assets/e49229a8-9789-4b49-9883-fa723dc7be93" />

#### User profiles

Users have public profile with it’s basic information and various statistics like rated content, top rated movie category etc. By clicking specific buttons there is redirection to different page for example, you can get redirected to all movies rated or to all comments posted by user.

<img width="1084" height="830" alt="image" src="https://github.com/user-attachments/assets/fc07486d-419b-4315-973a-0a01b19227dc" />

#### Landing page

Main page contains things like latest comments and top rated entities with their rates. Topics have clickable hyperlinks that redirect to entity page or user profile.

<img width="1088" height="803" alt="image" src="https://github.com/user-attachments/assets/a123ca38-7fc9-4597-942c-e6515cc0e19b" />

#### Browsing content

You can browse movies, actors and directors and filter them by certain key (like title, name or last name) and list with see basic information. Movies can also be sorted by category.

<img width="1890" height="908" alt="image" src="https://github.com/user-attachments/assets/6d6adc5c-eb47-4c25-9bb0-bf4875a2071f" />


#### Content details

Each entity has its own unique URI. User can see more details about that entity, add rate to it and see it's topic section. User can add rate by clicking on the star next to poster, change or remove rate by clicking on a star again. Content managers can also upload poster, remove or edit entity by clicking corresponding button.

<img width="1887" height="904" alt="image" src="https://github.com/user-attachments/assets/8d2ce2dc-d8f2-4482-acaa-dfa05029d894" />


#### Adding content

After clicking od **Add** button user is redirected to another page with form. Form need to be filled properly with valid values. In the below example of adding movie, there can be added multiple actors.

<img width="544" height="828" alt="image" src="https://github.com/user-attachments/assets/42a03fe7-ccc5-4864-8d6d-26dd9ea2c6e2" />

#### Topics & Comments

On a topic view there are user comments and some data like user posts, link to user profile, posted time etc. User can either post a comment in a topic or reply directly to given comment in that topic.
Replies are hidden by default, so user have to click on "Show replies" to make them visible. Comments can be deleted or modified here. When user have ADMIN authorites, he can also edit other user comments
if they break rules.

<img width="1094" height="778" alt="image" src="https://github.com/user-attachments/assets/09b64605-e74a-430e-8490-e3a49f620b16" />




## How to run service

>**IMPORTANT**
>
>Application comes with built in SQL script to provide some records to database, to make testing the app easier. If you wish to run the app you will have content to work with.
>
>**Also there is predefinied ADMIN ROLE to explore full functionality. Below are credentials:**
>
>**Email: admin@admin.pl**
>
>**Password: Admin123!**

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

**3. Use browser on url http://localhost:8081/ or Send requests using Postman**

Download postman here https://www.postman.com/downloads/ if you haven't already, create account and log in.

Download Postman collection from this repository -  ***MovieReviewPortal.postman_collection.json***

And then import it to the Postman to easily navigate through enpoints.

Click on the import button in Postman, and paste the file url, raw text or drop the file to the window:

![import](https://github.com/user-attachments/assets/59c73cd6-8200-45e0-a42e-5f0a50602361)


Collection has every endpoint set up, with prepared authority, request body and parameters. You just need to provide data you want.
**List of available endpoints down the page.**

![package](https://github.com/user-attachments/assets/403081c5-c958-4df7-b53a-c9e5d3f2d5b7)


To perform request that need authentication, you must provide credentials under /api/auth/login path and provide received token under authorization header in following requests, as explained earlier.



## **Endpoints**

**Postman collection from this repository has all the endpoints prepared with request bodies and params**

**Auth**
  
Endpoints to menage users. All endpoints do not need any authentication. 

  - **POST** */api/auth/login* - login to generate JWT token
  - **POST** */api/auth/register* - register new user with role USER
  - **POST** */api/auth/register/confirm* - verify account by providing token to set account enabled
  - **POST** */api/auth/reset* - reset password by providing token and 
  - **GET** */api/auth/reset?email=* - generate password reset token, params: email
  - **GET** */api/auth/register/sendToken?email=* - generate verification token, params: email
  - **PATCH** */api/auth/update/* - update user data

**Actor**
  
Endpoints to menage actors.

  - **POST** */api/actors* - create new actor
  - **PATCH** */api/actors/{actorId}* - update existing actor
  - **DELETE** */api/actors/{actorId}* - delete existing actor
  - **GET** */api/actors/{actorId}* - fetch actor data by id
  - **GET** */api/actors?sorted=&findBy=* - fetch all actors data, define sorting, search by name or last name, params: sorted, findBy
  - **GET** */api/actors/top-rated* - fetch top  rated actors
  - **POST** */api/actors/rate/add* - add rate to actor
  - **DELETE** */api/actors/rate/remove/{actorId}* - remove rate from actor
 
**Director**
  
Endpoints to menage directors.

  - **POST** */api/directors* - create new director
  - **PATCH** */api/directors/{directorId}* - update existing director
  - **DELETE** */api/directors/{directorId}* - delete existing director
  - **GET** */api/directors/{directorId}* - fetch director data by id
  - **GET** */api/directors?sorted=&findBy=* - fetch all directors data, define sorting, search by name or last name, params: sorted, findBy
  - **GET** */api/directors/top-rated* - fetch top  rated directors
  - **POST** */api/directors/rate/add* - add rate to director
  - **DELETE** */api/directors/rate/remove/{directorId}* - remove rate from director
    
**Movies**
  
Endpoints to menage movies.

  - **POST** */api/movies* - create new movie
  - **DELETE** */api/movies/{movieId}* - delete movie
  - **POST** */api/movies/{movieId}/add-actor/{actorId}* - add actor do movie
  - **DELETE** */api/movies/{movieId}/remove-actor/{actorId}* - remove actor from movie
  - **PATCH** */api/movies/{movieId}* - update existing movie
  - **GET** */api/movies/{movieId}* - fetch movie data by id
  - **GET** */api/movies/search?title=&sorted=* - fetch all movies data, params: title, sorted
  - **GET** */api/movies?sorted=* - fetch all movies data, params: sorted
  - **POST** */api/movies/rate/add* - add rate to movie
  - **DELETE** */api/movies/rate/remove/{movieId}* - remove rate from movie
    
**Topic**
  
Endpoints to menage topic.

- **POST** */api/topics* - create new topic
- **PATCH** */api/topics/{topicId}* - update existing topic
- **DELETE** */api/topics/{topicId}* - delete existing topic
- **GET** */api/topics/{topicId}* - fetch  topic data by id
- **GET** */api/topics?sorted=&page=&page-size=&entity-type=&entity-id=* - fetch all topics data, define sorting, params: sorted, page, page-size, entity-type, entity-id
- **GET** */api/topics/search?sorted=&title=* - fetch all topics data, params: sorted, topic title
- **GET** */api/topics/latest* - fetch latest topics
    
**Comment**
  
Endpoints to menage comments.

- **POST** */api/comments* - create new comment
- **PATCH** */api/comments/{commentId}* - update existing comment
- **DELETE** */api/comments/{commentId}* - delete existing comment
- **GET** */api/comments/user/{username}* - fetch all comments by username
- **GET** */api/comments/{commentId}* - fetch comment by id

  **User**

  - **GET** /api/user/{userId} - find user by id
  - **GET** /api/user/rated-movies-count?userId= - count movies rated by user, params - userId
  - **GET** /api/user/rated-movies?userId=&pageNo=&pageSize= - find movies rated by user, params: userId, pageNo, pageSize
  - **GET** /api/user/rated-directors?userId=&pageNo=&pageSize= - find directors rated by user, params: userId, pageNo, pageSize
  - **GET** /api/user/rated-actors?userId=&pageNo=&pageSize= - find actors rated by user, params: userId, pageNo, pageSize
  - **GET** */api/user/rated-actors-count?userId=* - count actors rated by user, params: userId
  - **GET** */api/user/rated-directors-count?userId=* - count directors rated by user, params: userId
  - **GET** */api/user/highest-rated-movies?userId=&pageNo=&pageSize=* - fetch highest rated movies by user, params: userId, pageNo, pageSize
  - **GET** */api/user/highest-rated-actors?userId=&pageNo=&pageSize=* - fetch highest rated actors by user, params: userId, pageNo, pageSize
  - **GET** */api/user/highest-rated-directors?userId=&pageNo=&pageSize=* - fetch highest rated directors by user, params: userId, pageNo, pageSize
  - **GET** */api/user/find-all?pageNo=&pageSize=* - find all users, params: pageNo, pageSize
  - **GET** */api/user/count* - count registered users
  - **POST** */api/user/ban* - ban user for given time
  - **POST** */api/user/add-role?username=&role=MODERATOR* - add role to user, params, username, role (MODERATOR)
  - **DELETE** */api/user/remove-role?username=&role=MODERATOR* - remove role from user, params: username, role(MODERATOR)
  - **POST** */api/user/remove-ban?username=* - remove ban from user, params: username
  - **GET** */api/user/average-movie-rate?userId=* - find average movie rate, params: userId
  - **GET** */api/user/most-rated-category?userId=* - find most rated category by user, params: userId

**Every request has prepared body. You just need to insert data you want and send request.**


![body](https://github.com/user-attachments/assets/770f6c04-b690-427d-a649-836686f6666b)


**Same with request parameters. Every parameter is prepared and described.**


![parameters](https://github.com/user-attachments/assets/7eaaacb6-503b-4061-8c56-a32a322676ef)


### Database client

You can use database terminal like **Sqlectron** to operate directly on the database.

- **Download Sqlectron** from https://sqlectron.github.io

  
- Open Sqlectron and configure database connetion like in the image below. Make sure to run the container first.
  

  ![connect to sqlectron](https://github.com/user-attachments/assets/d2957743-9af3-4fa4-916a-4f268a899838)
  

- Switch to the MoviePortal database and write queries.
- 

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








