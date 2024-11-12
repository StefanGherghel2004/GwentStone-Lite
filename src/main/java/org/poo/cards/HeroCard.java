package org.poo.cards;

import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class HeroCard extends Card {
    private int mana;
    private int HEALTH = 30;
    private int attacked;

    public HeroCard() {
        super();
    }
    public HeroCard(final String name, final  String description,
                    final ArrayList<String> colors, final int mana) {
        super(description, colors, name);
        this.mana = mana;
    }

    public HeroCard(final CardInput cardinput) {
        super(cardinput);
        this.mana = cardinput.getMana();
    }

    public HeroCard(final HeroCard otherHero) {
        super(otherHero.getDescription(), otherHero.getColors(), otherHero.getName());
        this.mana = otherHero.getMana();
    }

    public int getMana() {
        return mana;
    }

    public int getHealth() {
        return HEALTH;
    }

    public void decreaseHealth(final int dec) {
        this.HEALTH -= dec;
    }

    public void useAbility(HeroCard myHero,final int row) {

    }
}
