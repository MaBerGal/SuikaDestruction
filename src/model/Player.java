package model;

import java.awt.*;

// Nested class representing a player in the game.
public class Player extends Sprite {
    // Unique ID assigned to the player
    private int id;
    // Flag indicating whether the player is alive.
    private boolean isAlive;
    // Player's score in the game.
    private int score;
    // Player's name.
    private String playerName;

    // Constructor for creating a player with a given position, size, and name.
    public Player(Point position, Dimension size, String playerName) {
        this.setPosition(position);
        this.setSize(size);
        // Players start the game alive, obviously.
        this.isAlive = true;
        // Initial score is set to zero, obviously.
        this.score = 0;
        this.playerName = playerName;
    }

    // Getter method for retrieving the ID of the player.
    public int getId() {
        return this.id;
    }

    // Setter method for setting the ID of the player.
    public void setId(int id) {
        this.id = id;
    }

    // Getter method for checking whether the player is alive.
    public boolean isAlive() {
        return this.isAlive;
    }

    // Setter method for setting the player's alive status.
    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    // Getter method for retrieving the player's score.
    public int getScore() {
        return this.score;
    }

    // Method to increment the player's score by a specified number of points.
    public void incrementScore(int points) {
        this.score += points;
    }

    // Getter method for retrieving the player's name.
    public String getPlayerName() {
        return playerName;
    }

    // Setter method for setting the player's name.
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
