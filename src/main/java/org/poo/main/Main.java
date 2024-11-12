package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.cards.MinionCard;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.fileio.*;
import org.poo.game.Game;
import org.poo.inputhandler.InputHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;


/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();


          ObjectMapper mapper = new ObjectMapper();

         ObjectNode objectNode = mapper.createObjectNode();

         ArrayNode arrayNode = mapper.createArrayNode();
         arrayNode.add(objectNode);
         // implementing my approach for calling methods
         InputHandler handler = new InputHandler();
         ArrayList<GameInput> games = inputData.getGames();
         Game game = new Game();
         for (GameInput g : games) {
             game = handler.setInitialSetup(inputData, g);
             MinionCard oneCardDraw = game.getBoard().getPlayer(1).getUsingDeck().getCards().get(0);
             MinionCard twoCardDraw = game.getBoard().getPlayer(2).getUsingDeck().getCards().get(0);
             game.getBoard().getPlayer(1).getHand().add(oneCardDraw);
             game.getBoard().getPlayer(2).getHand().add(twoCardDraw);
             game.getBoard().getPlayer(1).getUsingDeck().getCards().removeFirst();
             game.getBoard().getPlayer(2).getUsingDeck().getCards().removeFirst();
             game.getBoard().getPlayer(1).setMana(1);
             game.getBoard().getPlayer(2).setMana(1);
             game.setNumRound(1);
             for (ActionsInput action : g.getActions()) {
                 handler.debugCommands(action, game, output);
             }
         }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
    // need of copy constructor

}
