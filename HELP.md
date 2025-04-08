# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.example.accessing-data-rest' is invalid and this project uses 'com.example.accessing_data_rest' instead.

* This version was used as a basis for a simple Spring application for CRUD access to data following  the HATEAOS for the
  course 02324 Advanced Topics for Software Engineering at DTU compute.

* Basically, it is enought to add and extend the data model in the package
  model using equipped with the right JPA/jakarta annotaions.

* In contrast to the original version of this project, this version is set
  up to save the data of H2 to a file, so that the data will be persisted
  over different runs of the backend. This file will be added to the top
  level folder db.

* Some dependencies were added to the pom file (like the use of HAL).

The rest of the original Help.md follows below unchanged.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.4/maven-plugin/build-image.html)
* [Rest Repositories](https://docs.spring.io/spring-boot/3.4.4/how-to/data-access.html#howto.data-access.exposing-spring-data-repositories-as-rest)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.4/reference/data/sql.html#data.sql.jpa-and-spring-data)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing JPA Data with REST](https://spring.io/guides/gs/accessing-data-rest/)
* [Accessing Neo4j Data with REST](https://spring.io/guides/gs/accessing-neo4j-data-rest/)
* [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

