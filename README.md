# e-Sign Microservice

e-Sign Microservice is an open-source solution that allows businesses to securely sign documents online. This service is designed to offer more control, flexibility, and cost savings compared to third-party e-Signing services.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Environment Variables](#environment-variables)


## Features

- Securely sign documents online.
- Compliance with the e-Sign Act and Regulatory Standards.
- Cost-efficient and scalable.
- Built with a robust tech stack including Spring Boot, Java, AWS S3, and MySQL.
- Channel-agnostic design for seamless integration across platforms.

## Demo
https://github.com/iCreateWorks/esigning/assets/50334500/ef36b377-0027-4d46-ab17-dde21f8909c3

## Prerequisites

- Java JDK 8 or higher
- MySQL
- AWS Account (for S3 storage)

## Setup & Installation

1. Clone the repository:

2. Navigate to the project directory:
 
3. Install the required maven dependencies:
   
4. Set up the required [Environment Variables](#environment-variables).

5. Run the application:
   
## Environment Variables

To run this project, you will need to set the following environment variables:

- `SPRING_ESIGN_DB_URL`: JDBC URL for the database.
- `SPRING_ESIGN_DB_USER`: Database username.
- `SPRING_ESIGN_DB_PASS`: Database password.
- `S3_BUCKET_NAME`: S3 bucket name.
- `S3_BUCKET_REGION`: S3 bucket region.

