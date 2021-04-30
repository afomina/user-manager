# User manager
## Installation and deployment
* Obtain cassandra docker image 
`docker pull cassandra`

* Run cassandra network
`docker run -p 9042:9042 -d --name nodeA --network cass-cluster-network cassandra`

* Create database 
`docker exec -it nodeA cqlsh`

```
CREATE  KEYSPACE IF NOT EXISTS user_manager
WITH REPLICATION = {'class':'SimpleStrategy','replication_factor':1};

USE user_manager;

create table if not exists user(
id uuid primary key,
email varchar,
password varchar,
first_name varchar,
last_name varchar,
avatar blob,
role int
);

create table if not exists user_email(
id uuid,
email varchar,
primary key (email, id)
);

begin batch 
insert into user (id, email, password, role) 
values (38752b70-a9c0-11eb-aab7-8903a043eed4, 'admin@test.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2); 
insert into user_email (id, email) values (38752b70-a9c0-11eb-aab7-8903a043eed4, 'admin@test.com'); 
apply batch;

QUIT;
```
* Run app on localhost:8080
`gradlew bootRun`

## API
#### Authorization
* `POST /login`
* Request `{email: "admin@test.com", password: "123456"}`
* Response `{token: "string"}`
* Use `token` from response in `Authorization` header for next requests
  
#### List all users 
* `GET /user`

#### Get user by id
* `GET /user/{id}`

#### Create user
* `POST /user`

    Request body:
  
    `{"email": "string", "password": "Base64 encoded string", 
    "firstName": "string", "lastName": "string",
    "role": "user|admin", "avatar": "Base64 encoded string"}`

#### Update user
* `PUT /user/{id}`

  Request body:

  `{"email": "string", "password": "Base64 encoded string",
  "firstName": "string", "lastName": "string",
  "role": "user|admin", "avatar": "Base64 encoded string"}`

#### Delete user
* `DELETE /user/{id}`
