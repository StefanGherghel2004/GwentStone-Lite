package org.poo.player;

import org.poo.cards.MinionCard;
import org.poo.fileio.CardInput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Deck {
    private ArrayList<MinionCard> cards;
    public Deck(final ArrayList<CardInput> cards) {
        this.cards = new ArrayList<>();
        for (CardInput card : cards) {
            this.cards.add(new MinionCard(card));
        }
    }

    /**
     * Method to shuffle the cards in given deck at the start of new game
     * @param seed seed used for shuffling the cards using Random class
     */
    public void shuffle(final int seed) {
        Random random = new Random(seed);
        Collections.shuffle(cards, random);
    }

    public Deck(final Deck otherDeck) {
        this.cards = new ArrayList<>(otherDeck.getCards());
    }


    public ArrayList<MinionCard> getCards() {
        return cards;
    }



}
