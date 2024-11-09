package org.poo.cards;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Card {
    protected String description;
    protected String colors;
    protected String name;

    public Card() {

    }
    public Card(String description, String colors, String name) {
        this.description = description;
        this.colors = colors;
        this.name = name;
    }

}
