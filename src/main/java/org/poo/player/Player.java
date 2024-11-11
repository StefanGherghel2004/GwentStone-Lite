package org.poo.player;

import lombok.Getter;
import lombok.Setter;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

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
    public Player(final ArrayList<ArrayList<CardInput>> decks, final int usingDeckIndex, final int seed) {
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
     *
     * @param otherMana
     */
    public void decreaseMana(final int otherMana) {
        this.mana -= otherMana;
    }

    /**
     *
     * @param otherMana
     */
    public void increaseMana(final int otherMana) {
        this.mana += otherMana;
    }

}
