package org.poo.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.game.Game;
import org.poo.player.Player;

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
    // this is set to the current round value after a successful attack
    private int attacked;

    public MinionCard() {

    }


    public MinionCard(final String name, final String description,
                      final ArrayList<String> colors, final int mana,
                      final int health, final int attackDamage) {
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
     * @param target The MinionCard card to attack
     * @param game current game context
     * @param mapper    ObjectMapper to generate JSON
     * @param attackerCoords coordinates of attacking card
     * @param targetCoords coordinates of attacked card
     * @return JSON response object containing the result of the attack
     *      returns null if the attack is successful
     */
    public ObjectNode useAttack(final MinionCard target, final Game game, final ObjectMapper mapper,
                                final Coordinates attackerCoords, final Coordinates targetCoords) {
        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("command", "cardUsesAttack");

        ObjectNode attackerNode = mapper.createObjectNode();
        attackerNode.put("x", attackerCoords.getX());
        attackerNode.put("y", attackerCoords.getY());
        responseNode.set("cardAttacker", attackerNode);

        ObjectNode targetNode = mapper.createObjectNode();
        targetNode.put("x", targetCoords.getX());
        targetNode.put("y", targetCoords.getY());
        responseNode.set("cardAttacked", targetNode);

        // check if the target is an enemy card
        if (!game.isEnemyCard(targetCoords.getX(), targetCoords.getY())) {
            responseNode.put("error", "Attacked card does not belong to the enemy.");
            return responseNode;
        }
        // check if the attacked is frozen
        if (isFrozen()) {
            responseNode.put("error", "Attacker card is frozen.");
            return responseNode;
        }

        // check if the attacker has already attacked this turn
        if (attacked == game.getNumRound()) {
            responseNode.put("error", "Attacker card has already attacked this turn.");
            return responseNode;
        }

        boolean hasTank = game.hasTankCard(game.turnToEnemy(game.getTurn()));
        // if the enemy has a Tank card it has to be attacked before non-tank cards
        if (hasTank && !target.isTank()) {
            responseNode.put("error", "Attacked card is not of type 'Tank'.");
            return responseNode;
        }

        // perform the attack
        target.decreaseHealth(attackDamage);

        // ff the target dies, remove it from the board
        if (target.getHealth() <= 0) {
            game.getBoard().removeCardWithCoordinates(targetCoords.getX(), targetCoords.getY());
        }

        // mark the attacker as having attacked this turn
        // (setting it to the current round value)
        this.setAttacked(game.getNumRound());

        return null;
    }

    /**
     * Use the ability of the Minion card on a target Minion card
     * @param target the card attacked
     * @param game current game context
     * @param mapper ObjectMapper to generate JSON
     * @param attackerCoords the coordinates of the attacking card
     * @param targetCoords the coordinates of the attacked card
     * @return JSON response containing the result of using the ability
     *      returns null if the use of special ability is successful
     */
    public ObjectNode useAbility(final MinionCard target, final Game game,
                                 final ObjectMapper mapper, final Coordinates attackerCoords,
                                 final Coordinates targetCoords) {
        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("command", "cardUsesAbility");

        ObjectNode attackerNode = mapper.createObjectNode();
        attackerNode.put("x", attackerCoords.getX());
        attackerNode.put("y", attackerCoords.getY());
        responseNode.set("cardAttacker", attackerNode);

        ObjectNode targetNode = mapper.createObjectNode();
        targetNode.put("x", targetCoords.getX());
        targetNode.put("y", targetCoords.getY());
        responseNode.set("cardAttacked", targetNode);

        int x = targetCoords.getX();
        int y = targetCoords.getY();

        // check if the attacker is frozen
        if (this.isFrozen()) {
            responseNode.put("error", "Attacker card is frozen.");
            return responseNode;
        }

        // check if the attacker has already attacked this turn
        if (this.getAttacked() == game.getNumRound()) {
            responseNode.put("error", "Attacker card has already attacked this turn.");
            return responseNode;
        }

        // if the card is "Disciple" type the attacked card must belong
        // to the current player
        if (this.getName().equals("Disciple") && game.isEnemyCard(x, y)) {
            responseNode.put("error", "Attacked card does not belong to the current player.");
            return responseNode;
        }
        // if the card is not "Disciple" type the attacked card must belong to enemy
        if (!this.getName().equals("Disciple") && !game.isEnemyCard(x, y)) {
            responseNode.put("error", "Attacked card does not belong to the enemy.");
            return responseNode;
        }

        // if the enemy has a Tank card it has to be attacked before non-tank cards
        boolean hasTank = game.hasTankCard(game.turnToEnemy(game.getTurn()));
        if (!this.getName().equals("Disciple") && hasTank && !target.isTank()) {
            responseNode.put("error", "Attacked card is not of type 'Tank'.");
            return responseNode;
        }

        // use the card's ability on the target
        this.useAbilityEffect(target);

        // ff the target dies after ability usage, remove it from the board
        if (target.getHealth() <= 0) {
            game.getBoard().removeCardWithCoordinates(targetCoords.getX(), targetCoords.getY());
        }

        // mark the attacker has attacked this round
        this.setAttacked(game.getNumRound());

        // Return null to indicate success
        return null;
    }

    /**
     * Performs an attack on the enemy's hero card
     * @param game the current game context
     * @param cardAttacker the coordinates of attacking card
     * @param mapper ObjectMapper to generate JSON
     * @return JSON response object containing the result of the attack
     *      returns null if the attack is successful
     */
    public ObjectNode useAttackHero(final Game game, final Coordinates cardAttacker,
                                    final ObjectMapper mapper) {
        Player currentPlayer = game.getBoard().getPlayer(game.getTurn());
        ObjectNode mainNode = mapper.createObjectNode();

        mainNode.put("command", "useAttackHero");
        ObjectNode attackerNode = mapper.createObjectNode();
        attackerNode.put("x", cardAttacker.getX());
        attackerNode.put("y", cardAttacker.getY());
        mainNode.set("cardAttacker", attackerNode);



        // check if the card is frozen
        if (isFrozen()) {
            mainNode.put("error", "Attacker card is frozen.");
            return mainNode;
        }

        // check if the card has already attacked in the current round
        if (attacked == game.getNumRound()) {
            mainNode.put("error", "Attacker card has already attacked this turn.");
            return mainNode;
        }

        // check if the enemy has a Tank card
        if (game.hasTankCard(game.turnToEnemy(game.getTurn()))) {
            mainNode.put("error", "Attacked card is not of type 'Tank'.");
            return mainNode;
        }

        // perform the attack
        HeroCard attackedHero = game.getBoard().getPlayerHero(game.turnToEnemy(game.getTurn()));
        attackedHero.decreaseHealth(attackDamage);

        // check if the enemy hero is defeated
        if (attackedHero.getHealth() <= 0) {
            String gameEndedMessage = (game.getTurn() == 1)
                    ? "Player one killed the enemy hero."
                    : "Player two killed the enemy hero.";
            game.incPlayerWins(game.getTurn());
            game.incGamesPlayed();
            mainNode = mapper.createObjectNode();
            mainNode.put("gameEnded", gameEndedMessage);
            return mainNode;
        }

        // mark that the minion card has attacked
        setAttacked(game.getNumRound());
        return null; // No errors, attack successful
    }
    /**
     *  Applies the effect of the special ability on the attacked card
     * @param target attacked card
     */
    public void useAbilityEffect(final MinionCard target) {
        switch (this.name) {
            case "The Ripper":
                // decreases the attack of the target with 2
                target.setAttackDamage(Math.max(0, target.getAttackDamage() - 2));
                break;
            case "Miraj":
                //swap health between this card and the target
                int tempHealth = this.health;
                this.health = target.getHealth();
                target.setHealth(tempHealth);
                break;
            case "The Cursed One":
                // swap health and attack of target
                int temp = target.attackDamage;
                target.attackDamage = target.health;
                target.health = temp;
                break;
            case "Disciple":
                // increases the health of target with 2
                target.increaseHealth(2);
                break;

            default:
                break;
        }
    }

    /**
     * Increase the health of current card by a given amount
     * @param inc the amount to increase
     */
    public void increaseHealth(final int inc) {
        health += inc;
    }

    /**
     * Determines which row the card should be placed
     * based on the player's index
     * @param card card being placed
     * @param playerIdx the index of the player (1 or 2)
     * @return the index of the row to place the card
     */
    public int rowToPlace(final MinionCard card, final int playerIdx) {
        Set<String> cards = Set.of("The Cursed One", "Disciple", "Sentinel", "Berserker");

        boolean backrow = cards.contains(card.getName());

        if (playerIdx == 1) {
            if (backrow) {
                return 3; // back row for player 1
            }
            return 2; // front row for player 1
        } else  {
            if (backrow) {
                return 0; // back fow for player 2
            }
            return 1; // front row of player 2
        }
    }

    /**
     * Deetermines whether the card is a Tank type card
     * @return true if the card is a Tank ("Goliath" or "Warden")
     *          false otherwise
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
     * Converts the card to a JSOn representation
     * @param mapper ObjectMapper used to generate the JSON
     * @return the JSON object representing the card
     */
    public ObjectNode toJson(final ObjectMapper mapper) {
        ObjectNode outputNode = mapper.createObjectNode();

        outputNode.put("mana", mana);
        outputNode.put("attackDamage", attackDamage);
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

    /**
     * Decreases the health of the card by the given amount
     * @param otherHealth given amount
     */
    public void decreaseHealth(final int otherHealth) {
        this.health -= otherHealth;
    }

    /**
     *Increases the attack of the card by a given amount
     * @param inc given amount
     */
    public void increaseAttackDamage(final int inc) {
        attackDamage += inc;
    }
}
