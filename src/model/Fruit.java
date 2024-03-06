package model;

import java.awt.*;

// Nested class representing an fruit in the game.
public class Fruit extends Sprite {

    // Radius to determine the fruit's size.
    private int radius = 0;

    // Constructor for creating a fruit with a given position and size.
    public Fruit(Point position, Dimension size) {
        this.setPosition(position);
        this.setSize(size);
        // Initial downward speed.
        this.setSpeed(new Point(0, 5));
    }

    // Constructor for creating an fruit with a given position, speed, size, and radius.
    public Fruit(Point position, Point speed, Dimension size, int radius) {
        this(position, size);
        this.setSpeed(speed);
        this.setRadius(radius);
    }

    // Getter method for retrieving the radius of the fruit.
    public int getRadius() {
        return this.radius;
    }

    // Setter method for setting the radius of the fruit.
    public void setRadius(int radius) {
        this.radius = radius;
    }
}
