# Java TCP Client-Server Wordle

Implementation of the **Wordle** game using a client-server architecture, communicating over TCP sockets with a custom text-based protocol. The server picks a random 5-letter word, the client has 6 attempts to guess it, and receives letter-by-letter hints after each guess.

## Requirements

- **Java 18** (JDK)

## Project structure

| File | Role |
|---|---|
| `WordleServer.java` | Server: listens on port `2779` and accepts client connections |
| `WordleClientHandler.java` | Handles one game per client (target word, remaining attempts, 20 s timeout) |
| `WordleClient.java` | Interactive console client (connects to `127.0.0.1:2779`) |
| `WordleProtocol.java` | Builds and parses protocol packets |
| `WordleAlgorithm.java` | Random word selection and hint computation (handles duplicate letters) |
| `WordleEnum.java` | Packet types (`TRY`, `CHEAT`, `QUIT`, `GAMEOVER`, `WRONG`, `NONEXISTENT`, `WORD`) |
| `WordleWordSet.java` | Dictionary of valid words ([source](https://github.com/tabatkins/wordle-list)) |

## Build and run

```bash
# Compile
javac *.java

# Terminal 1: start the server
java WordleServer

# Terminal 2: start the client
java WordleClient
```

Client menu:

```
1) Cheat              -> shows the word to guess
2) Propose a word     -> submit a 5-letter word
3) Quit               -> leave the game
```

## Protocol

Text packets terminated by `\r\n`.

**Client → Server**

| Packet | Description |
|---|---|
| `TRY <word>` | Submit a 5-letter word |
| `CHEAT` | Request the word to guess |
| `QUIT` | Close the connection |

**Server → Client**

| Packet | Description |
|---|---|
| `<hints>` | Result of a guess, e.g. `BGYBB` |
| `<hints> GAMEOVER` | End of game (win if `GGGGG`, otherwise out of attempts) |
| `<WORD>` | Response to `CHEAT` |
| `NONEXISTENT` | Word not in dictionary (attempt not counted) |
| `WRONG` | Malformed packet |

**Hints**

- `G` (Green): correct letter, correct position
- `Y` (Yellow): letter in the word, wrong position
- `B` (Black): letter not in the word

## Limitations

- The server handles clients **one at a time**: `WordleClientHandler` implements `Runnable` but `run()` is called directly, without a dedicated thread.
- Hardcoded address and port (`127.0.0.1:2779`).
- A connection idle for 20 seconds is closed (socket timeout).
