# spring-docker
a simple spring project which implements spring security that wrapped by docker-compose

## Run the project

### 1.Build JAR file
Before run the containers it's necessary to build the project:

#### Step 1
![first_step](spring-docker/docs/groupMessageImage/first_step.png)


#### Step 2
![second_step](docs/groupMessageImage/second_step.png)



#### Step 3
![third_step](docs/groupMessageImage/third_step.png)


#### Step 4
![fourth_step](docs/groupMessageImage/fourth_step.png)




### 2. Run via Docker compose
Run the bundle of the application and the database:
bash
docker compose up --build


The application will be available in the address: http://localhost:8080
The database (Postgres) is available locally on the port: 5433

