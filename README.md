# RestAssured Automation Project

This repository contains three independent modules demonstrating different approaches to API automation using **RestAssured**. Each module is self-contained and showcases various testing strategies, from basic scripting to advanced BDD frameworks.

---

## Project Structure

The project is organized into three main modules:

1.  **RestBasics**: Basic RestAssured implementation with TestNG.
2.  **APIFramework**: A BDD-driven framework using Cucumber and JUnit.
3.  **JiraAPiExcel**: An advanced BDD framework for Jira API automation with ExtentReports and TestNG integration.

---

## Modules Overview

### 1. RestBasics
This module focuses on the core concepts of RestAssured and is ideal for understanding the basics of API testing.
- **Testing Tool**: TestNG
- **Key Features**:
    - Simple GET/POST/PUT/DELETE requests.
    - JSON Schema Validation.
    - Use of POJOs for Serialization and Deserialization (Jackson/Gson).
    - Hamcrest matchers for assertions.
    - Lombok for reducing boilerplate code.
- **Tech Stack**: Java 21, Maven, RestAssured, TestNG, Jackson, Lombok, Hamcrest.

### 2. APIFramework
A Behavior-Driven Development (BDD) approach to API testing.
- **Testing Tool**: Cucumber (JUnit Runner)
- **Key Features**:
    - Gherkin feature files for test scenarios.
    - Step definitions for reusable code.
    - Serialization/Deserialization using Jackson.
    - Logging capabilities.
- **Tech Stack**: Java, Maven, RestAssured, Cucumber Java, Cucumber JUnit, Jackson, Lombok.

### 3. JiraAPiExcel
A specialized BDD framework designed for Jira API interactions, featuring robust reporting.
- **Testing Tool**: Cucumber (TestNG Runner)
- **Key Features**:
    - Jira API automation.
    - Advanced reporting using **ExtentReports**.
    - Dependency Injection with Cucumber Picocontainer.
    - TestNG integration for parallel execution and suite management.
    - Logging with Log4j.
- **Tech Stack**: Java 21, Maven, RestAssured, Cucumber (Java, TestNG, Core), ExtentReports, Lombok, Apache Commons IO.

---

## Getting Started

### Prerequisites
- JDK 21
- Maven 3.x
- An IDE (IntelliJ IDEA or Android Studio recommended)

### Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. Navigate to the root directory:
   ```bash
   cd RestAssured
   ```

### Running Tests

Since these are independent Maven modules, you can run tests for each module separately.

#### Running RestBasics:
```bash
cd RestBasics
mvn test
```

#### Running APIFramework:
```bash
cd APIFramework
mvn test
```

#### Running JiraAPiExcel:
```bash
cd JiraAPiExcel
mvn test
```

---

## License
This project is for educational and demonstration purposes.
