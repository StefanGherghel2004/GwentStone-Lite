package org.poo.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.board.Board;
import org.poo.cards.MinionCard;

import java.util.ArrayList;

import static org.poo.inputhandler.InputHandler.MAX_STAMINA;

@Getter
@Setter
public final class Game {
    // board representing the game state, containing the table
    // and the players with their decks and hands
    private Board board;
    private int turn;
    // flags to indicate if the player's round has ended
    private boolean playerOneRoundEnded;
    private boolean playerTwoRoundEnded;
    // current round number
    private int numRound;
    // stats that are updated after each completed game
    private int playerOneWins;
    private int playerTwoWins;
    private int gamesPlayed;

    public Game(final Board board, final int turn) {
        this.board = board;
        this.turn = turn;
    }

    public Game() {

    }

    public  boolean getPlayerOneRoundEnded() {
        return playerOneRoundEnded;
    }

    public boolean getPlayerTwoRoundEnded() {
        return playerTwoRoundEnded;
    }

    /**
     * Checks if the card at coordinates (x, y) belongs to the enemy of current player
     * @param x x-coordinate of the card
     * @param y y-coordinate of the card
     * @return true if the card belongs to the enemy, false otherwise
     */
    public boolean isEnemyCard(final int x, final int y) {

        boolean isValid = board.areCoordinatesWithinBounds(x, y);
        if (!isValid) {
            return false;
        }
        int cardOwner;

        if (x == 3 || x == 2) {
            cardOwner = 1;
        } else  {
            cardOwner = 2;
         }


        return (turn == 1 && cardOwner == 2) || (turn == 2 && cardOwner == 1);
    }

    /**
     *  gets the front row of cards for a specified player.
     * @param playerIdx the player index (1 or 2)
     * @return ArrayList of MinionCards representing the front row
     */
    public ArrayList<MinionCard> getFrontRow(final int playerIdx) {
        if (playerIdx == 1) {
            return this.getBoard().getTable().get(2);
        }
        return this.getBoard().getTable().get(1);
    }

    /**
     * checks if the specified player has a Tank card in their front row.
     * @param playerIdx playerIdx - The player index (1 or 2)
     * @return true if the player has a Tank card in their front row, false otherwise
     */
    public boolean hasTankCard(final int playerIdx) {
        ArrayList<MinionCard> cards = this.getFrontRow(playerIdx);
        for (MinionCard card : cards) {
            if (card.isTank()) {
                return true;
            }
        }
        return false;
    }

    /**
     *  increases the number of games played by 1
     */
    public void incGamesPlayed() {
        gamesPlayed++;
    }

    /**
     *   increases by 1 the win count for the specified player.
     * @param idx the player index (1 or 2)
     */
    public void incPlayerWins(final int idx) {
        if (idx == 1) {
            playerOneWins++;
        } else {
            playerTwoWins++;
        }

    }

    /**
     *  sets the statistics of the game (games played and wins for each player).
     * @param games total number of games played
     * @param oneWins  number of wins for Player 1
     * @param twoWins   number of wins for Player 2
     */
    public void setStats(final int games, final int oneWins, final int twoWins) {
        setGamesPlayed(games);
        setPlayerOneWins(oneWins);
        setPlayerTwoWins(twoWins);
    }

    /**
     * converts the current turn (1 or 2) to a JSON object
     * @param mapper  ObjectMapper for converting objects to JSON
     * @return objectNode representing the current player's turn in JSON format
     */
    public ObjectNode getTurnToJson(final ObjectMapper mapper) {
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "getPlayerTurn");
        mainNode.put("output", turn);
        return mainNode;
    }

    /**
     * Converts the current turn to the enemy turn
     * @param givenTurn current player turn (1 or 2)
     * @return the enemy turn (1 or 2)
     */
    public int turnToEnemy(final int givenTurn) {
        if (givenTurn == 1) {
            return 2;
        }
        return 1;
    }

    /**
     *  ends the current player's turn and updates game state for the next round.
     */
    public void endTurn() {
        if (turn == 1) {
            // unfreeze front and back row for player one
            unfreezeMinions(2);  // front row for player 1
            unfreezeMinions(3);  // back row for player 1

            // switch turn to player two
            setTurn(2);
            setPlayerTwoRoundEnded(true);
        } else if (turn == 2) {
            // unfreeze front and back row for player two
            unfreezeMinions(1);  // front row for player 2
            unfreezeMinions(0);  // back row for player 2

            // switch turn to player one
            setTurn(1);
            setPlayerOneRoundEnded(true);
        }

        // if both players have ended their round, proceed to the next round
        if (playerOneRoundEnded == playerTwoRoundEnded) {
            setPlayerTwoRoundEnded(false);
            setPlayerOneRoundEnded(false);
            setNumRound(numRound + 1);

            // increase mana for both players, ensuring no more than MAX_STAMINA
            increaseManaForBothPlayers();

            // draw cards if the deck is not empty
            drawCards();
        }
    }

    /**
     *  initializes the first round by drawing one card for each player
     *   and setting the initial round settings
     */
    public void setFirstRound() {
        // draw one card for each player from their decks
        MinionCard oneCardDraw = board.getPlayer(1).getUsingDeck().getCards().get(0);
        MinionCard twoCardDraw = board.getPlayer(2).getUsingDeck().getCards().get(0);

        // add drawn cards to players' hands
        board.getPlayer(1).getHand().add(oneCardDraw);
        board.getPlayer(2).getHand().add(twoCardDraw);

        // remove the drawn cards from the decks
        board.getPlayer(1).getUsingDeck().getCards().removeFirst();
        board.getPlayer(2).getUsingDeck().getCards().removeFirst();

        // set initial mana for both players
        board.getPlayer(1).setMana(1);
        board.getPlayer(2).setMana(1);

        // set the round number to 1
        numRound = 1;
    }

    /**
     *  unfreezes all minions in the specified row.
     * @param rowIndex the row index to unfreeze (0, 1, 2, or 3)
     */
    private void unfreezeMinions(final int rowIndex) {
        ArrayList<MinionCard> row = board.getTable().get(rowIndex);
        for (MinionCard card : row) {
            card.setFrozen(false);
        }
    }

    /**
     *  increases the mana for both players at the start of a new round.
     *  ensures mana does not exceed the maximum allowed (MAX_STAMINA).
     */
    private void increaseManaForBothPlayers() {
        int newMana = numRound;
        if (newMana > MAX_STAMINA) {
            newMana = MAX_STAMINA;
        }

        // Increase mana for both players
        board.getPlayer(1).increaseMana(newMana);
        board.getPlayer(2).increaseMana(newMana);
    }

    private void drawCards() {
        if (!board.getPlayer(1).getUsingDeck().getCards().isEmpty()) {
            // Draw cards for both players if their decks are not empty
            MinionCard playerOneCardDraw = board.getPlayer(1).getUsingDeck().getCards().get(0);
            MinionCard playerTwoCardDraw = board.getPlayer(2).getUsingDeck().getCards().get(0);
            board.getPlayer(1).getHand().add(playerOneCardDraw);
            board.getPlayer(2).getHand().add(playerTwoCardDraw);

            // Remove the drawn cards from the decks
            board.getPlayer(1).getUsingDeck().getCards().remove(0);
            board.getPlayer(2).getUsingDeck().getCards().remove(0);
        }
    }



}
