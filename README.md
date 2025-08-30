
# book-management

 :rotating_light::no_entry: **IN CONSTRUCCTION**
Upgrade of [SpringBasicBooksManagement](https://github.com/Ivan-Montes/SpringBasicBooksManagement) project with different approaches

## Table of contents

- [Summary](#summary)
- [Requirements](#requirements)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [License](#license)


## Summary

#### Variants

| Module Name | Architecture | Programming Style | UI Framework |
|-----|-----|-----|-----|
| book-mgmt-basic-imperative-react | 3 Layers | imperative | react  + JS |
| book-mgmt-hex-imperative-react | hexagonal | imperative | react + TS | 
| book-mgmt-hex-reactive-react | hexagonal | reactive | react + TS | 


#### Description

- book-mgmt-basic-imperative-react
Monolithic architecture MVC with Spring Framework for managing a book collection.

- book-mgmt-hex-imperative-react
Maven project with Hexagonal Arch, CQRS, Event Sourcing and Kafka for synchronizing databases. You should also run the project's Docker Compose file.

- book-mgmt-hex-reactive-react
Maven project is built with Reactive Programming, Hexagonal Arch, CQRS, Event Sourcing and Kafka for synchronizing databases. It's necessary to run the docker-compose file 
included inside the project folder
     

## Requirements

[Docker Compose](https://docs.docker.com/compose/install/) is required to run certain services

Working with React requires that Node.js is installed. We can find the installation instructions on the [Node.js download page](https://nodejs.org/en/download).

   
## Usage
  
Some projects require execute the docker compose file located in the same project folder

```    
    **Developer mode: start**  
    docker-compose -f docker-compose-dev.yml up -d
    
     **Developer mode: stop**
    docker-compose -f docker-compose-dev.yml down --rmi local -v

```

Each project has a folder called frontend. It's located in the main directory, inside the src folder. From there, run Node, it listens for web requests on port 3000

```
npm run dev
```
  
  
## Maintainers

Just me, [Iván](https://github.com/Ivan-Montes) :sweat_smile:


## License

[GPLv3 license](https://choosealicense.com/licenses/gpl-3.0/)


---


[![Java](https://badgen.net/static/JavaSE/21/orange)](https://www.java.com/es/)
[![Maven](https://badgen.net/badge/icon/maven?icon=maven&label&color=red)](https://https://maven.apache.org/)
[![Spring](https://img.shields.io/badge/spring-blue?logo=Spring&logoColor=white)](https://spring.io)
[![GitHub](https://badgen.net/badge/icon/github?icon=github&label)](https://github.com)
[![Eclipse](https://badgen.net/badge/icon/eclipse?icon=eclipse&label)](https://https://eclipse.org/)
[![SonarQube](https://badgen.net/badge/icon/sonarqube?icon=sonarqube&label&color=purple)](https://www.sonarsource.com/products/sonarqube/downloads/)
[![Docker](https://badgen.net/badge/icon/docker?icon=docker&label)](https://www.docker.com/)
[![Kafka](https://badgen.net/static/Apache/Kafka/cyan)](https://kafka.apache.org/)
[![GPLv3 license](https://badgen.net/static/License/GPLv3/blue)](https://choosealicense.com/licenses/gpl-3.0/)
