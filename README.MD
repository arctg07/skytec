# Golden Clan

This project is a simple example of a gold management system for a fictional organization called "Clan." It includes the following components:

- **ClanRepository**: A repository for managing the clan's gold and initializing the database.

- **GoldProcessingService**: A service for updating the clan's gold balance based on requests from the queue.

- **GoldUpdateProducer**: A producer of requests to update the gold balance.

- **EventRepository**: A repository for recording events of gold balance changes.

- **EventProcessingService**: A service for processing and recording events of gold balance changes.

- **Main**: The main class demonstrating the system's operation.

## Dependencies

- Java 21 
- H2 library for database operations
