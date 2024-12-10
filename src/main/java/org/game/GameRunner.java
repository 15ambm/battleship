package org.game;

import java.util.List;
import java.util.Scanner;

class GameRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private final Board playerOneBoard;
    private final Board playerTwoBoard;
    private boolean gameOver = false;

    public GameRunner() {
        this.playerOneBoard = new Board();
        this.playerTwoBoard = new Board();
    }

    public void run() {
        placeShips(playerOneBoard);
        placeShips(playerTwoBoard);
        waitForNextPlayer();
        beginGame();
    }

    private void placeShips(Board board) {
        String input;
        List<Ship> ships = board.getShips();
        int shipIndex = 0;
        board.printFullBoard();
        do {
            System.out.printf("\nEnter the coordinates of the %s (%d cells):\n> ", ships.get(shipIndex).getName(), ships.get(shipIndex).getLength());
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
    }

    private void beginGame() {
        System.out.println("\nThe game starts!");
        int currentPlayer = 1;

        while (!gameOver) {
            printBoards(currentPlayer);
            System.out.printf("\nPlayer %d, it's your turn:", currentPlayer);
            attack(currentPlayer);
            waitForNextPlayer();
            currentPlayer = currentPlayer == 1 ? 2 : 1;
        }
    }

    private void attack(int currentPlayer) {
        Board board = currentPlayer == 1 ? playerTwoBoard : playerOneBoard;
        System.out.print("\n> ");
        String input = scanner.nextLine();
        String coordinate = input.toUpperCase().trim();
        try {
            String actionResult = board.shootAtShip(coordinate);
            if (actionResult.equals("win")) {
                board.printFullBoard();
                System.out.println("\nYou sank the last ship. You won. Congratulations!");
                this.gameOver = true;
                return;
            }
            System.out.println(actionResult);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            attack(currentPlayer);
        }
    }

    private void waitForNextPlayer() {
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
    }

    private void printBoards(int currentPlayer) {
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
