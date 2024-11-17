package org.poo.inputhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.board.Board;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.Coordinates;
import org.poo.fileio.GameInput;
import org.poo.fileio.Input;
import org.poo.game.Game;
import org.poo.player.Player;


public final class InputHandler {

    // maximum stamina a player can receive at the beginning of a new round
    public static final int MAX_STAMINA = 10;
    // max length of a row on the table
    public static final int ROW_LENGTH = 5;

    /**
     * sets up the initial configuration of the game based on input data.
     * this method initializes players with their decks, heroes, and shuffled decks.
     * it also sets up the game board and starts the first round.
     *
     * @param inputData data containing the player's decks and other game settings.
     * @param game  game input object containing information about the start of the game.
     * @param gamesPlayed  number of games played so far.
     * @param playerOneWins  number of games player one has won.
     * @param playerTwoWins  number of games player two has won.
     * @return initialized game object.
     */
    public Game setInitialSetup(final Input inputData, final GameInput game,
                                final int gamesPlayed, final int playerOneWins,
                                final int playerTwoWins) {


            // initialize players with their decks and shuffle seeds
            Player playerOne = new Player(inputData.getPlayerOneDecks().getDecks(),
                    game.getStartGame().getPlayerOneDeckIdx(),
                    game.getStartGame().getShuffleSeed());

            Player playerTwo = new Player(inputData.getPlayerTwoDecks().getDecks(),
                    game.getStartGame().getPlayerTwoDeckIdx(),
                    game.getStartGame().getShuffleSeed());

            // initialize hero cards for both players
            HeroCard playerOneHero = new HeroCard(game.getStartGame().getPlayerOneHero());
            HeroCard playerTwoHero = new HeroCard(game.getStartGame().getPlayerTwoHero());

            // assign hero cards to players
            playerOne.setHeroCard(playerOneHero);
            playerTwo.setHeroCard(playerTwoHero);

            // initialize the board with players
            Board board = new Board(playerOne, playerTwo);

            // initialize game with the board and starting player
            Game myGame = new Game(board, game.getStartGame().getStartingPlayer());

            // initialise first round
            myGame.setFirstRound();

            // previous games stats
            myGame.setStats(gamesPlayed, playerOneWins, playerTwoWins);

            return myGame;
    }

    /**
     * handles debug commands and generates appropriate responses for the output.
     *
     * @param actionsInput  input containing the command and related data.
     * @param game current game context.
     * @param output output arrayNode that will hold the results of the command.
     */
    public void debugCommands(final ActionsInput actionsInput,
                              final Game game, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        String command = actionsInput.getCommand();
        int index = actionsInput.getPlayerIdx();
        Player player = game.getBoard().getPlayer(index);
        int x = actionsInput.getX();
        int y = actionsInput.getY();

        if (command.equals("getCardsInHand") || command.equals("getPlayerDeck")) {
            ObjectNode hand = player.getPlayerCardsJson(mapper, index, command);
            output.add(hand);

        } else if (command.equals("getCardsOnTable")) {
            ObjectNode table = game.getBoard().toJson(mapper);
            output.add(table);

        } else if (command.equals("getPlayerTurn")) {
            ObjectNode turn = game.getTurnToJson(mapper);
            output.add(turn);

        } else if (command.equals("getPlayerHero")) {
            ObjectNode hero = player.getHeroToJson(mapper, index);
            output.add(hero);

        } else if (command.equals("getCardAtPosition")) {
            ObjectNode node = game.getBoard().getCardAtPositionJson(x, y, mapper);
            output.add(node);
        } else if (command.equals("getPlayerMana")) {
            ObjectNode mainNode = player.getManaToJson(mapper, index);
            output.add(mainNode);

        } else if (command.equals("getFrozenCardsOnTable")) {
            ObjectNode mainNode = game.getBoard().toJsonFrozen(mapper);
            output.add(mainNode);

        } else {
            InputHandler inputHandler = new InputHandler();
            inputHandler.statsCommands(actionsInput, game, output);
        }
    }

    /**
     * handles statistics commands
     *
     * @param actionsInput input containing the command and related data.
     * @param game current game context.
     * @param output output arrayNode that will hold the results of the command.
     */
    public void statsCommands(final ActionsInput actionsInput, final Game game,
                              final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();

        switch (actionsInput.getCommand()) {
            case "getTotalGamesPlayed" -> {
                resultNode.put("command", "getTotalGamesPlayed");
                resultNode.put("output", game.getGamesPlayed());
                output.add(resultNode);
            }
            case "getPlayerOneWins" -> {
                resultNode.put("command", "getPlayerOneWins");
                resultNode.put("output", game.getPlayerOneWins());
                output.add(resultNode);
            }
            case "getPlayerTwoWins" -> {
                resultNode.put("command", "getPlayerTwoWins");
                resultNode.put("output", game.getPlayerTwoWins());
                output.add(resultNode);
            }
            default -> playCommands(actionsInput, game, output);
        }
    }

    /**
     * handles commands related to gameplay actions.
     * this method processes commands and modifies the game state accordingly.
     *
     * @param actionsInput input containing the command and related data.
     * @param game current game context.
     * @param output output arrayNode that will hold the results of the command.
     */
    public void playCommands(final ActionsInput actionsInput, final Game game,
                             final ArrayNode output) {
        ObjectMapper mapper  = new ObjectMapper();
        String command = actionsInput.getCommand();
        Coordinates cardAttacked = actionsInput.getCardAttacked();
        Coordinates cardAttacker = actionsInput.getCardAttacker();
        int xAttacked = 0, yAttacked = 0, xAttacker = 0, yAttacker = 0;

        // extract coordinates if available
        if (cardAttacked != null) {
            xAttacked = cardAttacked.getX();
            yAttacked = cardAttacked.getY();
        }
        if (cardAttacker != null) {
            xAttacker = cardAttacker.getX();
            yAttacker = cardAttacker.getY();
        }

        if (command.equals("endPlayerTurn")) {
                game.endTurn();
        } else if (command.equals("placeCard")) {

            Player currentPlayer = game.getBoard().getPlayer(game.getTurn());
            int handIdx = actionsInput.getHandIdx();
            ObjectNode node = currentPlayer.getPlaceCardJson(handIdx, game, mapper);
            if (node != null) {
                output.add(node);
            }

        } else if (command.equals("cardUsesAttack") || command.equals("cardUsesAbility")) {

            MinionCard attacking = game.getBoard().getCardWithCoordinates(xAttacker, yAttacker);
            MinionCard attacked = game.getBoard().getCardWithCoordinates(xAttacked, yAttacked);
            ObjectNode result;

            if (command.equals("cardUsesAttack")) {
                result = attacking.useAttack(attacked, game, mapper, cardAttacker, cardAttacked);
            } else {
                result = attacking.useAbility(attacked, game, mapper, cardAttacker, cardAttacked);
            }

            if (result != null) {
                output.add(result);
            }

        }  else if (command.equals("useAttackHero")) {
            MinionCard attacking = game.getBoard().getCardWithCoordinates(xAttacker, yAttacker);

            ObjectNode node = attacking.useAttackHero(game, cardAttacker, mapper);
            if (node != null) {
                output.add(node);
            }
        } else if (command.equals("useHeroAbility")) {
            int affectedRow = actionsInput.getAffectedRow();
            HeroCard hero = game.getBoard().getPlayerHero(game.getTurn());

            ObjectNode node = hero.useAbility(game, affectedRow, mapper);
            if (node != null) {
                output.add(node);
            }
        } else {
            System.out.println(actionsInput.getCommand());
        }
    }
}
