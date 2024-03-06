package model;

import java.awt.Dimension;
import java.awt.Point;

// Class that represents every graphical asset, containing its coordinates, size, and speed.
public class Sprite {
    // The position of the sprite on the screen (x, y coordinates).
    private Point position;

    // The size of the sprite (width and height).
    private Dimension size;

    // The speed of the sprite in terms of movement (dx, dy coordinates).
    private Point speed;

    // Method to set the position of the sprite.
    public void setPosition(Point position) {
        this.position = position;
    }

    // Method to set the size of the sprite.
    public void setSize(Dimension size) {
        this.size = size;
    }

    // Method to set the speed of the sprite.
    public void setSpeed(Point speed) {
        this.speed = speed;
    }

    // Method to get the current position of the sprite.
    public Point getPosition() {
        return position;
    }

    // Method to get the current size of the sprite.
    public Dimension getSize() {
        return size;
    }

    // Method to get the current speed of the sprite.
    public Point getSpeed() {
        return speed;
    }
}

