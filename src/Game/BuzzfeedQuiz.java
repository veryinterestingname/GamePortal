package Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BuzzfeedQuiz extends GameGeneric implements GameWriteable {

    private String finalResult = "N/A"; // Stores the outcome for the "score"

    @Override
    public void play() {
        Scanner sc = new Scanner(System.in);

        // 1. Setup Results
        Result macAndCheese = new Result("Mac and Cheese", "You are nostalgic, comforting, and reliable.");
        Result grilledCheese = new Result("Grilled Cheese and Tomato Soup", "You are classic, comforting, adaptable, and generous.");
        Result spicyRamen = new Result("Spicy Ramen", "You are bold, adventurous, and thrill seeking.");
        Result lavaCake = new Result("Molten Lava Cake", "You are deceptively simple, confident, and volatile.");

        // 2. Setup Questions
        List<Question> questions = new ArrayList<>();

        Question q1 = new Question("What do you wear?");
        q1.addAnswer(new Answer("A pajama set", lavaCake));
        q1.addAnswer(new Answer("An oversized hoodie and sweatpants", macAndCheese));
        q1.addAnswer(new Answer("A knitted sweater and wool socks", grilledCheese));
        q1.addAnswer(new Answer("Leather jacket and ripped jeans", spicyRamen));
        questions.add(q1);

        Question q2 = new Question("What movie/show do you watch?");
        q2.addAnswer(new Answer("A 90s sitcom like Friends", macAndCheese));
        q2.addAnswer(new Answer("A psychological thriller", spicyRamen));
        q2.addAnswer(new Answer("A nostalgic disney movie", grilledCheese));
        q2.addAnswer(new Answer("A period drama", lavaCake));
        questions.add(q2);

        Question q3 = new Question("What beverage do you pick?");
        q3.addAnswer(new Answer("A soda", macAndCheese));
        q3.addAnswer(new Answer("A coffee", lavaCake));
        q3.addAnswer(new Answer("Hot tea", grilledCheese));
        q3.addAnswer(new Answer("An energy drink", spicyRamen));
        questions.add(q3);

        Question q4 = new Question("Pick a dream vacation spot");
        q4.addAnswer(new Answer("Tokyo", spicyRamen));
        q4.addAnswer(new Answer("Paris", lavaCake));
        q4.addAnswer(new Answer("Beach resort", macAndCheese));
        q4.addAnswer(new Answer("Quiet cabin", grilledCheese));
        questions.add(q4);

        Question q5 = new Question("Pick a chaos level for your life right now");
        q5.addAnswer(new Answer("Low", grilledCheese));
        q5.addAnswer(new Answer("Medium", macAndCheese));
        q5.addAnswer(new Answer("High", spicyRamen));
        q5.addAnswer(new Answer("Very high", lavaCake));
        questions.add(q5);

        Question q6 = new Question("What’s your favorite season?");
        q6.addAnswer(new Answer("Autumn", macAndCheese));
        q6.addAnswer(new Answer("Winter", grilledCheese));
        q6.addAnswer(new Answer("Spring", lavaCake));
        q6.addAnswer(new Answer("Summer", spicyRamen));
        questions.add(q6);

        Question q7 = new Question("Which of these is your favorite animal?");
        q7.addAnswer(new Answer("Dog", lavaCake));
        q7.addAnswer(new Answer("Cat", grilledCheese));
        q7.addAnswer(new Answer("Bird", spicyRamen));
        q7.addAnswer(new Answer("Rabbit", macAndCheese));
        questions.add(q7);

        Question q8 = new Question("What’s your go-to weekend activity?");
        q8.addAnswer(new Answer("Going outdoors", macAndCheese));
        q8.addAnswer(new Answer("Quiet time at home", grilledCheese));
        q8.addAnswer(new Answer("Going to parties", spicyRamen));
        q8.addAnswer(new Answer("Exploring new hobbies", lavaCake));
        questions.add(q8);

        // 3. Game Tracking Variables
        Map<Result, Integer> scores = new HashMap<>();
        scores.put(macAndCheese, 0);
        scores.put(grilledCheese, 0);
        scores.put(spicyRamen, 0);
        scores.put(lavaCake, 0);

        List<Result> userHistory = new ArrayList<>();

        System.out.println("\n======= BUZZFEED QUIZ: WHAT COMFORT FOOD ARE YOU? ======");
        System.out.println("--------------------------------------------------------");

        // 4. Main Game Loop
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println("\nQuestion " + (i + 1) + ": " + q.getPrompt());

            for (int j = 0; j < q.getAnswers().size(); j++) {
                System.out.println((j + 1) + ". " + q.getAnswers().get(j).getText());
            }

            int choice = getValidInput(sc, q.getAnswers().size());
            Answer selectedAnswer = q.getAnswers().get(choice - 1);
            Result result = selectedAnswer.getAssociatedResult();

            scores.put(result, scores.get(result) + 1);
            userHistory.add(result);
        }

        // 5. Determine and Display Winner
        Result winner = determineWinner(scores, userHistory);

        System.out.println("\n==========================================");
        System.out.println("YOU ARE: " + winner.getName().toUpperCase());
        System.out.println("==========================================");
        System.out.println(winner.getDescription());
        System.out.println();

        // Save result so GameWriteable can write it to the file
        finalResult = winner.getName(); 
    }

    private int getValidInput(Scanner sc, int maxOption) {
        while (true) {
            System.out.print("Pick a number (1-" + maxOption + "): ");
            int parsed = ErrorCheck.getInt(sc);
            if (parsed >= 1 && parsed <= maxOption) {
                return parsed;
            } else {
                System.out.println(" >> Invalid choice! Please enter a number between 1 and " + maxOption + ".");
            }
        }
    }

    private Result determineWinner(Map<Result, Integer> scores, List<Result> history) {
        int maxScore = -1;
        for (int s : scores.values()) {
            if (s > maxScore) maxScore = s;
        }

        List<Result> topResults = new ArrayList<>();
        for (Map.Entry<Result, Integer> entry : scores.entrySet()) {
            if (entry.getValue() == maxScore) {
                topResults.add(entry.getKey());
            }
        }

        if (topResults.size() == 1) {
            return topResults.get(0);
        }

        System.out.println("\n(Tie-detected, calculating tie breaker. Earlier questions will be weighed more...)");

        for (Result previousChoice : history) {
            if (topResults.contains(previousChoice)) {
                return previousChoice;
            }
        }

        return topResults.get(0); // fallback, should never happen
    }



    @Override
    public String getScore() {
        return finalResult;
    }

    @Override
    public boolean isHighScore(String newScoreStr, String currentHighScoreStr) {
        // always saves latest result because high score doesn't really apply
        return true; 
    }



    private static class Result {
        private final String name;
        private final String description;

        public Result(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class Answer {
        private final String text;
        private final Result associatedResult;

        public Answer(String text, Result associatedResult) {
            this.text = text;
            this.associatedResult = associatedResult;
        }

        public String getText() {
            return text;
        }

        public Result getAssociatedResult() {
            return associatedResult;
        }
    }

    private static class Question {
        private final String prompt;
        private final List<Answer> answers;

        public Question(String prompt) {
            this.prompt = prompt;
            this.answers = new ArrayList<>();
        }

        public void addAnswer(Answer answer) {
            answers.add(answer);
        }

        public String getPrompt() {
            return prompt;
        }

        public List<Answer> getAnswers() {
            return answers;
        }
    }
}