package org.poo.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CardInput;
import org.poo.game.Game;
import org.poo.player.Player;

import java.util.ArrayList;

@Getter
public final class HeroCard extends Card {
    // standard health of a hero
    private static final int MAX_HEALTH = 30;
    private int mana;
    private int health = MAX_HEALTH;
    @Setter
    // this is set to the current round number after the card attacks successfully
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

    /**
     *
     * @param dec the quantity that is subtracted from hero's health
     */
    public void decreaseHealth(final int dec) {
        this.health -= dec;
    }

    /**
     *
     * @param row the index of the row that is affected by the special ability of the hero
     */
    public void useAbilityEffect(final ArrayList<MinionCard> row) {
        switch (name) {
                // Lord Royce will froze all the card on the target row
            case "Lord Royce":
                for (MinionCard card : row) {
                    card.setFrozen(true);
                }
                break;
                // Empress Thorina eliminates the card with the biggest health on the row
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
                // General Kocioraw increases the attack of all the card on the row with 1
            case "General Kocioraw":
                for (MinionCard card : row) {
                    card.increaseAttackDamage(1);
                }
                break;
                // King Mudface increases the health of all the cards on the row with 1
            case "King Mudface":
                for (MinionCard card : row) {
                    card.increaseHealth(1);
                }
                break;
            default:
                break;
        }

    }

    /**
     *
     * @param game      current game context
     * @param affectedRow the index of the row affected by the ability
     * @param mapper MapperObject used for JSON generation
     * @return an ObjectNode representing the result of using the ability
     *          return null if the ability is successful
     */
    public ObjectNode useAbility(final Game game, final  int affectedRow,
                                 final ObjectMapper mapper) {
        Player currentPlayer = game.getBoard().getPlayer(game.getTurn());
        ObjectNode mainNode = mapper.createObjectNode();

        mainNode.put("command", "useHeroAbility");
        mainNode.put("affectedRow", affectedRow);

        // check if the player has enough mana
        if (currentPlayer.getMana() < mana) {
            mainNode.put("error", "Not enough mana to use hero's ability.");
            return mainNode;
        }

        // check if the hero has already attacked this turn
        if (attacked == game.getNumRound()) {
            mainNode.put("error", "Hero has already attacked this turn.");
            return mainNode;
        }

        // if the hero is Lord Royce or Empress Thorina affected row has to belong to the enemy
        if (name.equals("Lord Royce") || name.equals("Empress Thorina")) {
            if (!game.isEnemyCard(affectedRow, 0)) {
                mainNode.put("error", "Selected row does not belong to the enemy.");
                return mainNode;
            }
        }

        // if the hero is General Kocioraw or King Mudface affected row has
        // to belong to the current player
        if (name.equals("General Kocioraw") || name.equals("King Mudface")) {
            if (game.isEnemyCard(affectedRow, 0)) {
                mainNode.put("error", "Selected row does not belong to the current player.");
                return mainNode;
            }
        }

        // ff all checks pass, use the hero's ability
        this.useAbilityEffect(game.getBoard().getRow(affectedRow));

        // decrease mana after using the ability
        currentPlayer.decreaseMana(mana);
        // set attacked after using ability
        setAttacked(game.getNumRound());
        return null;
    }

    /**
     *
     * @param mapper ObjectMapper used for JSON creation.
     * @return a JSON node representing the hero card.
     */
    public ObjectNode toJson(final ObjectMapper mapper) {

        ObjectNode outputNode = mapper.createObjectNode();

        outputNode.put("mana", mana);
        outputNode.put("description", description);
        outputNode.put("name", name);
        outputNode.put("health", health);

        ArrayNode colorsArray = mapper.createArrayNode();
        for (String color : colors) {
            colorsArray.add(color);
        }
        outputNode.set("colors", colorsArray);

        return outputNode;
    }

}
