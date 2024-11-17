package org.poo.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    @Getter
    // the 4 x 5  array of cards
    private final ArrayList<ArrayList<MinionCard>> table;
    private Player playerOne;
    private Player playerTwo;

    // Default constructor that initializes the board with the empty rows
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
     * Getting the card with given coordinates on the table
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the MinionCard card at (x,y) on the table
     */
    public MinionCard getCardWithCoordinates(final int x, final int y) {
        return table.get(x).get(y);
    }

    /**
     *Removing the card with given coordinates on the table
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void removeCardWithCoordinates(final int x, final int y) {
        table.get(x).remove(y);
    }

    /**
     * Determines if given coordinates are valid for current table configuraiton
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the coordinates are valid
     *          false otherwise
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
     * Returning the hero of a player based on the index given
     * @param playerNumber the index of the player
     * @return the hero of player
     */
    public HeroCard getPlayerHero(final int playerNumber) {
        return playerNumber == 1 ? playerOne.getHeroCard() : playerTwo.getHeroCard();
    }

    /**
     *
     * @param playerIdx the given index ot the player
     * @return playerOne if the index is 1
     *         playerTwo otherwise (2)
     */
    public Player getPlayer(final int playerIdx) {
        return playerIdx == 1 ? playerOne : playerTwo;

    }

    /**
     *
     * @param rowIndex given index of the row
     * @return the ArrayList of MinionCard type at given index in the table
     */
    public ArrayList<MinionCard> getRow(final int rowIndex) {
        return table.get(rowIndex);
    }

    /**
     * Converts the table in a JSON representation (the cards on table)
     * @param mapper ObjectMapper to generate JSON
     * @return the JSON representation of the table
     */
    public ObjectNode toJson(final ObjectMapper mapper) {
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "getCardsOnTable");

        ArrayNode boardRows = mapper.createArrayNode();

        // if a row has no cards it will be printed as an empty arrayNode
        for (int rowIndex = 0; rowIndex < getTable().size(); rowIndex++) {
            ArrayList<MinionCard> boardRow = getRow(rowIndex);
            ArrayNode rowArray = mapper.createArrayNode();

            for (MinionCard card : boardRow) {
                ObjectNode cardNode = card.toJson(mapper);
                rowArray.add(cardNode);
            }

            boardRows.add(rowArray);
        }

        mainNode.set("output", boardRows);
        return mainNode;
    }

    /**
     * Converts a card with given coordinates to JSON
     * @param x x-coordinate
     * @param y y-coordinate
     * @param mapper  ObjectMapper for JSON representation
     * @return the representation  of the card with given coordinates
     */
    public ObjectNode getCardAtPositionJson(final int x, final int y,
                                            final ObjectMapper mapper) {
        ObjectNode responseNode = mapper.createObjectNode();

        if (!areCoordinatesWithinBounds(x, y)) {
            responseNode.put("command", "getCardAtPosition");
            responseNode.put("x", x);
            responseNode.put("y", y);
            responseNode.put("output", "No card available at that position.");
        } else {
            MinionCard card = getCardWithCoordinates(x, y);
            responseNode.put("command", "getCardAtPosition");
            responseNode.put("x", x);
            responseNode.put("y", y);

            if (card != null) {
                ObjectNode cardNode = card.toJson(mapper);
                responseNode.set("output", cardNode);
            } else {
                responseNode.put("output", "No card available at that position.");
            }
        }

        return responseNode;
    }

    /**
     *
     * @param mapper ObjectMapper for JSON representation
     * @return Object Node containing an ArrayNode of all frozen cards on table
     */
    public ObjectNode toJsonFrozen(final ObjectMapper mapper) {
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "getFrozenCardsOnTable");

        ArrayNode frozenCardsArray = mapper.createArrayNode();

        for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
            ArrayList<MinionCard> boardRow = table.get(rowIndex);

            for (MinionCard card : boardRow) {
                if (card.isFrozen()) {
                    ObjectNode cardNode = card.toJson(mapper);
                    frozenCardsArray.add(cardNode);
                }
            }
        }

        mainNode.set("output", frozenCardsArray);
        return mainNode;
    }

}
