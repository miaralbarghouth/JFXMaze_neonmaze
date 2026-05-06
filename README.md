# JFXMaze - Futuristic Neon Labyrinth

JFXMaze is a desktop maze game developed with Java 17, JavaFX 21, and Maven. The project is organized for a university final submission: it includes clean source code, Maven configuration, required resources, authentication, local file processing, and clear documentation.

## Key Features

- User registration, login, and secure logout flow.
- Local account storage with salted SHA-256 password hashes.
- Persistent score storage and a leaderboard loaded from local disk.
- Random maze generation with a Depth-First Search based algorithm.
- Breadth-First Search hint system that calculates a path to the exit.
- JavaFX Canvas based game rendering.
- Adjustable maze size, music controls, sound effects, and account settings.
- Clean package structure separating models, logic managers, and UI classes.

## Distinctive Aspects

- Each game session generates a new maze instead of using static maps.
- The leaderboard ranking combines score, maze size, and completion time.
- Runtime data is stored outside the project folder, keeping the GitHub repository clean.
- The UI uses a futuristic neon style with transitions, background music, and sound effects.
- The project demonstrates algorithmic problem solving through DFS maze generation and BFS pathfinding.

## Requirements

- Java JDK 17 or newer
- Maven 3.8 or newer

## Installation and Running

```bash
git clone https://github.com/<your-username>/JFXneonmaze.git
cd JFXneonmaze
mvn javafx:run
```

You can also verify compilation first:

```bash
mvn clean compile
```

Default test account:

```text
Username: mk
Password: mk
```

## Controls

- `W`, `A`, `S`, `D` or arrow keys: Move the player.
- `H`: Show a BFS-based hint path.
- `ESC`: Pause the game.

## File Processing

The application stores runtime data in the `.jfxmaze` folder inside the user's home directory:

- `users.properties`: Usernames and hashed password records.
- `leaderboard.dat`: Saved score and leaderboard records.

These files are runtime data and should not be uploaded to GitHub or included in the final submission package.

## Submission Notes

The final submission should include the source code, `pom.xml`, `README.md`, and the required files under `src/main/resources`. Generated files such as `target/`, `.class`, `.jar`, `users.dat`, `leaderboard.dat`, and other runtime outputs should not be submitted.
