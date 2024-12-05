package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.run();
    }
}

class Board {

    char[][] board;
    private static final char FOG_OF_WAR = '~';
    private static final char PLAYER_1 = '1';
    private static final char PLAYER_2 = '2';

    public Board() {
        this.board = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) this.board[i][j] = FOG_OF_WAR;
        }
    }

    public boolean placeShip(String[] coordinates) {
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

        // print length and location
        System.out.printf("Length: %d", length);

        int[] startPoint;
        System.out.print("\nParts: ");
        // print horizontally
        if (orientation == 0) {
            // start at point with smaller column value
            startPoint = point1[1] < point2[1] ? point1 : point2;
            for (int i = startPoint[1]; i < startPoint[1] + length; i++) {
                // row value is fixed, increment column
                System.out.printf("%s ",Board.getCoordinateString(startPoint[0], i));
                board[startPoint[0]][i] = PLAYER_1;

            }
        } else { // print vertically
            // start at point with smaller row value
            startPoint = point1[0] < point2[0] ? point1 : point2;
            for (int i = startPoint[0]; i < startPoint[0] + length; i++) {
                // column value is fixed, increment row value
                System.out.printf("%s ",Board.getCoordinateString(i, startPoint[1]));
                board[i][startPoint[1]] = PLAYER_1;
            }
        }
        System.out.println();
        return true;
    }

    private int[] getCoordinatesFromInput(String input) {
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

    public static String getCoordinateString(int row, int col) {
        StringBuilder builder = new StringBuilder();
        builder.append((char) (row + 65));
        builder.append(col + 1);
        return builder.toString().trim();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for (int i = 1; i <= 10; i++) builder.append(" %d".formatted(i));
        builder.append("\n");
        for (int i = 0; i < 10; i++) {
            builder.append(Character.toString(i + 'A'));
            for (int j = 0; j < 10; j++) {
                builder.append(" ").append(this.board[i][j]);
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}

class Runner {
    private static final Scanner scanner = new Scanner(System.in);
    public Runner() {}
    public void run() {
        Board board = new Board();
        System.out.println(board);

        System.out.println("Enter the coordinates of the ship:");
        String input = scanner.nextLine();
        String[] coordinates = input.split(" ");
        try {
            board.placeShip(coordinates);
        } catch (IllegalArgumentException e) {
            System.out.println("Error!");
        }
        //System.out.println(board);
    }
}