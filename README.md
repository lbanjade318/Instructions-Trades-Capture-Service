# Instructions Capture Service

This project matches the structure requested.

Structure:
- src/main/java/com/example/instructions
  - InstructionsCaptureApplication.java
  - controller/TradeController.java
  - service/TradeService.java
  - service/KafkaPublisher.java
  - service/KafkaListenerService.java
  - model/CanonicalTrade.java
  - model/PlatformTrade.java
  - util/TradeTransformer.java
  - store/InMemoryStore.java

Endpoints:
- POST /api/trades/upload (multipart file .csv or .json)


Build:
`mvn clean package`


How run application: 
1. Ensure you have Java and Maven installed.
2. Build the project using the command above.
3. Run Docker Compose up to start Kafka and Zookeeper:
   `docker-compose up -d`
4. Run the application using the command above.
5. Use tools like Postman or curl to interact with the endpoints.
6. Use sample-trades.csv or sample-trades.json to test file uploads.
- POST /api/trades/upload with file
