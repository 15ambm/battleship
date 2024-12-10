package org.game;

import java.util.List;
import java.util.Scanner;

class GameRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private final Board playerOneBoard;
    private final Board playerTwoBoard;
    private boolean gameOver = false;
    private int currentPlayer = 1;

    public GameRunner() {
        this.playerOneBoard = new Board();
        this.playerTwoBoard = new Board();
    }

    public void run() {
        System.out.println("\nWelcome to Battleship CLI!" +
                "\nPlayer one will begin by entering the coordinates of their ships, followed by player 2." +
                "\nYou can see the game board below:");
        placeShips(playerOneBoard);
        waitForAndSetNextPlayer();
        placeShips(playerTwoBoard);
        waitForAndSetNextPlayer();
        beginGame();
    }

    private void placeShips(Board board) {
        String input;
        List<Ship> ships = board.getShips();
        int shipIndex = 0;
        board.printFullBoard();
        do {
            System.out.printf("\nPlayer %d, enter the coordinates of the %s (%d cells):\n> ", currentPlayer, ships.get(shipIndex).getName(), ships.get(shipIndex).getLength());
            input = scanner.nextLine();
            String[] coordinates = input.toUpperCase().trim().split("\\s+");
            try {
                board.placeShip(coordinates, ships.get(shipIndex));
                board.printFullBoard();
                shipIndex++;
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (!input.equals("exit") && shipIndex < ships.size());
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private void beginGame() {
        System.out.println("\nThe game starts!");
        while (!gameOver) {
            printBoards();
            System.out.printf("\nPlayer %d, it's your turn:", currentPlayer);
            attack();
            waitForAndSetNextPlayer();
        }
    }

    private void attack() {
        Board board = currentPlayer == 1 ? playerTwoBoard : playerOneBoard;
        System.out.print("\n> ");
        String input = scanner.nextLine();
        if (input.equals("exit")) System.exit(0);
        String coordinate = input.toUpperCase().trim();
        try {
            String actionResult = board.shootAtShip(coordinate);
            if (actionResult.equals("win")) {
                board.printFullBoard();
                System.out.printf("\nCongrats player %s, you sank the last ship! You win!\n", currentPlayer);
                System.exit(0);
            }
            System.out.println(actionResult);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            attack();
        }
    }

    private void waitForAndSetNextPlayer() {
        System.out.println("\nPress Enter and pass the move to another player");
        currentPlayer = currentPlayer == 1 ? 2 : 1;
        scanner.nextLine();
    }

    private void printBoards() {
        if (currentPlayer == 1) {
            playerTwoBoard.printBoardWithFog();
            System.out.print("---------------------");
            playerOneBoard.printFullBoard();
        } else if (currentPlayer == 2) {
            playerOneBoard.printBoardWithFog();
            System.out.print("---------------------");
            playerTwoBoard.printFullBoard();
        }
    }

}
