# TCP Server Documentation

## Overview
The TCP server is designed to facilitate real-time communication for the Mus Royale game. It manages connections, room matchmaking, and game flow for participants.

## Main Responsibilities
- Handle client connections and disconnections.
- Facilitate matchmaking for public and private rooms.
- Manage game state and flow between players.
- Process game actions and commands efficiently.

## Folder Structure
```
MusServerTCP/
├── MusServer/
│   ├── MusServer.csproj
│   ├── Program.cs
│   └── ...
└── README.md
```

## Breakdown of Major Sections in `MusServerTCP/MusServer/MusServer/Program.cs`

### Partida
This section manages the game sessions, including starting and ending games. It ensures that all necessary setup is done before players can participate.

### Bezeroak
Handles the client connections, enabling communication between clients and the server. This includes adding new clients and managing their disconnects.

### Matchmaking
This part is responsible for creating public and private game rooms. It matches players based on their preferences and current availability.

### Listener Loop on Port 13000
The listener loop listens for incoming connections on port 13000, accepting client requests and routing them to the appropriate handlers.

### Game Flow - PartidaHasi/IniciarPartida
Initiates a game session and sets up the initial game state, ensuring all players are synchronized before gameplay begins.

### Envido Handling - EnvidoKudeaketa/ProcesarErabakia
Processes 'envido' actions, determining the outcomes based on player decisions during this phase of the game.

### Deck/Card Helpers
Utilities that assist in managing the deck of cards and their related functionalities, ensuring smooth gameplay.

---

## Conclusion
This documentation provides insights into the architecture and functionality of the TCP server, ensuring developers can easily navigate and extend the server's capabilities.