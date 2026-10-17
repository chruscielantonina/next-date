# 📅 NextDate

Note: This project is currently a Work In Progress. The core logic and API are implemented, but additional features (like the notification system) are still under development.

NextDate is a Spring Boot web application designed to help users efficiently track and manage recurring events, tasks, and appointments. The application automatically calculates the date of the next occurrence based on user-defined intervals (days, weeks, months, or years), acting as the single source of truth for your schedule.

---

## How to run
Ensure you have the following tools installed on your system:
- **Git** (to clone the repository)
- **Docker** and **Docker Compose** (installing **Docker Desktop** is highly recommended)

### 1. Start Docker
Launch Docker Desktop and verify that the engine is running and the status shows as **Connected**.

### 2. Clone the Repository
Open your terminal and execute the following commands to download the project and navigate into its directory:

`git clone https://github.com/chruscielantonina/next-date
cd next-date`

### 3. Build and Run
In the same terminal (make sure you are inside the next-date directory), start the application and database containers in the background using Docker Compose:

`docker-compose up -d --build`

### 4. Access the Application
Once the containers are successfully running, the API will be accessible at:

`http://localhost:8080`

---

## Features

User Authentication: Secure registration and login system using JSON Web Tokens (JWT).

Appointment Management: Full CRUD (Create, Read, Update, Delete) operations for appointments.

Auto-Calculation Engine: Automatically determines the nextDate of an event based on the previousDate and a specified timePeriod and timeUnit.

Data Isolation: Users can only view and manage their own appointments.

Dockerized: Ready to be deployed and run locally using Docker and Docker Compose.

---

## Tech Stack

* **Backend:** Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA
* **Database:** MySQL 8.0
* **Frontend:** Vanilla HTML, CSS, JavaScript (served as static resources)
* **DevOps:** Docker, Docker Compose, Maven
