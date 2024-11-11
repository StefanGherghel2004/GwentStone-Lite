package org.poo.inputhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.board.Board;
import org.poo.cards.Card;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.GameInput;
import org.poo.fileio.Input;
import org.poo.game.Game;
import org.poo.player.Player;

import java.util.ArrayList;

public final class InputHandler {

    public static final int MAX_STAMINA = 10;
    public static final int ROW_LENGTH = 5;

    /**
     *
     * @param inputData
     * @param game
     * @return
     */
    public Game setInitialSetup(final Input inputData, final GameInput game) {


            // Initialize players with their decks and shuffle seeds
            Player playerOne = new Player(inputData.getPlayerOneDecks().getDecks(),
                    game.getStartGame().getPlayerOneDeckIdx(),
                    game.getStartGame().getShuffleSeed());

            Player playerTwo = new Player(inputData.getPlayerTwoDecks().getDecks(),
                    game.getStartGame().getPlayerTwoDeckIdx(),
                    game.getStartGame().getShuffleSeed());

            // Initialize hero cards for both players
            HeroCard playerOneHero = new HeroCard(game.getStartGame().getPlayerOneHero());
            HeroCard playerTwoHero = new HeroCard(game.getStartGame().getPlayerTwoHero());

            // Assign hero cards to players
            playerOne.setHeroCard(playerOneHero);
            playerTwo.setHeroCard(playerTwoHero);

            // Initialize the board with players
            Board board = new Board(playerOne, playerTwo);

            // Initialize game with the board and starting player
            Game myGame = new Game(board, game.getStartGame().getStartingPlayer());


            return myGame;
    }

    /**
     *
     * @param actionsInput
     * @param game
     * @param output
     */
    public void debugCommands(final ActionsInput actionsInput, final Game game, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        String command = actionsInput.getCommand();
        if (command.equals("getCardsInHand")) {

            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getCardsInHand");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());

            ArrayNode outputArrayNode = mapper.createArrayNode();

            for (MinionCard card : game.getBoard().getPlayer(actionsInput.getPlayerIdx()).getHand()) {

                ObjectNode cardNode = cardToJson(card, mapper);
                outputArrayNode.add(cardNode);
            }

            mainNode.set("output", outputArrayNode);

            output.add(mainNode);


        } else if (command.equals("getPlayerDeck")) {

            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getPlayerDeck");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());

            // Create the output array node
            ArrayNode outputArrayNode = mapper.createArrayNode();

            // Iterate over each card in the player's deck
            for (MinionCard card : game.getBoard().getPlayer(actionsInput.getPlayerIdx()).getUsingDeck().getCards()) {
                // Create an object node for each card
                ObjectNode cardNode = cardToJson(card, mapper);

                // Add the card node to the output array node
                outputArrayNode.add(cardNode);
            }

            // Attach the output array node to the main node
            mainNode.set("output", outputArrayNode);

            output.add(mainNode);


        } else if (command.equals("getCardsOnTable")) {
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getCardsOnTable");

            ArrayNode boardRows = mapper.createArrayNode();

            for (int rowIndex = 0; rowIndex < game.getBoard().getTable().size(); rowIndex++) {
                ArrayList<MinionCard> boardRow = game.getBoard().getRow(rowIndex);
                ArrayNode rowArray = mapper.createArrayNode();

                for (MinionCard card : boardRow) {
                    if (card != null) {
                        ObjectNode cardNode = cardToJson(card, mapper);
                        rowArray.add(cardNode);
                    } else {
                        // Add an empty object or skip if there's no card in this slot
                        System.out.println();
                    }
                }

                // Add the row array to the boardRows array
                boardRows.add(rowArray);
            }

// Attach the boardRows array to the mainNode as "output"
            mainNode.set("output", boardRows);
            output.add(mainNode);

        } else if (command.equals("getPlayerTurn")) {
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", actionsInput.getCommand());
            mainNode.put("output", game.getTurn());

            output.add(mainNode);

        } else if (command.equals("getPlayerHero")) {

            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getPlayerHero");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("mana", game.getBoard().getPlayerHero(actionsInput.getPlayerIdx()).getMana());
            outputNode.put("description", game.getBoard().getPlayerHero(actionsInput.getPlayerIdx()).getDescription());
            outputNode.put("name", game.getBoard().getPlayerHero(actionsInput.getPlayerIdx()).getName());
            outputNode.put("health", game.getBoard().getPlayerHero(actionsInput.getPlayerIdx()).getHealth());

            ArrayNode colorsArray = mapper.createArrayNode();
            for (String color : game.getBoard().getPlayerHero(actionsInput.getPlayerIdx()).getColors()) {
                colorsArray.add(color);
            }
            outputNode.set("colors", colorsArray);

            mainNode.set("output", outputNode);
            output.add(mainNode);

        } else if (command.equals("getCardAtPosition")) {
            if (game.getBoard().getTable().get(actionsInput.getY()).size() <= actionsInput.getX()) {
                System.out.println("Nu exista carte pe pozitia " + actionsInput.getX() + actionsInput.getY());
            }
        } else if (command.equals("getPlayerMana")) {
            ObjectNode mainNode = mapper.createObjectNode();

            mainNode.put("command", "getPlayerMana");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());
            mainNode.put("output", game.getBoard().getPlayer(actionsInput.getPlayerIdx()).getMana());

            output.add(mainNode);

        } else if (command.equals("getFrozenCardsOnTable")) {
            System.out.println("");
        } else {
            InputHandler inputHandler = new InputHandler();
            inputHandler.statsCommands(actionsInput, game, output);
        }
    }

    /**
     *
     * @param actionsInput
     * @param game
     * @param output
     */
    public void statsCommands(final ActionsInput actionsInput, final Game game, final ArrayNode output) {
        if (actionsInput.getCommand().equals("getTotalGamesPlayed")) {
            System.out.println("");
        } else if (actionsInput.getCommand().equals("getPlayerOneWins")) {
            System.out.println("");
        } else if (actionsInput.getCommand().equals("getPlayerTwoWins")) {

        } else {
            InputHandler inputHandler = new InputHandler();
            inputHandler.playCommands(actionsInput, game, output);
        }
    }

    public void playCommands(final ActionsInput actionsInput, final Game game, final ArrayNode output) {
        ObjectMapper mapper  = new ObjectMapper();
        if (actionsInput.getCommand().equals("endPlayerTurn")) {
                if (game.getTurn() == 1) {
                    game.setTurn(2);
                    game.setPlayerTwoRoundEnded(true);
                } else if (game.getTurn() == 2) {
                    game.setTurn(1);
                    game.setPlayerOneRoundEnded(true);
                }
                // new round
                if (game.getPlayerOneRoundEnded() == game.getPlayerTwoRoundEnded()) {
                    game.setPlayerTwoRoundEnded(false);
                    game.setPlayerOneRoundEnded(false);
                    game.setNumRound(game.getNumRound() + 1);
                    // needing condition to not add more than 10 mana
                    if (game.getNumRound() <= MAX_STAMINA) {
                        game.getBoard().getPlayer(1).increaseMana(game.getNumRound());
                        game.getBoard().getPlayer(2).increaseMana(game.getNumRound());
                    } else {
                        game.getBoard().getPlayer(1).increaseMana(MAX_STAMINA);
                        game.getBoard().getPlayer(2).increaseMana(MAX_STAMINA);
                    }
                    // needing to draw card if the deck is not empty
                    if (!game.getBoard().getPlayer(1).getUsingDeck().getCards().isEmpty()) {
                        MinionCard playerOneCardDraw = game.getBoard().getPlayer(1).getUsingDeck().getCards().get(0);
                        MinionCard playerTwoCardDraw = game.getBoard().getPlayer(2).getUsingDeck().getCards().get(0);
                        game.getBoard().getPlayer(1).getHand().add(playerOneCardDraw);
                        game.getBoard().getPlayer(2).getHand().add(playerTwoCardDraw);
                        game.getBoard().getPlayer(1).getUsingDeck().getCards().remove(0);
                        game.getBoard().getPlayer(2).getUsingDeck().getCards().remove(0);
                    }
                }

        } else if (actionsInput.getCommand().equals("placeCard")) {

            Player currentPlayer = game.getBoard().getPlayer(game.getTurn());

            if (actionsInput.getHandIdx() < currentPlayer.getHand().size()) {

                MinionCard cardToPlace = currentPlayer.getHand().get(actionsInput.getHandIdx());

                // Check if the player has enough mana to place the card
                if (currentPlayer.getMana() < cardToPlace.getMana()) {
                    // Handle case where the player has insufficient mana
                    ObjectNode errorNode = mapper.createObjectNode();
                    errorNode.put("command", "placeCard");
                    errorNode.put("handIdx", actionsInput.getHandIdx());
                    errorNode.put("error", "Not enough mana to place card on table.");

                    output.add(errorNode);
                } else {
                    int row = cardToPlace.rowToPlace(cardToPlace, game.getTurn());

                    if (game.getBoard().getRow(row).size() < ROW_LENGTH) {
                        currentPlayer.decreaseMana(cardToPlace.getMana());

                        game.getBoard().getRow(row).add(cardToPlace);

                        currentPlayer.getHand().remove(actionsInput.getHandIdx());
                    } else {
                        // Handle case where the row is full
                        ObjectNode errorNode = mapper.createObjectNode();
                        errorNode.put("command", "placeCard");
                        errorNode.put("handIdx", actionsInput.getHandIdx());
                        errorNode.put("error", "Cannot place card on table since row is full.");

                        output.add(errorNode);
                    }
                }
            } else {
                ObjectNode errorNode = mapper.createObjectNode();
                errorNode.put("command", "placeCard");
                errorNode.put("handIdx", actionsInput.getHandIdx());
                errorNode.put("error", "Invalid hand index");

                output.add(errorNode);
            }


        } else if (actionsInput.getCommand().equals("cardUsesAttack")) {
            System.out.println();
        } else if (actionsInput.getCommand().equals("cardUsesAbility")) {
            System.out.println();
        } else if (actionsInput.getCommand().equals("useAttackHero")) {
            System.out.println();
        } else if (actionsInput.getCommand().equals("useAbility")) {
            System.out.println();
        } else if (actionsInput.getCommand().equals("useHeroAbility")) {
            System.out.println();
        } else {
            System.out.println(actionsInput.getCommand());
        }
    }

    public static ObjectNode cardToJson(final Card card, final ObjectMapper mapper) {
        ObjectNode outputNode = mapper.createObjectNode();

        outputNode.put("mana", card.getMana());
        outputNode.put("attackDamage", card.getAttackDamage());
        outputNode.put("description", card.getDescription());
        outputNode.put("name", card.getName());
        outputNode.put("health", card.getHealth());

        ArrayNode colorsArray = mapper.createArrayNode();
        for (String color : card.getColors()) {
            colorsArray.add(color);
        }
        outputNode.set("colors", colorsArray);

        return outputNode;
    }

}
