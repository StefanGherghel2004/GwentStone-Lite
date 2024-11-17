package org.poo.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.board.Board;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.CardInput;
import org.poo.game.Game;
import java.util.ArrayList;

import static org.poo.inputhandler.InputHandler.ROW_LENGTH;


@Setter
@Getter
public final class Player {
    private HeroCard heroCard;
    private ArrayList<MinionCard> hand;
    private Deck usingDeck;
    private ArrayList<Deck> decks;
    private int mana;

    public Player() {

    }
    public Player(final ArrayList<ArrayList<CardInput>> decks,
                  final int usingDeckIndex, final int seed) {
        this.hand = new ArrayList<MinionCard>();
        this.decks = new ArrayList<Deck>();
        for (ArrayList<CardInput> deck : decks) {
            this.decks.add(new Deck(deck));
        }
        this.usingDeck = new Deck(decks.get(usingDeckIndex));
        this.usingDeck.shuffle(seed);
    }

    public Player(final Player otherPlayer) {
        this.heroCard = new HeroCard(otherPlayer.getHeroCard());

        // Deep copy the hand
        this.hand = new ArrayList<>();
        for (MinionCard minion : otherPlayer.hand) {
            this.hand.add(new MinionCard(minion));
        }


        this.usingDeck = new Deck(otherPlayer.getUsingDeck());

        this.decks = new ArrayList<>();
        for (Deck deck : otherPlayer.decks) {
            this.decks.add(new Deck(deck));
        }
    }

    /**
     * Converts the hand or deck of the player in a JSON object
     * @param mapper ObjectMapper used ofr JSON generation
     * @param playerIdx given index of the player (1 or 2)
     * @param command given command ("getPlayerDeck" or "getCardsInHand")
     * @return JSON representation of the deck or hand
     */
    public ObjectNode getPlayerCardsJson(final ObjectMapper mapper,
                                         final int playerIdx, final String command) {

        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", command);
        mainNode.put("playerIdx", playerIdx);

        ArrayNode outputArrayNode = mapper.createArrayNode();

        ArrayList<MinionCard> cards = new ArrayList<MinionCard>();

        if (command.equals("getPlayerDeck")) {
            cards = this.usingDeck.getCards();
        } else if (command.equals("getCardsInHand")) {
            cards = this.hand;
        }

        for (MinionCard card : cards) {
            ObjectNode cardNode = card.toJson(mapper);
            outputArrayNode.add(cardNode);
        }

        mainNode.set("output", outputArrayNode);

        return mainNode;
    }

    /**
     * Converts player's hero to a JSON representation
     * @param mapper ObjectMapper used for generating JSON
     * @param index index representing the player
     * @return JSON representation of player's hero
     */
    public ObjectNode getHeroToJson(final ObjectMapper mapper, final int index) {
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.put("command", "getPlayerHero");
        mainNode.put("playerIdx", index);

        ObjectNode outputNode = heroCard.toJson(mapper);
        mainNode.set("output", outputNode);
        return mainNode;
    }

    /**
     * Converts player's mana to a JSON representation
     * @param mapper ObjectMapper used for generating JSON
     * @param index index representing the player
     * @return JSON representation of player's mana
     */
    public ObjectNode getManaToJson(final ObjectMapper mapper, final int index) {
        ObjectNode mainNode = mapper.createObjectNode();

        mainNode.put("command", "getPlayerMana");
        mainNode.put("playerIdx", index);
        mainNode.put("output", mana);
        return mainNode;
    }
    /**
     * decreases player's mana by given amount
     * @param otherMana given amount to decrease
     */
    public void decreaseMana(final int otherMana) {
        this.mana -= otherMana;
    }

    /**
     * increases player's mana by given amount
     * @param otherMana given amount to increase
     */
    public void increaseMana(final int otherMana) {
        this.mana += otherMana;
    }

    /**
     * Method that places a card on the table
     * @param handIdx the index in player's hand
     * @param game current game context
     * @param mapper ObjectMapper for JSON in case of an error
     * @return null if the operation is successful
     */
    public ObjectNode getPlaceCardJson(final int handIdx, final Game game,
                                       final  ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("command", "placeCard");
        node.put("handIdx", handIdx);

        // checking if the index is valid for player's hand
        if (handIdx < 0 || handIdx >= hand.size()) {
            node.put("error", "Invalid hand index");
            return node;
        }

        MinionCard cardToPlace = hand.get(handIdx);

        // checking if the player has enough mana to place the card
        if (this.getMana() < cardToPlace.getMana()) {
            node.put("error", "Not enough mana to place card on table.");
            return node;
        }

        int row = cardToPlace.rowToPlace(cardToPlace, game.getTurn());

        Board board = game.getBoard();
        // checking if the row on the table is full
        if (board.getRow(row).size() >= ROW_LENGTH) {
            node.put("error", "Cannot place card on table since row is full.");
            return node;
        }
        // decreasing the mana of the player is placing is successful
        decreaseMana(cardToPlace.getMana());
        board.getRow(row).add(cardToPlace);
        //removing the card from player's hand
        hand.remove(handIdx);

        return null;
    }
}
