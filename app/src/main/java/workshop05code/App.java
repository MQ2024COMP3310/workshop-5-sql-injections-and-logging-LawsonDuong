package workshop05code;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "error loading logging.properties", e1);
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (!wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.SEVERE, "Not able to connect. Sorry!");
            return;
        } else {
            logger.log(Level.INFO, "Wordle created and connected.");
        }
        if (!wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.SEVERE, "Not able to launch. Sorry!");
            return;
        } else {
            logger.log(Level.INFO, "Wordle structures in place.");
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.length() != 4) { 
                    logger.log(Level.SEVERE, "Invalid word from file: " + line);
                } else {
                    wordleDatabaseConnection.addValidWord(i, line);
                    logger.log(Level.INFO, "Valid word added: " + line);
                    i++;
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Not able to load words from file.", e);
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            String guess;
            do {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();

                if (!guess.equals("q")) {
                    if (guess.length() != 4) {
                        logger.log(Level.WARNING, "Invalid guess: " + guess + " (guesses must be 4 letters)");
                        System.out.println("Invalid guess. Please ensure your guess is exactly 4 letters.");
                    } else {
                        if (wordleDatabaseConnection.isValidWord(guess)) { 
                            System.out.println("Success! It is in the list.\n");
                        } else {
                            System.out.println("Sorry. This word is NOT in the list.\n");
                            logger.log(Level.INFO, "Invalid guess (not in list): " + guess);
                        }
                    }
                }
            } while (!guess.equals("q"));
        } catch (NoSuchElementException | IllegalStateException e) {
           logger.log(Level.SEVERE, "error occurred during input.", e);
        }
    }
}
