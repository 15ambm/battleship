package org.game;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameRunner runner = new GameRunner();
        runner.run();
    }
}

class Board {

    private final char[][] board;

    private static final char FOG_OF_WAR = '~';
    private static final char SHIP_BLOCK = 'O';
    private static final char HIT_BLOCK = 'X';
    private static final char MISS_BLOCK = 'M';

    public Board() {
        this.board = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.board[i][j] = FOG_OF_WAR;
            }
        }
    }

    public String shootAtShip(String coordinate) {
        if (coordinate.length() == 0 || coordinate.length() >3) throw new IllegalArgumentException("Expecting two characters [A-J][1-10]");
        int[] point = getCoordinatesFromInput(coordinate);
        char boardValue = board[point[0]][point[1]];
        switch (boardValue) {
            case SHIP_BLOCK -> {
                board[point[0]][point[1]] = HIT_BLOCK;
                return "\nYou hit a ship!";
            }
            case FOG_OF_WAR -> {
                board[point[0]][point[1]] = MISS_BLOCK;
                return "\nYou missed!";
            }
            case (MISS_BLOCK | HIT_BLOCK) -> {
                return "\nAlready hit!";
            }
        }
        return "";
    }

    public void placeShip(String[] coordinates, ShipType ship) {
        if (coordinates.length != 2) throw new IllegalArgumentException("Expecting 2 distinct coordinates");

        // get coordinates and validate input/boundaries
       int[] point1 = getCoordinatesFromInput(coordinates[0]);
       int[] point2 = getCoordinatesFromInput(coordinates[1]);

        // validate coordinates are in-line
        if ((point1[0] != point2[0]) && (point1[1] != point2[1])) throw new IllegalArgumentException("Coordinates are not in-line");

        // if points in the same row the ship is horizontal, otherwise vertical
        int orientation = point1[0] == point2[0] ? 0 : 1;

        int length = (orientation == 0 ? Math.abs(point1[1] - point2[1]) : Math.abs(point1[0] - point2[0])) + 1;
        if (length != ship.length) throw new RuntimeException("Coordinates are not correct ship length (%d)".formatted(ship.length));

        checkAndInsertShip(orientation, point1, point2, ship.length);
    }

    private void checkAndInsertShip(int orientation, int[] point1, int[] point2, int length) {
        int[] startPoint;
        if (orientation == 0) startPoint = point1[1] < point2[1] ? point1 : point2;
        else startPoint = point1[0] < point2[0] ? point1 : point2;

        int row = startPoint[0]; int col = startPoint[1];
        for (int i = 0; i < length; i++) {
            if (board[row][col] == SHIP_BLOCK)
                throw new RuntimeException("A boat is already located here");
            if ((col > 0 && board[row][col-1] == SHIP_BLOCK) ||
                (col < 9 && board[row][col+1] == SHIP_BLOCK) ||
                (row > 0 && board[row-1][col] == SHIP_BLOCK) ||
                (row < 9 && board[row+1][col] == SHIP_BLOCK) )
                throw new RuntimeException("Invalid boat placement");
            if (orientation == 0) col ++; // move right across columns horizontally
            else row++;                   // move down across rows vertically
        }
        row = startPoint[0]; col = startPoint[1];
        for (int i = 0; i < length; i++) {
            board[row][col] = SHIP_BLOCK;
            if (orientation == 0) col ++;
            else row++;
        }
    }

    private static int[] getCoordinatesFromInput(String input) {
        if (input.length() > 3) throw new IllegalArgumentException("Invalid coordinate");
        try { //[A-J][1-10] B2
            if (input.charAt(0) < 'A' || input.charAt(0) > 'J') throw new IllegalArgumentException("Invalid Coordinate");
            int row = input.charAt(0) - 'A';
            int col = Integer.parseInt(input.substring(1)) - 1;
            if (row < 0 || col < 0 || row >= 10 || col >= 10) throw new IllegalArgumentException("Invalid coordinate");
            return new int[] {row, col};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    private static String getCoordinateString(int row, int col) {
        return String.format("%c%d", (row + 'A'), (col + 1));
    }

    public String toString() {
        return getBoardString(false);
    }

    public void printBoardWithFog() {
        System.out.println(getBoardString(true));
    }

    public void printFullBoard() {
        System.out.println(getBoardString(false));
    }

    private String getBoardString(boolean withFog) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n ");
        for (int i = 1; i <= 10; i++) builder.append(" %d".formatted(i));
        builder.append("\n");
        for (int i = 0; i < 10; i++) {
            builder.append((char)(i + 'A'));
            for (int j = 0; j < 10; j++) {
                builder.append(" ");
                if (withFog && this.board[i][j] == SHIP_BLOCK) builder.append(FOG_OF_WAR);
                else builder.append(this.board[i][j]);
            }
            if (i < 9) builder.append("\n");
        }
        return builder.toString();
    }
}

enum ShipType {

    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    final int length;
    final String name;

    ShipType(String name, int length) {
        this.length = length;
        this.name = name;
    }
}

class GameRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private final Board board;
    public GameRunner() {
        this.board = new Board();
    }
    public void run() {
        placeShips();
        beginGame();
    }
    private void placeShips(){
        String input;
        ShipType[] ships = ShipType.values();
        int shipIndex = 0;
        board.printFullBoard();
        do {
            System.out.printf("\nEnter the coordinates of the %s (%d cells):\n> ", ships[shipIndex].name, ships[shipIndex].length);
            input = scanner.nextLine();
            String[] coordinates = input.toUpperCase().trim().split("\\s+");
            try {
                board.placeShip(coordinates, ships[shipIndex]);
                board.printFullBoard();
                shipIndex++;
            } catch (IllegalArgumentException e) {
                System.out.println("Error : " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error runtime: " + e.getMessage());
            }
        } while (!input.equals("exit") && shipIndex < ships.length);
    }

    private void beginGame() {
        String input;
        System.out.println("\nThe game starts!");
        board.printBoardWithFog();
        do {
            System.out.println("\nTake a shot!\n> ");
            input = scanner.nextLine();
            String coordinate = input.toUpperCase().trim();
            try {
                String actionResult = board.shootAtShip(coordinate);
                board.printBoardWithFog();
                System.out.println(actionResult);
                board.printFullBoard();
            } catch (IllegalArgumentException e) {
                System.out.println("Error : " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error runtime: " + e.getMessage());
            }
        } while (!input.equals("exit"));
    }

}