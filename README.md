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

QUIT;
```
* Run app on localhost:8080
`gradlew bootRun`

## API
#### List all users 
* `GET /user`

#### Get user by id
* `GET /user/{id}`

#### Create user
* `POST /user`

    Request body:
  
    `{"email": "string", "password": "string", 
    "firstName": "string", "lastName": "string",
    "role": "user|admin", "avatar": "Base64 encoded string"}`

#### Update user
* `PUT /user/{id}`

  Request body:

  `{"email": "string", "password": "string",
  "firstName": "string", "lastName": "string",
  "role": "user|admin", "avatar": "Base64 encoded string"}`

#### Delete user
* `DELETE /user/{id}`
