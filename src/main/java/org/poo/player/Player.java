package org.poo.player;

import lombok.Getter;
import lombok.Setter;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

@Setter
@Getter
public class Player {
    private HeroCard heroCard;
    private ArrayList<MinionCard> hand;
    private Deck usingDeck;
    private ArrayList<Deck> decks;
    private int mana;

    public Player() {

    }
    public Player(ArrayList<ArrayList<CardInput>> decks, int usingDeckIndex, int seed) {
        this.hand = new ArrayList<MinionCard>();
        this.decks = new ArrayList<Deck>();
        for (ArrayList<CardInput> deck : decks) {
            this.decks.add(new Deck(deck));
        }
        this.usingDeck = new Deck(decks.get(usingDeckIndex));
        this.usingDeck.shuffle(seed);
    }

    public Player(Player otherPlayer) {
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

    public void decreaseMana(int mana) {
        this.mana -= mana;
    }

    public void increaseMana(int mana) {
        this.mana += mana;
    }

}
