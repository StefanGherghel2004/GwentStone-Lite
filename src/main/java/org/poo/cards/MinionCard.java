package org.poo.cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CardInput;

import java.util.ArrayList;
import java.util.Set;


@Setter
@Getter
public final class MinionCard extends Card {
    @Getter
    private int mana;
    private int health;
    private int attackDamage;
    private boolean frozen;
    private int attacked;

    public MinionCard() {

    }


    public MinionCard(final String name, final String description, final ArrayList<String> colors, final int mana, final int health, final int attackDamage) {
        super(description, colors, name);
        this.mana = mana;
        this.health = health;
        this.attackDamage = attackDamage;
        this.frozen = false;
    }

    public MinionCard(final CardInput cardinput) {
        super(cardinput);
        this.mana = cardinput.getMana();
        this.health = cardinput.getHealth();
        this.attackDamage = cardinput.getAttackDamage();
        this.frozen = false;
    }

    // copy constructor
    public MinionCard(final MinionCard other) {
        super(other.description, other.colors, other.name);  // Copy fields from the Card superclass
        this.mana = other.mana;
        this.health = other.health;
        this.attackDamage = other.attackDamage;
        this.frozen = other.frozen;
    }

    /**
     *
     * @param target
     */
    public void useSpecialAbility(final MinionCard target) {
        if (isFrozen()) {
            return;
        }
        switch (this.name) {
            case "The Ripper":

                target.setAttackDamage(Math.max(0, target.getAttackDamage() - 2));
                break;
            case "Miraj":
                int tempHealth = this.health;
                this.health = target.getHealth();
                target.setHealth(tempHealth);
                break;
            case "The Cursed One":
                int temp = target.attackDamage;
                target.attackDamage = target.health;
                target.health = temp;
                break;
            case "Disciple":
                target.increaseHealth(2);
                break;

            default:
                break;
        }
    }

    private void increaseHealth(final int inc) {
        this.health += inc;
    }

    /**
     *
     * @param card
     * @param playerIdx
     * @return
     */
    public int rowToPlace(final MinionCard card, final int playerIdx) {
        Set<String> cards = Set.of("The Cursed One", "Disciple", "Sentinel", "Berserker");

        boolean backrow = cards.contains(card.getName());

        if (playerIdx == 1) {
            if (backrow) {
                return 3;
            }
            return 2;
        } else  {
            if (backrow) {
                return 0;
            }
            return 1;
        }
    }

    /**
     *
     * @return
     */
    public boolean isTank() {
        if (this.name.equals("Goliath")) {
            return true;
        }
        if (this.name.equals("Warden")) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param otherHealth
     */
    public void decreaseHealth(final int otherHealth) {
        this.health -= otherHealth;
    }

}
