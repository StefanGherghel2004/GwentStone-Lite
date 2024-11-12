package org.poo.board;

import lombok.Getter;
import lombok.Setter;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.player.Player;

import java.util.ArrayList;
@Getter
@Setter
public final class Board {
    private static final int ROWS = 4;

    final private ArrayList<ArrayList<MinionCard>> table;
    private Player playerOne;
    private Player playerTwo;

    // Default constructor that initializes the board with ROWS x COLUMNS
    public Board() {
        table = new ArrayList<>(ROWS);
        for (int i = 0; i < ROWS; i++) {
            ArrayList<MinionCard> row = new ArrayList<>();
            table.add(row);
        }
    }

    public Board(final Player playerOne, final Player playerTwo) {
        this();
        this.playerOne = new Player(playerOne);
        this.playerTwo = new Player(playerTwo);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public MinionCard getCardWithCoordinates(final int x, final int y) {
        // Adjust y index to 0-based for ArrayList indexing
        return table.get(x).get(y);
    }

    /**
     *
     * @param x
     * @param y
     */
    public void removeCardWithCoordinates(final int x, final int y) {
        table.get(x).remove(y);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean areCoordinatesWithinBounds(final int x, final int y) {
        if (x > table.size() - 1) {
            return false;
        }

        if (y > table.get(x).size() - 1) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param x
     * @param y
     * @param card
     */
    public void setCardWithCoordinates(final int x, final int y, final MinionCard card) {
        table.get(x).set(y - 1, card);
    }

    /**
     *
     * @param playerNumber
     * @return
     */
    public HeroCard getPlayerHero(final int playerNumber) {
        return playerNumber == 1 ? playerOne.getHeroCard() : playerTwo.getHeroCard();
    }

    /**
     *
     * @param playerIdx
     * @param hero
     */
    public void setHero(final int playerIdx, final HeroCard hero) {
        if (playerIdx == 1) {
            playerOne.setHeroCard(hero);
        } else if (playerIdx == 2) {
            playerTwo.setHeroCard(hero);
        }
    }

    /**
     *
     * @param playerIdx
     * @return
     */
    public Player getPlayer(final int playerIdx) {
        return playerIdx == 1 ? playerOne : playerTwo;

    }

    /**
     *
     * @param rowIndex
     * @return
     */
    public ArrayList<MinionCard> getRow(final int rowIndex) {
        return table.get(rowIndex);
    }

    public ArrayList<ArrayList<MinionCard>> getTable() {
        return table;
    }
}
