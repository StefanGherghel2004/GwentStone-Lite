package org.poo.cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

@Setter
@Getter
public class Card {
    protected String description;
    protected ArrayList<String> colors;
    protected String name;

    public Card() {

    }

    public Card(CardInput cardinput) {
        this.description = cardinput.getDescription();
        this.colors = cardinput.getColors();
        this.name = cardinput.getName();
    }
    public Card(String description, ArrayList<String> colors, String name) {
        this.description = description;
        this.colors = colors;
        this.name = name;
    }

    public int getMana() {
        return 0;
    }

    public int getHealth() {
        return 0;
    }
}
