package org.poo.cards;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

public class HeroCard extends Card {
    private int mana;
    private int health = 30;

    public HeroCard() {
        super();
    }
    public HeroCard(String name, String description, ArrayList<String> colors, int mana) {
        super(description,colors, name);
        this.mana = mana;
    }

    public HeroCard(CardInput cardinput) {
        super(cardinput);
        this.mana = cardinput.getMana();
    }

    public HeroCard(HeroCard otherHero) {
        super(otherHero.getDescription(), otherHero.getColors(), otherHero.getName());
        this.mana = otherHero.getMana();
    }
    // concept function because of needing it
    public ArrayNode HeroCardToArrayNode() {
        return null;
    }

    public int getMana() {
        return mana;
    }

    public int getHealth() {
        return health;
    }
}
