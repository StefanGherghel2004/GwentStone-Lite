package org.poo.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.game.Game;

import java.util.ArrayList;

@Setter
@Getter
public class Card {
    protected String description;
    protected ArrayList<String> colors;
    protected String name;

    public Card() {

    }

    public Card(final CardInput cardinput) {
        this.description = cardinput.getDescription();
        this.colors = cardinput.getColors();
        this.name = cardinput.getName();
    }
    public Card(final String description, final ArrayList<String> colors, final String name) {
        this.description = description;
        this.colors = colors;
        this.name = name;
    }

    /**
     *
     * @param mapper ObjectMapper used for JSON creation
     * @return a JSON node representing the card
     *      returns null because it is overridden in subclasses Minioncard, HeroCard
     */
    public ObjectNode toJson(final ObjectMapper mapper) {
        return null;
    }

    /**
     * Allows the card to use its attack ability on a target
     *
     * @param target the card being attacked
     * @param game the current game context
     * @param mapper ObjectMapper used for generating JSON representation
     * @param attackerCoords coordinates of attacking card
     * @param targetCoords coordinates of target card
     * @return a JSON node representing the result of the attack (just in case of an error)
     *        returns null because it is overridden in subclass Minioncard
     */
    public ObjectNode useAttack(final MinionCard target, final  Game game,
                                final  ObjectMapper mapper, final Coordinates attackerCoords,
                                final Coordinates targetCoords) {
        return null;
    }
}
