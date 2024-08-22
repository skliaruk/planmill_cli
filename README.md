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
2. Create a `config.properties` file in the root directory with the following content:
    ```ini
    clientId=your_client_id
    clientSecret=your_client_secret
    apiUrl=https://online.planmill.com/:instance/api/1.5
    tokenUrl=https://online.planmill.com/:instance/api/oauth2/token
    authUrl=https://online.planmill.com/:instance/api/oauth2/authorize
    ```
3. Build the project:
    ```sh
    ./gradlew build
    ```
4. Execute the JAR file:
    ```sh
    java -jar build/libs/planmill_cli-1.0-SNAPSHOT.jar --projectId 123 --hours 8 --desc 'Worked on feature X'
    ```
    
## Example
To run the application with arguments:
```sh
./gradlew run --args="--projectId 123 --hours 8 --desc 'Worked on feature X'"
