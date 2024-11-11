package org.poo.cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CardInput;

import java.util.ArrayList;


@Setter
@Getter
public class MinionCard extends Card {
    @Getter
    private int mana;
    private int health;
    private int attackDamage;
    private boolean frozen;

    public MinionCard() {

    }


    public MinionCard(String name, String description, ArrayList<String> colors, int mana, int health, int attackDamage) {
        super(description, colors, name);
        this.mana = mana;
        this.health = health;
        this.attackDamage = attackDamage;
        this.frozen = false;
    }

    public MinionCard(CardInput cardinput) {
        super(cardinput);
        this.mana = cardinput.getMana();
        this.health = cardinput.getHealth();
        this.attackDamage = cardinput.getAttackDamage();
        this.frozen = false;
    }

    // copy constructor
    public MinionCard(MinionCard other) {
        super(other.description, other.colors, other.name);  // Copy fields from the Card superclass
        this.mana = other.mana;
        this.health = other.health;
        this.attackDamage = other.attackDamage;
        this.frozen = other.frozen;
    }

    public void useSpecialAbility(MinionCard target) {
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
                int temp = this.attackDamage;
                this.attackDamage = this.health;
                this.health = temp;
                break;
            case "Disciple":
                this.health += 2;
                break;
        }
    }

}
