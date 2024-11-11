package org.poo.game;

import lombok.Getter;
import lombok.Setter;
import org.poo.board.Board;

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

}
