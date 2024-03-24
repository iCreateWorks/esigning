# e-Sign Microservice

e-Sign Microservice is an open-source solution that allows businesses to securely sign documents online. This service is designed to offer more control, flexibility, and cost savings compared to third-party e-Signing services. Here is the article which talks about this implementation in detail: [From Dependency to Autonomy: Building an In-House E-signing Service](https://www.infoq.com/articles/electronic-signing-service-cloud/)

## Table of Contents

- [Features](#features)
- [Demo](#demo)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Environment Variables](#environment-variables)


## Features

- Securely sign documents.
- Secured artifacts (Signed PDF Documents) that can’t be tampered with after digital signing.
- Compliance with the e-Sign Act and Regulatory Standards.
- Cost-efficient and scalable.
- Channel-agnostic design for seamless integration across platforms.
- Built with a tech stack including Spring Boot, Java, MySQL and other cloud services. 

## Demo
https://github.com/iCreateWorks/esigning/assets/50334500/49dddfee-c9bb-4ef8-a762-037bc38fa56c

https://github.com/iCreateWorks/esigning/assets/50334500/a4c77f27-8919-4f4d-84d6-260b922fb409

Sample loan agreement: [Master-Promissory-Note.pdf](https://github.com/iCreateWorks/esigning/files/14389744/Loan-Agreement.pdf)

## Prerequisites

- Java JDK 8 or higher
- MySQL
- AWS Account (for S3 storage)

## Setup & Installation

1. **Clone the repository:** Use the command git clone https://github.com/iCreateWorks/esigning.git to clone the repository to your local machine.

2. **Navigate to the project directory**.
 
3. **Install Maven dependencies:** Run _**'mvn install'**_ to install the necessary dependencies.
   
4. **Configure Environment Variables:** Set up the required environment variables as described in the [Environment Variables](#environment-variables) section. 

5. **Run the application:** Execute **_java -jar target/esigning-0.0.1-SNAPSHOT.jar_** to start the service.
   
## Environment Variables

To run this project, you will need to set the following environment variables:

- `SPRING_ESIGN_DB_URL`: JDBC URL for the database.
- `SPRING_ESIGN_DB_USER`: Database username.
- `SPRING_ESIGN_DB_PASS`: Database password.
- `S3_BUCKET_NAME`: S3 bucket name.
- `S3_BUCKET_REGION`: S3 bucket region.

Following is the sample application.properties files, where environment variables have been replaced with actual sample values
server.port=8081
server.servlet.context-path=/api/e-sign/v1
spring.esign.datasource.jdbc-url=jdbc:mysql://localhost:3306/e-sign?allowPublicKeyRetrieval=true&useSSL=false
spring.esign.datasource.username=e-sign
spring.esign.datasource.password=Password1
spring.esign.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.esign.datasource.minimumIdle=5
spring.esign.datasource.maxLifetime=120000
spring.jpa.database-platform = org.hibernate.dialect.MySQL5Dialect
spring.jpa.properties.hibernate.proc.param_null_passing=true

s3.bucket.name=esign-docs
s3.bucket.region=us-east-1

esign.entrust.keystore.passphrase=esignphasphrase
esign.entrust.cert.path=/jks/entrust.p12
esign.entrust.pdf.owner.password=esignpdfownerpassword

