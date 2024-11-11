package org.poo.player;

import org.poo.cards.Card;
import org.poo.cards.MinionCard;
import org.poo.fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private ArrayList<MinionCard> cards;
    public Deck(ArrayList<CardInput> cards) {
        this.cards = new ArrayList<>();
        for (CardInput card : cards) {
            this.cards.add(new MinionCard(card));
        }
    }

    public void shuffle(int seed) {
        Random random = new Random(seed);
        Collections.shuffle(cards, random);
    }

    public Deck(Deck otherDeck) {
        this.cards = new ArrayList<>(otherDeck.getCards());
    }

    public ArrayList<MinionCard> getCards() {
        return cards;
    }



}
