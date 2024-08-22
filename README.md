# Planmill CLI

## Description
Planmill CLI is a command-line interface tool for interacting with the Planmill API. It allows users to create timereports

## Prerequisites
- JDK 17 or higher
- Gradle 7.0 or higher

## Installation
1. Clone the repository:
    ```sh
    git clone https://github.com/skliaruk/planmill_cli.git
    cd planmill_cli
    ```

2. Build the project:
    ```sh
    ./gradlew build
    ```
3. Execute the JAR file:
    ```sh
    java -jar build/libs/planmill_cli-1.0-SNAPSHOT.jar --projectId 123 --hours 8 --desc 'Worked on feature X'
    ```
    
## Example
To run the application with arguments:
```sh
./gradlew run --args="--projectId 123 --hours 8 --desc 'Worked on feature X'"
