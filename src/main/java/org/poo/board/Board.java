package org.poo.board;

import lombok.Getter;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.player.Player;

import java.util.ArrayList;
import java.util.Collection;

public class Board {
    private static final int ROWS = 4;
    private static final int COLUMNS = 5;

    private ArrayList<ArrayList<MinionCard>> table;
    @Getter
    private Player playerOne;
    @Getter
    private Player playerTwo;

    // Default constructor that initializes the board with ROWS x COLUMNS
    public Board() {
        table = new ArrayList<>(ROWS);
        for (int i = 0; i < ROWS; i++) {
            ArrayList<MinionCard> row = new ArrayList<>(COLUMNS);
            for (int j = 0; j < COLUMNS; j++) {
                row.add(null);  // Initialize each cell to null
            }
            table.add(row);
        }
    }

    public Board(Player PlayerOne, Player PlayerTwo) {
        this();
        this.playerOne = new Player(PlayerOne);
        this.playerTwo = new Player(PlayerTwo);
    }

    // Method to get a card at specific coordinates (x, y)
    public MinionCard getCardWithCoordinates(int x, int y) {
        // Adjust y index to 0-based for ArrayList indexing
        return table.get(x).get(y - 1);
    }

    // Method to set a card at specific coordinates (x, y)
    public void setCardWithCoordinates(int x, int y, MinionCard card) {
        table.get(x).set(y - 1, card);
    }

    // Method to get the hero for a specific player
    public HeroCard getPlayerHero(int playerNumber) {
        return playerNumber == 1 ? playerOne.getHeroCard() : playerTwo.getHeroCard();
    }

    public void setHero(int playerIdx, HeroCard hero) {
        if (playerIdx == 1) {
            playerOne.setHeroCard(hero);
        } else if (playerIdx == 2) {
            playerTwo.setHeroCard(hero);
        }
    }
    public Player getPlayer(int playerIdx) {
        return playerIdx == 1 ? playerOne : playerTwo;

    }

    public ArrayList<MinionCard> getRow(int rowIndex) {
        return table.get(rowIndex);
    }

    public ArrayList<ArrayList<MinionCard>> getTable() {
        return table;
    }
}
