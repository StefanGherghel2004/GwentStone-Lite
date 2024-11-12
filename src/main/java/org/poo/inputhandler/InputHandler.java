package org.poo.inputhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.board.Board;
import org.poo.cards.Card;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.Coordinates;
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
    public void debugCommands(final ActionsInput actionsInput,
                              final Game game, final ArrayNode output) {
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

            ArrayNode outputArrayNode = mapper.createArrayNode();

            for (MinionCard card : game.getBoard().getPlayer(actionsInput.getPlayerIdx()).getUsingDeck().getCards()) {

                ObjectNode cardNode = cardToJson(card, mapper);

                outputArrayNode.add(cardNode);
            }

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

            mainNode.set("output", boardRows);
            output.add(mainNode);

        } else if (command.equals("getPlayerTurn")) {
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", actionsInput.getCommand());
            mainNode.put("output", game.getTurn());

            output.add(mainNode);

        } else if (command.equals("getPlayerHero")) {

            HeroCard hero = game.getBoard().getPlayerHero(actionsInput.getPlayerIdx());
            ObjectNode mainNode = mapper.createObjectNode();
            mainNode.put("command", "getPlayerHero");
            mainNode.put("playerIdx", actionsInput.getPlayerIdx());

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("mana", hero.getMana());
            outputNode.put("description", hero.getDescription());
            outputNode.put("name", hero.getName());
            outputNode.put("health", hero.getHealth());

            ArrayNode colorsArray = mapper.createArrayNode();
            for (String color : hero.getColors()) {
                colorsArray.add(color);
            }
            outputNode.set("colors", colorsArray);

            mainNode.set("output", outputNode);
            output.add(mainNode);

        } else if (command.equals("getCardAtPosition")) {
            int xTarget = actionsInput.getX();
            int yTarget = actionsInput.getY();
            if (!game.getBoard().areCoordinatesWithinBounds(xTarget, yTarget)) {
                ObjectNode errorNode = mapper.createObjectNode();
                errorNode.put("command", "getCardAtPosition");
                errorNode.put("x", xTarget);
                errorNode.put("y", yTarget);
                errorNode.put("output", "No card available at that position.");
                output.add(errorNode);
            } else {
                MinionCard card = game.getBoard().getCardWithCoordinates(xTarget, yTarget);
                ObjectNode mainNode = mapper.createObjectNode();
                mainNode.put("command", "getCardAtPosition");
                mainNode.put("x", xTarget);
                mainNode.put("y", yTarget);
                ObjectNode outputNode = cardToJson(card, mapper);
                mainNode.set("output", outputNode);
                output.add(mainNode);

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
            System.out.println("");
        } else {
            InputHandler inputHandler = new InputHandler();
            inputHandler.playCommands(actionsInput, game, output);
        }
    }

    /**
     *
     * @param actionsInput
     * @param game
     * @param output
     */
    public void playCommands(final ActionsInput actionsInput, final Game game, final ArrayNode output) {
        ObjectMapper mapper  = new ObjectMapper();
        String command = actionsInput.getCommand();
        if (command.equals("endPlayerTurn")) {
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

        } else if (command.equals("placeCard")) {

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


        } else if (command.equals("cardUsesAttack")) {

                Coordinates cardAttacked = actionsInput.getCardAttacked();
                int xAttacked = cardAttacked.getX();
                int yAttacked = cardAttacked.getY();
                Coordinates cardAttacker = actionsInput.getCardAttacker();
                int xAttacker = cardAttacker.getX();
                int yAttacker = cardAttacker.getY();

                if (!game.isEnemyCard(xAttacked, yAttacked)) {
                    System.out.println("AICI");
                    output.add(attackErrorToJson(cardAttacker, cardAttacked, mapper, "Attacked card does not belong to the enemy.", "cardUsesAttack"));
                    return;
                }

                MinionCard attacked = game.getBoard().getCardWithCoordinates(xAttacked, yAttacked);
                MinionCard attacking = game.getBoard().getCardWithCoordinates(xAttacker, yAttacker);

                if (attacking.getAttacked() == game.getNumRound()) {
                    output.add(attackErrorToJson(cardAttacker, cardAttacked, mapper, "Attacker card has already attacked this turn.", "cardUsesAttack"));
                    return;
                }

                if (attacking.isFrozen()) {
                    System.out.println("Attacker card is frozen");
                    return;
                }

                if (game.hasTankCard(turnToEnemy(game.getTurn())) & !attacked.isTank()) {
                    output.add(attackErrorToJson(cardAttacker, cardAttacked, mapper, "Attacked card is not of type 'Tank'.", "cardUsesAttack"));
                    return;
                }

                attacked.decreaseHealth(attacking.getAttackDamage());
                if (attacked.getHealth() <= 0) {
                    game.getBoard().removeCardWithCoordinates(xAttacked, yAttacked);
                }
                attacking.setAttacked(game.getNumRound());

        } else if (command.equals("cardUsesAbility")) {
            Coordinates cardAttacked = actionsInput.getCardAttacked();
            int xAttacked = cardAttacked.getX();
            int yAttacked = cardAttacked.getY();
            Coordinates cardAttacker = actionsInput.getCardAttacker();
            int xAttacker = cardAttacker.getX();
            int yAttacker = cardAttacker.getY();
            MinionCard attacked = game.getBoard().getCardWithCoordinates(xAttacked, yAttacked);
            MinionCard attacking = game.getBoard().getCardWithCoordinates(xAttacker, yAttacker);

            if (attacking.isFrozen()) {
                System.out.println("Attacker card is frozen");
                return;
            }

            if (attacking.getAttacked() == game.getNumRound()) {
                output.add(attackErrorToJson(cardAttacker, cardAttacked, mapper, "Attacker card has already attacked this turn.", "cardUsesAbility"));
                return;
            }

            if (attacking.getName().equals("Disciple") & game.isEnemyCard(xAttacked, yAttacked)) {
                output.add(attackErrorToJson(cardAttacker, cardAttacked, mapper, "Attacked card does not belong to the current player.", "cardUsesAbility"));
                return;
            }

            if (!attacking.getName().equals("Disciple") & !game.isEnemyCard(xAttacked, yAttacked)) {
                output.add(attackErrorToJson(cardAttacker, cardAttacked, mapper, "Attacked card does not belong to the enemy.", "cardUsesAbility"));
                return;
            }

            if (!attacking.getName().equals("Disciple") & game.hasTankCard(turnToEnemy(game.getTurn())) & !attacked.isTank()) {
                output.add(attackErrorToJson(cardAttacker, cardAttacked, mapper, "Attacked card is not of type 'Tank'.", "cardUsesAbility"));
                return;
            }

            attacking.useSpecialAbility(attacked);
            if (attacked.getHealth() <= 0) {
                game.getBoard().removeCardWithCoordinates(xAttacked, yAttacked);
            }
            attacking.setAttacked(game.getNumRound());


        } else if (command.equals("useAttackHero")) {
            Coordinates cardAttacker = actionsInput.getCardAttacker();
            int xAttacker = cardAttacker.getX();
            int yAttacker = cardAttacker.getY();
            MinionCard attacking = game.getBoard().getCardWithCoordinates(xAttacker, yAttacker);
            if (attacking.isFrozen()) {
                System.out.println("Attacker card is frozen");
                return;
            }
            if (attacking.getAttacked() == game.getNumRound()) {
                ObjectNode mainNode = mapper.createObjectNode();

                mainNode.put("command", command);

                ObjectNode attackerNode = mapper.createObjectNode();
                attackerNode.put("x", cardAttacker.getX());
                attackerNode.put("y", cardAttacker.getY());
                mainNode.set("cardAttacker", attackerNode);

                mainNode.put("error", "Attacker card has already attacked this turn.");
                output.add(mainNode);
                return;
            }
            if (game.hasTankCard(turnToEnemy(game.getTurn()))) {
                ObjectNode mainNode = mapper.createObjectNode();

                mainNode.put("command", command);

                ObjectNode attackerNode = mapper.createObjectNode();
                attackerNode.put("x", cardAttacker.getX());
                attackerNode.put("y", cardAttacker.getY());
                mainNode.set("cardAttacker", attackerNode);

                mainNode.put("error", "Attacked card is not of type 'Tank'.");
                output.add(mainNode);
                return;
            }
            HeroCard attackedHero = game.getBoard().getPlayerHero(turnToEnemy(game.getTurn()));
            attackedHero.decreaseHealth(attacking.getAttackDamage());
            if (attackedHero.getHealth() <= 0) {
                ObjectNode mainNode = mapper.createObjectNode();
                if (game.getTurn() == 1) {
                    mainNode.put("gameEnded", "Player one killed the enemy hero.");
                } else {
                    mainNode.put("gameEnded", "Player two killed the enemy hero.");
                }
                output.add(mainNode);
            }
            attacking.setAttacked(game.getNumRound());
        } else if (command.equals("useHeroAbility")) {
            System.out.println();
        } else {
            System.out.println(actionsInput.getCommand());
        }
    }

    /**
     *
     * @param card
     * @param mapper
     * @return ObjectNode representing the card in JSON format
     */
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

    /**
     *
     * @param turn
     * @return
     */
    public int turnToEnemy(final int turn) {
        if (turn == 1) {
            return 2;
        }
        return 1;
    }

    /**
     *
     * @param attackerCard
     * @param attackedCard
     * @param mapper
     * @param error
     * @param command
     * @return
     */
    public ObjectNode attackErrorToJson(final Coordinates attackerCard, final Coordinates attackedCard, final ObjectMapper mapper, final String error, final String command) {
        ObjectNode mainNode = mapper.createObjectNode();

        mainNode.put("command", command);

        ObjectNode attackerNode = mapper.createObjectNode();
        attackerNode.put("x", attackerCard.getX());
        attackerNode.put("y", attackerCard.getY());
        mainNode.set("cardAttacker", attackerNode);

        ObjectNode attackedNode = mapper.createObjectNode();
        attackedNode.put("x", attackedCard.getX());
        attackedNode.put("y", attackedCard.getY());
        mainNode.set("cardAttacked", attackedNode);

        mainNode.put("error", error);
        return mainNode;
    }

}
