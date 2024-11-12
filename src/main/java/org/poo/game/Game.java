package org.poo.game;

import lombok.Getter;
import lombok.Setter;
import org.poo.board.Board;
import org.poo.cards.MinionCard;

import java.util.ArrayList;

@Getter
@Setter
public final class Game {
    private Board board;
    private int turn;
    private boolean playerOneRoundEnded;
    private boolean playerTwoRoundEnded;
    private int numRound;
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
     *
     * @param x
     * @param y
     * @return
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
     *
     * @param playerIdx
     * @return
     */
    public ArrayList<MinionCard> getFrontRow(final int playerIdx) {
        if (playerIdx == 1) {
            return this.getBoard().getTable().get(2);
        }
        return this.getBoard().getTable().get(1);
    }

    /**
     *
     * @param playerIdx
     * @return
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



}
