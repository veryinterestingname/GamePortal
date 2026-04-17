


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Game.*;

public class GamePortal {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        File highscoreFile = new File("highscores.csv");
        
        // List to hold all your games
        List<Game> myGames = new ArrayList<>();
        
        
        myGames.add(new GuessTheNumber()); 
        myGames.add(new BlackjackGUI());
        myGames.add(new BuzzfeedQuiz());

        System.out.println("=================================");
        System.out.println("  WELCOME TO THE GAME PORTAL!  ");
        System.out.println("=================================");

        boolean running = true;
        while (running) {
            System.out.println("\nPlease select a game to play:");
            for (int i = 0; i < myGames.size(); i++) {
                System.out.println((i + 1) + ". " + myGames.get(i).getGameName());
            }
            System.out.println("0. Exit Portal");
            System.out.print("Enter your choice: ");

            int choice = ErrorCheck.getInt(sc);

            if (choice == 0) {
                System.out.println("Thanks for playing. Goodbye!");
                running = false;
            } else if (choice > 0 && choice <= myGames.size()) {
                // Get the chosen game
                Game selectedGame = myGames.get(choice - 1);
                
                System.out.println("\nStarting " + selectedGame.getGameName() + "...");
                
                // Play the game
                selectedGame.play();
                
                // Save high scores automatically after playing
                selectedGame.writeHighScore(highscoreFile);
                
            } else {
                System.out.println("Invalid choice. Please pick a number from the menu.");
            }
        }
        sc.close();
    }
}