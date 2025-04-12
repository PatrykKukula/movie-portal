# Movie Review Portal

Welcome to Movie Review Portal. This is REST API to store movies data alongside with actors and directors, and a place you can rate movies and have discussion about each movie.

## Features
* User registration, verifying account and password reset
* Create or update movie with its details
* Create or update actors and directors with their details
* Rating any movie
* Discussion section

## How to run service
**Clone repository**
```bash
git clone https://github.com/PatrykKukula/movie-review-portal.git
```
**Clone service image**
```bash
docker pull asteez/movieportalrestapi:v1.1
```
**Run the container**

Navigate to the service folder with **docker-compose.yml** file , for example
```bash
cd F:
cd moviereviewportal
```
And run the container:
```bash
docker compose up -d
```
**Send requests using Postman**

Import to Postman collection from this repository *MovieReviewPortal.postman_collection.json*

Click on the import button, and paste the file url or drop the file to the window:

![import](https://github.com/user-attachments/assets/55eeb03a-2656-4e4e-8700-617cc7faea43)

Collection has every point set up, with prepared authority, request body and parameters. List of endpoints below. 


![package](https://github.com/user-attachments/assets/403081c5-c958-4df7-b53a-c9e5d3f2d5b7)


There are two roles with different authorities. Any GET request does not need authentication.
**User** can set rate for any movie, create topic under each movie topic section and add comment in any topic.
**Admin** can do anything that user can do, edit user topics and comments, create/update/delete movies, actors and directors.

To perform request that need authentication, you must provide credentials under Auth section:

![authentication](https://github.com/user-attachments/assets/0ce82fd4-ece7-48b3-acf5-9fd489db20b7)


> **Note**  
> In the service, you can only create an user with the USER role.  
> To test endpoints allowed only for ADMIN role, use the pre-created admin account:  
> **Email:** admin@admin.pl  
> **Password:** Admin123!

**Auth**
  
Endpoint for user related actions. All endpoints do not need any authentication. 
  - register new user with role USER
  - verify account to set account enabled
  - reset password
  - send verification and password reset token

**Actor**
  
Endpoint to actor related actions.
  - create/update/delete actor - only admin
  - fetch actors details - no authentication
 
**Director**
  
Endpoint to director related actions.
  - create/update/delete director - only admin
  - fetch director details - no authentication

**Movies**
  
Endpoint to Movie related actions.
  - create/update/delete Movie - only admin
  - add/remove actor to/from movie - only admin
  - fetch movies details - no authentication
  - add/remove rate to/from movie - authentication needed
  - 
**Topic**
  
Endpoint to Topic related actions.
  - create/update/delete Topic - authentication needed
  - fetch topics details - no authentication
    
**Comment**
  
Endpoint to Comment related actions.
  - create/update/delete comment under topic - authentication needed
  - fetch comments details - no authentication

Every request have prepared body. You just need to insert data you want.

![body](https://github.com/user-attachments/assets/770f6c04-b690-427d-a649-836686f6666b)


Same with request parameters. Every parameter is prepared and described.


![parameters](https://github.com/user-attachments/assets/7eaaacb6-503b-4061-8c56-a32a322676ef)






