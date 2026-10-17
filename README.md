📅 NextDate

Note: This project is currently a Work In Progress. The core logic and API are implemented, but additional features (like the notification system) are still under development.

NextDate is a Spring Boot web application designed to help users efficiently track and manage recurring events, tasks, and appointments. The application automatically calculates the date of the next occurrence based on user-defined intervals (days, weeks, months, or years), acting as the single source of truth for your schedule.

✨ Features

User Authentication: Secure registration and login system using JSON Web Tokens (JWT).

Appointment Management: Full CRUD (Create, Read, Update, Delete) operations for appointments.

Auto-Calculation Engine: Automatically determines the nextDate of an event based on the previousDate and a specified timePeriod and timeUnit.

Data Isolation: Users can only view and manage their own appointments.

Dockerized: Ready to be deployed and run locally using Docker and Docker Compose.

🛠️ Tech Stack

Backend: Java 21, Spring Boot 3

Security: Spring Security, JWT (JSON Web Tokens), BCrypt

Database: MySQL 8, Spring Data JPA, Hibernate

Containerization: Docker, Docker Compose

Build Tool: Maven

🚀 Getting Started

Prerequisites

Docker and Docker Compose installed on your machine.

(Optional) Java 21 and Maven if you wish to run the app directly on your host.

Running with Docker (Recommended)

Clone the repository:

git clone https://github.com/yourusername/next-date.git
cd next-date


Start the application and the MySQL database using Docker Compose:

docker-compose up -d --build


The API will be available at http://localhost:8080.

📡 API Endpoints Overview

Here is a quick overview of the available REST API endpoints:

Authentication (/api/auth)

POST /api/auth/register - Register a new user account.

POST /api/auth/login - Authenticate a user and receive a JWT token.

Users (/api/users)

GET /api/users/me - Get details of the currently authenticated user (requires JWT).

Appointments (/api/appointments) - Requires JWT

GET /api/appointments - Get all appointments for the logged-in user.

POST /api/appointments - Create a new appointment.

PUT /api/appointments/{id} - Update an existing appointment.

DELETE /api/appointments/{id} - Delete an appointment.

