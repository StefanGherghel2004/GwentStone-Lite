package org.poo.cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

@Getter
public final class HeroCard extends Card {
    private int mana;
    private int health = 30;
    @Setter
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

    public void decreaseHealth(final int dec) {
        this.health -= dec;
    }

    public void useAbility(final HeroCard myHero, final ArrayList<MinionCard> row) {
        String heroName = myHero.getName();
        switch (heroName) {
            case "Lord Royce":
                for (MinionCard card : row) {
                    card.setFrozen(true);
                }
                break;
            case "Empress Thorina":
                int maxhealth = 0;
                int maxIdx = 0;
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).getHealth() > maxhealth) {
                        maxhealth = row.get(i).getHealth();
                        maxIdx = i;
                    }
                }
                row.remove(maxIdx);
                break;
            case "General Kocioraw":
                for (MinionCard card : row) {
                    card.increaseAttackDamage(1);
                    System.out.println("Increased attack of card: " + card.getAttackDamage());
                }
                break;
            case "King Mudface":
                for (MinionCard card : row) {
                    card.increaseHealth(1);
                    System.out.println("Increased health of card: " + card.getHealth());
                }
                break;
            default:
                break;
        }

    }

}
