# Sudoku Game with JavaFX and RMI Server

This Java application is a Sudoku game implemented using JavaFX for the graphical user interface and RMI (Remote Method Invocation) for server-client communication. This project aims to demonstrate proficiency in Java object-oriented programming and networking concepts.



## Features

- **Sudoku Game**: Users can play Sudoku puzzles directly through a graphical interface.
- **RMI Server**: Generates Sudoku puzzles and distributes them to clients upon request.
- **Statistics Tracking**: The server keeps track of statistics such as the number of puzzles generated and solved by clients.

## Requirements

- Java Development Kit 21
- JavaFX

## Setup

1. Clone this repository to your local machine.
2. Make sure you have the JDK and JavaFX installed.
3. Run the server: `java Server`
4. Run the client: `java Client`

## How to Play

1. Start the client application.
2. Connect to the server.
3. Request a Sudoku puzzle from the server.
4. Play the game by filling in the empty cells with numbers from 1 to 9 such that each row, column, and 3x3 subgrid contains all the digits from 1 to 9.
5. Once completed, submit your name to the server.
6. Enjoy playing more Sudoku puzzles!

## Folder Structure

- `src/`: Contains the source code for the Sudoku game and RMI server.
- `src/client/`: Client-side code including the entire sudoku logic.
- `src/server/`: Server-side code including sudoku generator.
- `src/types/`: Data types used in the code.
- `src/resources/`: Includes .fxml and .css files for the client. 
- `README.md`: This file provides instructions and information about the project.

## Contributors

- Rangel Plachkov
