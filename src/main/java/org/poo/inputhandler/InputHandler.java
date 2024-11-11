package org.poo.inputhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.board.Board;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.GameInput;
import org.poo.fileio.Input;
import org.poo.game.Game;
import org.poo.player.Player;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class InputHandler {

    public Game setInitialSetup(Input inputData, GameInput game) {


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

    // skeleton
    public void debugCommands(ActionsInput actionsInput, Game game, ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        if (actionsInput.getCommand().equals("getCardsInHand")) {

            // Create the main object
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getCardsInHand");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());

            // Create the output array node
            ArrayNode outputArrayNode = mapper.createArrayNode();

            // Iterate over each card in the player's deck
            for (MinionCard card : game.getBoard().getPlayer(actionsInput.getPlayerIdx()).getHand()) {
                // Create an object node for each card
                ObjectNode cardNode = mapper.createObjectNode();
                cardNode.put("mana", card.getMana());
                cardNode.put("attackDamage", card.getAttackDamage());
                cardNode.put("health", card.getHealth());
                cardNode.put("description", card.getDescription());
                cardNode.put("name", card.getName());

                // Create and populate the colors array for each card
                ArrayNode colorsArray = mapper.createArrayNode();
                for (String color : card.getColors()) {
                    colorsArray.add(color);
                }
                cardNode.set("colors", colorsArray);

                // Add the card node to the output array node
                outputArrayNode.add(cardNode);
            }

            // Attach the output array node to the main node
            mainNode.set("output", outputArrayNode);

            output.add(mainNode);


        } else if (actionsInput.getCommand().equals("getPlayerDeck")) {

            // Create the main object
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getPlayerDeck");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());

            // Create the output array node
            ArrayNode outputArrayNode = mapper.createArrayNode();

            // Iterate over each card in the player's deck
            for (MinionCard card : game.getBoard().getPlayer(actionsInput.getPlayerIdx()).getUsingDeck().getCards()) {
                // Create an object node for each card
                ObjectNode cardNode = mapper.createObjectNode();
                cardNode.put("mana", card.getMana());
                cardNode.put("attackDamage", card.getAttackDamage());
                cardNode.put("health", card.getHealth());
                cardNode.put("description", card.getDescription());
                cardNode.put("name", card.getName());

                // Create and populate the colors array for each card
                ArrayNode colorsArray = mapper.createArrayNode();
                for (String color : card.getColors()) {
                    colorsArray.add(color);
                }
                cardNode.set("colors", colorsArray);

                // Add the card node to the output array node
                outputArrayNode.add(cardNode);
            }

            // Attach the output array node to the main node
            mainNode.set("output", outputArrayNode);

            output.add(mainNode);


        } else if (actionsInput.getCommand().equals("getCardsOnTable")) {
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getCardsOnTable");

            ArrayNode boardRows = mapper.createArrayNode();

            for (int rowIndex = 0; rowIndex < game.getBoard().getTable().size(); rowIndex++) {
                ArrayList<MinionCard> boardRow = game.getBoard().getRow(rowIndex);
                ArrayNode rowArray = mapper.createArrayNode();

                for (MinionCard card : boardRow) {
                    if (card != null) {
                        // Create an ObjectNode for each card and populate it with the card's properties
                        ObjectNode cardNode = mapper.createObjectNode();
                        cardNode.put("mana", card.getMana());
                        cardNode.put("attackDamage", card.getAttackDamage());
                        cardNode.put("health", card.getHealth());
                        cardNode.put("description", card.getDescription());

                        // Add colors as an array
                        ArrayNode colorsArray = mapper.createArrayNode();
                        for (String color : card.getColors()) {
                            colorsArray.add(color);
                        }
                        cardNode.set("colors", colorsArray);

                        cardNode.put("name", card.getName());

                        // Add the card's JSON representation to the row array
                        rowArray.add(cardNode);
                    } else {
                        // Add an empty object or skip if there's no card in this slot

                    }
                }

                // Add the row array to the boardRows array
                boardRows.add(rowArray);
            }

// Attach the boardRows array to the mainNode as "output"
            mainNode.set("output", boardRows);
            output.add(mainNode);

        } else if (actionsInput.getCommand().equals("getPlayerTurn")) {
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", actionsInput.getCommand());
            mainNode.put("output", game.getTurn());

            output.add(mainNode);

        } else if (actionsInput.getCommand().equals("getPlayerHero")) {

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

        } else if (actionsInput.getCommand().equals("getCardAtPosition")) {

        } else if (actionsInput.getCommand().equals("getPlayerMana")) {
            ObjectNode mainNode = mapper.createObjectNode();

            mainNode.put("command", "getPlayerMana");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());
            mainNode.put("output", game.getBoard().getPlayer(actionsInput.getPlayerIdx()).getMana());

            output.add(mainNode);

        } else if (actionsInput.getCommand().equals("getFrozenCardsOnTable")) {

        } else {
            InputHandler inputHandler = new InputHandler();
            inputHandler.statsCommands(actionsInput, game, output);
        }
    }

    public void statsCommands(ActionsInput actionsInput, Game game, ArrayNode output) {
        if (actionsInput.getCommand().equals("getTotalGamesPlayed")) {

        } else if (actionsInput.getCommand().equals("getPlayerOneWins")) {

        } else if(actionsInput.getCommand().equals("getPlayerTwoWins")) {

        } else {
            InputHandler inputHandler = new InputHandler();
            inputHandler.playCommands(actionsInput, game, output);
        }
    }

    public void playCommands(ActionsInput actionsInput, Game game, ArrayNode output) {
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
                    if (game.getNumRound() <= 10) {
                        game.getBoard().getPlayer(1).increaseMana(game.getNumRound());
                        game.getBoard().getPlayer(2).increaseMana(game.getNumRound());
                    } else {
                        game.getBoard().getPlayer(1).increaseMana(10);
                        game.getBoard().getPlayer(2).increaseMana(10);
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
                // error handling
                if (actionsInput.getHandIdx() < game.getBoard().getPlayer(game.getTurn()).getHand().size()) {
                    game.getBoard().getPlayer(game.getTurn()).decreaseMana(game.getBoard().getPlayer(game.getTurn()).getHand().get(actionsInput.getHandIdx()).getMana());
                    game.getBoard().getPlayer(game.getTurn()).getHand().remove(actionsInput.getHandIdx());
                }


        } else if (actionsInput.getCommand().equals("cardUsesAttack")) {

        } else if (actionsInput.getCommand().equals("cardUsesAbility")) {

        } else if (actionsInput.getCommand().equals("useAttackHero")) {

        } else if (actionsInput.getCommand().equals("useAbility")) {

        } else if (actionsInput.getCommand().equals("useHeroAbility")) {

        } else {
            System.out.println(actionsInput.getCommand());
        }
    }

}
