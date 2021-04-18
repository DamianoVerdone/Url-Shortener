## Url Shortener


The project uses Java 11. (amazon corretto 11)

### Command-Line
 
 * Clean/Compile :: `./gradlew clean build`
 * Create Package :: `./gradlew jar`
 
 
 
 ### Run
* Server :: `java -jar ./server/build/lib/server-0.0.1.jar`
* Client :: `java -jar ./client/build/lib/client-0.0.1.jar`

### Docker
In the base root folder of the project there is a docker-compose.yml file.

It includes redis, activeMq and the Server application.

I left the client application outside because it was difficult interact with it otherwise.
Before to execute the docker compose command, build the jar package (`./gradlew jar`).

`docker-compose up`

### Shutdown
The server is using a server socket to receive the shutdown command 'X'

 `echo -n X | nc localhost 6347`

