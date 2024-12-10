package org.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        GameRunner runner = new GameRunner();
        runner.run();
    }
}

class Board {
    private static final char FOG_OF_WAR = '~';
    private static final char SHIP_BLOCK = 'O';
    private static final char HIT_BLOCK = 'X';
    private static final char MISS_BLOCK = 'M';

    private final char[][] board;
    private final HashMap<Point, Ship> shipLocationMap;
    private final List<Ship> ships;
    private int totalHealth;

    public Board() {
        this.board = new char[10][10];
        this.ships = Arrays.stream(ShipType.values()).map(Ship::new).collect(Collectors.toList());
        this.totalHealth = Arrays.stream(ShipType.values()).mapToInt(ship -> ship.length).sum();
        this.shipLocationMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.board[i][j] = FOG_OF_WAR;
            }
        }
    }

    public String shootAtShip(String coordinate) {
        if (coordinate.length() == 0 || coordinate.length() >3) throw new IllegalArgumentException("Expecting two characters [A-J][1-10]");
        Point point = getPointFromInput(coordinate);
        char boardValue = board[point.row()][point.col()];
        switch (boardValue) {
            case SHIP_BLOCK -> {
                board[point.row()][point.col()] = HIT_BLOCK;
                this.totalHealth--;
                if (this.totalHealth == 0) return "win";
                shipLocationMap.get(point).decrementHealth();
                if (shipLocationMap.get(point).getHealth() == 0) return "\nYou sank a ship!";
                return "\nYou hit a ship!";
            }
            case FOG_OF_WAR -> {
                board[point.row()][point.col()] = MISS_BLOCK;
                return "\nYou missed!";
            }
            case MISS_BLOCK, HIT_BLOCK -> {
                return "\nAlready hit!";
            }
        }
        return "";
    }

    public void placeShip(String[] coordinates, Ship ship) {
        if (coordinates.length != 2) throw new IllegalArgumentException("Expecting 2 distinct coordinates");

        // get coordinates and validate input/boundaries
       Point point1 = getPointFromInput(coordinates[0]);
       Point point2 = getPointFromInput(coordinates[1]);

        // validate coordinates are in-line
        if ((point1.row() != point2.row()) && (point1.col() != point2.col())) throw new IllegalArgumentException("Coordinates are not in-line");

        // if points in the same row the ship is horizontal, otherwise vertical
        int orientation = point1.row() == point2.row() ? 0 : 1;

        int length = (orientation == 0 ? Math.abs(point1.col() - point2.col()) : Math.abs(point1.row() - point2.row())) + 1;
        if (length != ship.getLength()) throw new RuntimeException("Coordinates are not correct ship length (%d)".formatted(ship.getLength()));

        checkAndInsertShip(orientation, point1, point2, ship);
    }

    private void checkAndInsertShip(int orientation, Point point1, Point point2, Ship ship) {
        Point startPoint;
        if (orientation == 0) startPoint = point1.col() < point2.col() ? point1 : point2;
        else startPoint = point1.row() < point2.row() ? point1 : point2;

        int row = startPoint.row(); int col = startPoint.col();
        for (int i = 0; i < ship.getLength(); i++) {
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
        row = startPoint.row(); col = startPoint.col();
        for (int i = 0; i < ship.getLength(); i++) {
            board[row][col] = SHIP_BLOCK;
            shipLocationMap.put(new Point(row, col), ship);
            if (orientation == 0) col ++;
            else row++;
        }
    }

    private static Point getPointFromInput(String input) {
        if (input.length() > 3) throw new IllegalArgumentException("Invalid coordinate");
        try { //[A-J][1-10] B2
            if (input.charAt(0) < 'A' || input.charAt(0) > 'J') throw new IllegalArgumentException("Invalid Coordinate");
            int row = input.charAt(0) - 'A';
            int col = Integer.parseInt(input.substring(1)) - 1;
            if (row < 0 || col < 0 || row >= 10 || col >= 10) throw new IllegalArgumentException("Invalid coordinate");
            return new Point(row, col);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    public List<Ship> getShips() {
        return this.ships;
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

class Ship {
    private final ShipType type;
    private int health;

    public Ship(ShipType type) {
        this.type = type;
        this.health = type.length;
    }
    public String getName() {
        return this.type.name;
    }
    public int getLength() {
        return this.type.length;
    }
    public int getHealth() {
        return this.health;
    }
    public void decrementHealth() {
        this.health--;
    }
}

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
    private void placeShips(Board board){
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
            } catch (IllegalArgumentException e) {
                System.out.println("Error : " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error runtime: " + e.getMessage());
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

record Point(int row, int col) { }