package Game;

import java.util.Collections;
import processing.core.PApplet;

public class Blackjack extends CardGame {
    Button standButton, plusButton, minusButton, dealButton;
    Button doubleButton, splitButton;
    
    String gameMessage = "";
    public int playerBalance = 1000; 
    public int currentBet = 100;     
    boolean isBettingPhase = true; 
    
    Hand splitHand;
    boolean isSplit = false;
    int activeHand = 1; 
    int splitBet = 0;

    public Blackjack() {
        super();
    }

    @Override
    protected void initializeGame() {
        super.initializeGame(); 
        
        drawButton.text = "Hit";
        standButton = new Button(drawButtonX + 110, drawButtonY, 100, 35, "Stand");
        doubleButton = new Button(drawButtonX + 220, drawButtonY, 100, 35, "Double");
        splitButton = new Button(drawButtonX + 330, drawButtonY, 100, 35, "Split");
        
        plusButton = new Button(BlackjackGUI.gameWidth/2 + 60, 400, 40, 40, "+");
        minusButton = new Button(BlackjackGUI.gameWidth/2 - 100, 400, 40, 40, "-");
        dealButton = new Button(BlackjackGUI.gameWidth/2 - 50, 460, 100, 40, "DEAL");

        gameMessage = "";
        isBettingPhase = true; 
        gameActive = false; 
        
        isSplit = false;
        activeHand = 1;
        splitHand = null;
        splitBet = 0;
    }

    private void startRound() {
        if (playerBalance < currentBet) return; 
        
        isBettingPhase = false;
        gameActive = true;
        playerOneTurn = true;
        
        isSplit = false;
        activeHand = 1;
        splitHand = null;
        
        Collections.shuffle(deck);

        for (int i = 0; i < 2; i++) {
            playerOneHand.addCard(deck.remove(0));
            Card dealerCard = deck.remove(0);
            if (i == 0) dealerCard.setTurned(true);
            playerTwoHand.addCard(dealerCard);
        }
        updateCardLayout();
    }

    private void updateCardLayout() {
        if (isSplit) {
            playerOneHand.positionCards(50, 450, 80, 120, 40);
            splitHand.positionCards(450, 450, 80, 120, 40);
            for (Card c : splitHand.getCards()) c.setClickableWidth(80);
        } else {
            playerOneHand.positionCards(50, 450, 80, 120, 40);
        }
        
        playerTwoHand.positionCards(50, 50, 80, 120, 40);
        for (Card c : playerOneHand.getCards()) c.setClickableWidth(80);
    }

    private boolean canDouble() {
        return !isSplit && playerOneHand.getSize() == 2 && playerBalance >= (currentBet * 2);
    }

    private boolean canSplit() {
        return !isSplit && playerOneHand.getSize() == 2 && 
               playerOneHand.getCard(0).value.equals(playerOneHand.getCard(1).value) && 
               playerBalance >= (currentBet * 2);
    }

    private void performDouble() {
        currentBet *= 2;
        playerOneHand.addCard(deck.remove(0));
        updateCardLayout();
        if (playerOneHand.getBlackjackTotal() > 21) {
            checkWinCondition(); 
        } else {
            switchTurns(); 
        }
    }

    private void performSplit() {
        isSplit = true;
        splitBet = currentBet;
        activeHand = 1;
        
        splitHand = new Hand();
        splitHand.addCard(playerOneHand.getCards().remove(1)); 
        
        playerOneHand.addCard(deck.remove(0));
        splitHand.addCard(deck.remove(0));
        updateCardLayout();
    }

@Override
    public void handleDrawButtonClick(int mouseX, int mouseY) {
        if (gameActive && playerOneTurn && drawButton.isClicked(mouseX, mouseY)) {
            
            // FIX: Replaced ternary operator with explicit if/else to satisfy JVM VerifyError
            Hand currentHand;
            if (activeHand == 1) {
                currentHand = playerOneHand;
            } else {
                currentHand = splitHand;
            }
            
            currentHand.addCard(deck.remove(0));
            updateCardLayout();
            
            if (currentHand.getBlackjackTotal() > 21) {
                if (isSplit && activeHand == 1) {
                    activeHand = 2; 
                } else if (isSplit && activeHand == 2) {
                    if (playerOneHand.getBlackjackTotal() > 21) {
                        checkWinCondition(); 
                    } else {
                        switchTurns(); 
                    }
                } else {
                    checkWinCondition(); 
                }
            }
        }
    }

    public void handleStandClick(int mouseX, int mouseY) {
        if (gameActive && playerOneTurn && standButton.isClicked(mouseX, mouseY)) {
            if (isSplit && activeHand == 1) {
                activeHand = 2; 
            } else {
                switchTurns(); 
            }
        }
    }

    @Override
    public void handleComputerTurn() {
        for (Card c : playerTwoHand.getCards()) c.setTurned(false);
        int dealerScore = playerTwoHand.getBlackjackTotal();

        if (dealerScore < 17) {
            playerTwoHand.addCard(deck.remove(0));
            updateCardLayout();
        } else {
            checkWinCondition();
        }
    }

    @Override
    public void checkWinCondition() {
        int dScore = playerTwoHand.getBlackjackTotal();
        boolean dBusts = dScore > 21;
        StringBuilder msg = new StringBuilder();
        
        int pScore1 = playerOneHand.getBlackjackTotal();
        if (pScore1 > 21) {
            msg.append(isSplit ? "Hand 1 Busted. " : "You Busted! Dealer Wins.");
            playerBalance -= currentBet;
        } else if (dBusts) {
            msg.append(isSplit ? "Hand 1 Wins (Dealer Bust)! " : "Dealer Busts! You Win!");
            playerBalance += currentBet;
        } else if (pScore1 > dScore) {
            msg.append(isSplit ? "Hand 1 Wins! " : "You Win!");
            playerBalance += currentBet;
        } else if (dScore > pScore1) {
            msg.append(isSplit ? "Hand 1 Loses. " : "Dealer Wins!");
            playerBalance -= currentBet;
        } else {
            msg.append(isSplit ? "Hand 1 Pushes. " : "It's a Tie (Push)!");
        }
        
        if (isSplit) {
            msg.append("\n");
            int pScore2 = splitHand.getBlackjackTotal();
            if (pScore2 > 21) {
                msg.append("Hand 2 Busted.");
                playerBalance -= splitBet;
            } else if (dBusts) {
                msg.append("Hand 2 Wins (Dealer Bust)!");
                playerBalance += splitBet;
            } else if (pScore2 > dScore) {
                msg.append("Hand 2 Wins!");
                playerBalance += splitBet;
            } else if (dScore > pScore2) {
                msg.append("Hand 2 Loses.");
                playerBalance -= splitBet;
            } else {
                msg.append("Hand 2 Pushes.");
            }
        }
        
        gameMessage = msg.toString();
        endGame();
    }

    private void endGame() {
        gameActive = false;
        for (Card c : playerTwoHand.getCards()) c.setTurned(false);
    }

    @Override
    public void handlePlayAgainClick(int mouseX, int mouseY) {
        if (playAgain.isClicked(mouseX, mouseY) && !gameActive && !isBettingPhase) {
            if (playerBalance <= 0) {
                playerBalance = 1000;
                currentBet = 100;
            } else if (currentBet > playerBalance) {
                currentBet = playerBalance;
            }
            initializeGame(); 
        }
        
        if (isBettingPhase) {
            if (plusButton.isClicked(mouseX, mouseY) && playerBalance >= currentBet + 10) currentBet += 10;
            if (minusButton.isClicked(mouseX, mouseY) && currentBet > 10) currentBet -= 10;
            if (dealButton.isClicked(mouseX, mouseY)) startRound();
        } else if (gameActive && playerOneTurn) {
            if (canDouble() && doubleButton.isClicked(mouseX, mouseY)) performDouble();
            if (canSplit() && splitButton.isClicked(mouseX, mouseY)) performSplit();
        }
    }

    @Override
    public void drawChoices(PApplet app) {
        if (isSplit && splitHand != null) splitHand.draw(app);

        if (isBettingPhase) {
            app.fill(0);
            app.textAlign(PApplet.CENTER);
            app.textSize(24);
            app.text("Select Your Bet", app.width/2, 350);
            app.textSize(32);
            app.text("$" + currentBet, app.width/2, 430);
            app.textSize(18);
            app.text("Balance: $" + playerBalance, app.width/2, 380);
            
            plusButton.draw(app);
            minusButton.draw(app);
            dealButton.draw(app);
            return;
        }

        drawButton.draw(app);
        standButton.draw(app);
        if (canDouble()) doubleButton.draw(app);
        if (canSplit()) splitButton.draw(app);
        
        app.fill(0);
        app.textSize(20);
        app.textAlign(PApplet.LEFT);
        app.text("Balance: $" + playerBalance + "   Bet: $" + currentBet, 50, 390);
        
        if (isSplit) {
            app.fill(activeHand == 1 ? app.color(0, 150, 0) : 0);
            app.text("Hand 1 Total: " + playerOneHand.getBlackjackTotal() + (activeHand == 1 ? " <--" : ""), 50, 430);
            app.fill(activeHand == 2 ? app.color(0, 150, 0) : 0);
            app.text("Hand 2 Total: " + splitHand.getBlackjackTotal() + (activeHand == 2 ? " <--" : ""), 450, 430);
        } else {
            app.fill(0);
            app.text("Your Total: " + playerOneHand.getBlackjackTotal(), 50, 430);
        }
        
        app.fill(0);
        if (!playerOneTurn || !gameActive) {
            app.text("Dealer Total: " + playerTwoHand.getBlackjackTotal(), 50, 190);
        } else {
            app.text("Dealer Total: ?", 50, 190);
        }
    }

    @Override
    public void drawPlayAgain(PApplet app) {
        if (gameActive || isBettingPhase) return;
        
        app.fill(0, 180);
        app.rect(0, 0, app.width, app.height);
        app.fill(255);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textSize(32);
        app.text(gameMessage, app.width / 2, 230);
        app.textSize(20);
        app.text("New Balance: $" + playerBalance, app.width/2, 290);
        playAgain.draw(app);
    }
}