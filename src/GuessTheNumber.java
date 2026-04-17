import java.util.Scanner;

import Game.ErrorCheck;
import Game.GameGeneric;
import Game.GameWriteable;

public class GuessTheNumber extends GameGeneric implements GameWriteable {
    
    private int score; // Keeps track of attempts

    @Override
    public void play() {
        Scanner sc = new Scanner(System.in);
        int target = (int) (Math.random() * 100) + 1; // Number 1-100
        score = 0;
        
        System.out.println("\n--- Guess The Number ---");
        System.out.println("I'm thinking of a number between 1 and 100.");
        
        while (true) {
            System.out.print("Enter your guess: ");
            // Assuming ErrorCheck is handling Scanners carefully
            int guess = ErrorCheck.getInt(sc);
            score++;
            
            if (guess == target) {
                System.out.println("You got it in " + score + " attempts!");
                break;
            } else if (guess > target) {
                System.out.println("Too high!");
            } else {
                System.out.println("Too low!");
            }
        }
    }

    @Override
    public String getScore() {
        return String.valueOf(score);
    }

    @Override
    public boolean isHighScore(String newScoreStr, String currentHighScoreStr) {
        // If there is no previous high score, the new score is automatically the best
        if (currentHighScoreStr == null || currentHighScoreStr.equals("null")) {
            return true;
        }

        int newScore = Integer.parseInt(newScoreStr);
        int currentHighScore = Integer.parseInt(currentHighScoreStr);

        // In Guess the Number, a LOWER score (fewer attempts) is better
        return newScore < currentHighScore;
    }
}