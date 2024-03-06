package model;

import java.awt.*;

// Nested class representing a bullet in the game.
public class Fire extends Sprite {
    // ID of the player who shot the bullet.
    private int shooterId;

    // Constructor for creating a bullet with a given position, size, and shooter ID.
    public Fire(Point position, Dimension size, int shooterId) {
        this.setPosition(position);
        this.setSize(size);
        // Initial upward speed.
        this.setSpeed(new Point(0, 10));
        this.shooterId = shooterId;
    }

    // Getter method for retrieving the ID of the player who shot the bullet.
    public int getShooterId() {
        return shooterId;
    }
}
