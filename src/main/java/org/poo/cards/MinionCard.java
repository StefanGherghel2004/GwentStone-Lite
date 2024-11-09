package org.poo.cards;

import lombok.Getter;
import lombok.Setter;

enum MinionType {
    Sentinel, Berseker, Goliath, Warden;
}

@Setter
@Getter
public class MinionCard extends Card {
    private int mana;
    private int health;
    private int attackDamage;
    private boolean frozen;
    private MinionType type;

    public MinionCard() {

    }
    public MinionCard(String name, String description, String colors, int mana, int health, int attackDamage, MinionType type) {
        super(description, colors, name);
        this.mana = mana;
        this.health = health;
        this.attackDamage = attackDamage;
        this.frozen = false;
        this.type = type;
    }
}
