package Game;

import processing.core.PApplet;
import java.util.concurrent.CountDownLatch;

public class BlackjackGUI extends PApplet implements GameWriteable {

    CardGame cardGame = new Blackjack();
    static int gameWidth = 1000;
    private int timer;
    private CountDownLatch latch;

    // ==========================================
    // GAME PORTAL OVERRIDES
    // ==========================================

    @Override
    public String getGameName() {
        return "Blackjack (Processing GUI)";
    }

    @Override
    public void play() {
        // Run the processing sketch
        PApplet.runSketch(new String[] { "BlackjackGUI" }, this);
        
        latch = new CountDownLatch(1); 
        try {
            latch.await(); // Pause the Terminal menu while window is open
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitActual() {
        // Doing nothing prevents the JVM from exiting for game portal
        if (latch != null) {
            latch.countDown(); 
        }
        if (this.getSurface() != null) {
            this.getSurface().setVisible(false); // Hide window cleanly
        }
    }

    @Override
    public String getScore() {
        if (cardGame instanceof Blackjack) {
            return String.valueOf(((Blackjack) cardGame).playerBalance);
        }
        return "0";
    }

    @Override
    public boolean isHighScore(String newScoreStr, String currentHighScoreStr) {
        if (currentHighScoreStr == null || currentHighScoreStr.equals("null")) return true;
        return Integer.parseInt(newScoreStr) > Integer.parseInt(currentHighScoreStr);
    }

    // ==========================================
    // PROCESSING LIFECYCLE
    // ==========================================

    @Override
    public void settings() {
        size(gameWidth, 800);   
    }

    @Override
    public void draw() {
        background(255);
        
        cardGame.playerOneHand.draw(this);
        cardGame.playerTwoHand.draw(this);
        cardGame.drawButton.draw(this);

        fill(0);
        textSize(16);
        textAlign(CENTER, CENTER);
        text("Current Player: " + cardGame.getCurrentPlayer(), width / 2, 20);
        text("Deck Size: " + cardGame.getDeckSize(), width / 2, height - 20);

        if (cardGame.getCurrentPlayer().equals("Player Two") && cardGame.gameActive) {
            fill(0);
            textSize(16);
            text("Dealer is thinking...", width / 2, height / 2 + 80);
            timer++;
            if (timer == 100) {
                cardGame.handleComputerTurn();
                timer = 0;
            }
        }

        cardGame.drawChoices(this);
        cardGame.drawPlayAgain(this);
    }

    @Override
    public void mousePressed() {
        cardGame.handleDrawButtonClick(mouseX, mouseY);
        
        if (cardGame instanceof Blackjack) {
            ((Blackjack) cardGame).handleStandClick(mouseX, mouseY);
        }
        
        cardGame.handleCardClick(mouseX, mouseY);
        cardGame.handlePlayAgainClick(mouseX, mouseY);
    }
}