package org.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        if (coordinate.length() == 0 || coordinate.length() > 3)
            throw new IllegalArgumentException("Expecting two characters [A-J][1-10]");
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
        if ((point1.row() != point2.row()) && (point1.col() != point2.col()))
            throw new IllegalArgumentException("Coordinates are not in-line");

        // if points in the same row the ship is horizontal, otherwise vertical
        int orientation = point1.row() == point2.row() ? 0 : 1;

        int length = (orientation == 0 ? Math.abs(point1.col() - point2.col()) : Math.abs(point1.row() - point2.row())) + 1;
        if (length != ship.getLength())
            throw new RuntimeException("Coordinates are not correct ship length (%d)".formatted(ship.getLength()));

        checkAndInsertShip(orientation, point1, point2, ship);
    }

    private void checkAndInsertShip(int orientation, Point point1, Point point2, Ship ship) {
        Point startPoint;
        if (orientation == 0) startPoint = point1.col() < point2.col() ? point1 : point2;
        else startPoint = point1.row() < point2.row() ? point1 : point2;

        int row = startPoint.row();
        int col = startPoint.col();
        for (int i = 0; i < ship.getLength(); i++) {
            if (board[row][col] == SHIP_BLOCK)
                throw new RuntimeException("A boat is already located here");
            if ((col > 0 && board[row][col - 1] == SHIP_BLOCK) ||
                    (col < 9 && board[row][col + 1] == SHIP_BLOCK) ||
                    (row > 0 && board[row - 1][col] == SHIP_BLOCK) ||
                    (row < 9 && board[row + 1][col] == SHIP_BLOCK))
                throw new RuntimeException("Invalid boat placement");
            if (orientation == 0) col++; // move right across columns horizontally
            else row++;                   // move down across rows vertically
        }
        row = startPoint.row();
        col = startPoint.col();
        for (int i = 0; i < ship.getLength(); i++) {
            board[row][col] = SHIP_BLOCK;
            shipLocationMap.put(new Point(row, col), ship);
            if (orientation == 0) col++;
            else row++;
        }
    }

    private static Point getPointFromInput(String input) {
        if (input.length() > 3) throw new IllegalArgumentException("Invalid coordinate");
        try { //[A-J][1-10] B2
            if (input.charAt(0) < 'A' || input.charAt(0) > 'J')
                throw new IllegalArgumentException("Invalid Coordinate");
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
            builder.append((char) (i + 'A'));
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
