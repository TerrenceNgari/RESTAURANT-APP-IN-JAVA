# Restaurant App In Java

A browser-based restaurant ordering app built with Java, Spring Boot, and Thymeleaf. It lets staff open a local web page, create food orders, and update their preparation status from one dashboard.

## Features

- Browse the full menu in the browser
- Create dine-in, takeaway, or delivery orders
- Enter quantities for multiple menu items in one form
- View all submitted orders with totals and timestamps
- Update order status from received to completed

## Tech Stack

- Java 17
- Spring Boot 3
- Thymeleaf
- Maven
- JUnit 5

## Run The App

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Run Tests

```bash
mvn test
```

## Project Structure

```text
src/main/java/com/restaurant
|- model/
|- service/
`- web/

src/main/resources
|- static/css/
`- templates/
```

## Notes

- Orders are stored in memory while the app is running.
- Restarting the app clears existing orders.