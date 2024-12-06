package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameRunner runner = new GameRunner();
        runner.run();
    }
}

class Board {

    char[][] board;
    private static final char FOG_OF_WAR = '~';
    private static final char SHIP_BLOCK = 'O';

    public Board() {
        this.board = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) this.board[i][j] = FOG_OF_WAR;
        }
    }

    public void placeShip(String[] coordinates, ShipType ship) {
        if (coordinates.length != 2) throw new IllegalArgumentException("Expecting 2 distinct coordinates");

        // get coordinates and validate input/boundaries
       int[] point1 = getCoordinatesFromInput(coordinates[0]);
       int[] point2 = getCoordinatesFromInput(coordinates[1]);

        // validate coordinates are in-line
        if ((point1[0] != point2[0]) && (point1[1] != point2[1])) throw new IllegalArgumentException("Coordinates are not in-line");

        int orientation;
        if (point1[0] == point2[0]) orientation = 0; // ship oriented horizontally
        else orientation = 1; // ship oriented vertically

        int length = (orientation == 0 ? Math.abs(point1[1] - point2[1]) : Math.abs(point1[0] - point2[0])) + 1;
        if (length != ship.length) throw new RuntimeException("Coordinates are not correct ship length (%d)".formatted(ship.length));

        // print length and location
        //System.out.printf("Length: %d", length);

        int[] startPoint;
        // iterate horizontally
        if (orientation == 0) {
            // start at point with smaller column value
            startPoint = point1[1] < point2[1] ? point1 : point2;
            for (int i = startPoint[1]; i < startPoint[1] + length; i++) {
                // row value is fixed, increment column
                if (board[startPoint[0]][i] == SHIP_BLOCK) throw new RuntimeException("A boat is already located here");
                if (i > 0 && board[startPoint[0]][i-1] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
                if (i < 9 && board[startPoint[0]][i+1] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
                if (startPoint[0] > 0 && board[startPoint[0] - 1][i] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
                if (startPoint[0] < 9 && board[startPoint[0] + 1][i] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
            }
            for (int i = startPoint[1]; i < startPoint[1] + length; i++) {
                // row value is fixed, increment column
                board[startPoint[0]][i] = SHIP_BLOCK;
            }
        } else { // iterate vertically
            // start at point with smaller row value
            startPoint = point1[0] < point2[0] ? point1 : point2;
            for (int i = startPoint[0]; i < startPoint[0] + length; i++) {
                // column value is fixed, increment row value
                if (board[i][startPoint[1]] == SHIP_BLOCK) throw new RuntimeException("A boat is already located here");
                if (i > 0 && board[i-1][startPoint[1]] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
                if (i < 9 && board[i+1][startPoint[1]] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
                if (startPoint[1] > 0 && board[i][startPoint[1] - 1] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
                if (startPoint[1] < 9 && board[i][startPoint[1] + 1] == SHIP_BLOCK) throw new RuntimeException("Boat placement out of bounds");
            }

            for (int i = startPoint[0]; i < startPoint[0] + length; i++) {
                // column value is fixed, increment row value
                board[i][startPoint[1]] = SHIP_BLOCK;
            }
        }
        System.out.println();
    }

    private static int[] getCoordinatesFromInput(String input) {
        input = input.trim();
        if (input.length() > 3) throw new IllegalArgumentException("Invalid coordinate");
        char rawRow = input.charAt(0);
        String rawCol = input.substring(1);
        try {
            int row = rawRow - 'A';
            int col = Integer.parseInt(rawCol) - 1;
            if (row < 0 || col < 0 || row >= 10 || col >= 10) throw new IllegalArgumentException("Coordinate is out of board boundaries");
            return new int[] {row, col};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    private static String getCoordinateString(int row, int col) {
        return String.format("%c%d", (row + 'A'), (col + 1));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for (int i = 1; i <= 10; i++) builder.append(" %d".formatted(i));
        builder.append("\n");
        for (int i = 0; i < 10; i++) {
            builder.append((char)(i + 'A'));
            for (int j = 0; j < 10; j++) {
                builder.append(" ").append(this.board[i][j]);
            }
            builder.append("\n");
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
    private Board board;
    public GameRunner() {
        this.board = new Board();
    }
    public void run() {
        placeShips();
        System.out.println(board);
    }
    private void placeShips(){
        String input;
        ShipType[] ships = ShipType.values();
        int shipIndex = 0;
        System.out.println(board);
        do {
            System.out.printf("Enter the coordinates of the %s (%d cells):\n", ships[shipIndex].name, ships[shipIndex].length);
            input = scanner.nextLine();
            String[] coordinates = input.split(" ");
            try {
                board.placeShip(coordinates, ships[shipIndex]);
                System.out.println(board);
                shipIndex++;
            } catch (IllegalArgumentException e) {
                System.out.println("Error : " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error runtime: " + e.getMessage());
            }
        } while (!input.equals("exit") && shipIndex < ships.length);
    }

}