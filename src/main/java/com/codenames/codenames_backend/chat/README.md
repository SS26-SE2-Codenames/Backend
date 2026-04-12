## WebSocket Configuration (WebSocketConfig.java)

Sets up the real-time Pub/Sub messaging server.

* Connection Setup (registerStompEndpoints) * /ws-codenames: The URL clients use to establish the
  initial WebSocket connection.

    * CORS (*): Currently allows connections from any domain for local development (restrict this in
      production).

* Message Routing (configureMessageBroker)

    * /topic (Broadcasts): The outgoing channel. Messages routed here are immediately pushed to all
      subscribed clients.

    * /app (Server Logic): The incoming channel. Messages sent here are routed to our Java
      controllers for processing.

## The Message Controller (ChatController.java)

Acts as the bridge between incoming client messages and outgoing room broadcasts.

* Listening (@MessageMapping): Catches messages that clients send to /app/chat/{roomID}/sendMessage.

    * Processing: Automatically converts the incoming JSON into a ChatDTO.

* Broadcasting (@SendTo): Automatically takes the method's return value (ChatDTO) and pushes it out
  to /topic/room/{roomID} so everyone in the lobby receives the message.