package org.game;

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
