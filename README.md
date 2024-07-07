# Game Service

## Overview
The Game Service is designed to handle player scores, push events to Kafka, and fetch top scores from the leaderboard service using Feign clients. It ensures secure communication with the leaderboard service via request interceptors and provides robust validation and exception handling.

## Features
- Posting player scores.
- Fetching top K scores for a game.
- Kafka integration for pushing score events.
- Secure communication with the leaderboard service.
- Validation and exception handling.

## Technologies Used
- Spring Boot
- Spring Cloud OpenFeign
- Spring Kafka
- Redis
- Gson
- Lombok

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven
- Redis
- Kafka

### Configuration
Configure the following properties in `application.properties`:

```properties
# Kafka properties
spring.kafka.bootstrap-servers=your_kafka_bootstrap_server
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.client-id=your_client_id
kafka.score-topic=your_score_topic

# Feign Client properties
leaderboard-service.secret-key=your_secret_key
leaderboardBaseUrl=http://localhost:8081

# Redis properties
spring.redis.host=localhost
spring.redis.port=6379

# Auth properties
auth.secret-key=your_secret_key

# Score limits
limit.max-score=100
```

Building and Running

1.	Clone the repository:
      git clone https://github.com/your-repo/game_service.git
      cd game_service
2.	Build the project:
      mvn clean install
3.	Run the application:
      mvn spring-boot:run


	•	Response:
	•	200 OK: If the score is successfully submitted.

Get Top K Scores

Retrieve the top K scores for a specific game.

	•	URL: /game/v1/score/{gameId}
	•	Method: GET
	•	Parameters:
	•	gameId (path): The ID of the game.
	•	k (query): The number of top scores to retrieve.
	•	Response:
	•	200 OK: A list of top K scores.

Code Structure

	•	config: Configuration classes for Feign clients, Kafka, and Redis.
	•	controller: REST controllers to handle API requests.
	•	dto: Data Transfer Objects.
	•	exception: Custom exceptions.
	•	interceptor: Interceptors for request handling.
	•	service: Business logic services.
	•	service.feignclients: Feign clients for external services.
	•	constant: Constants used across the application.

Running Tests

Unit tests are located in the src/test/java directory. To run the tests, use: ```mvn test```

Extending the Service

Adding a New Feign Client

	1.	Define the client interface in service.feignclients.
	2.	Annotate with @FeignClient and configure the URL in application.properties.

Adding a New Kafka Producer Configuration

	1.	Define the configuration in KafkaProducerConfig.
	2.	Create a new bean for KafkaProducerService.

Contributing

Feel free to fork the repository and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.


Contact

For any inquiries, please contact amirkpatna@gmail.com .