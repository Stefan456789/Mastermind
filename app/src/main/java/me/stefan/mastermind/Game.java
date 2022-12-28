package me.stefan.mastermind;

import android.util.Log;

public class Game {
    private final Config config;
    private final MainActivity mainActivity;
    String solution = "";
    boolean finished = false;

    public Game(MainActivity mainActivity, Config config) {
        this.config = config;
        this.mainActivity = mainActivity;
    }


    public String submit(String s, int tries) {
        if (tries >= config.guessRounds)
            return "NOT SOLVED! Solution was " + solution;
        if (solution.isEmpty() || tries == 0){
            for (int i = 0; i < config.codeLength; i++){
                char next = config.alphabet[(int) (Math.random() * config.alphabet.length)];
                if (!config.doubleAllowed && solution.contains("" + next)) {
                    i--;
                } else
                    solution += next;
            }
            Log.d("GameInfo", "Solution|" + solution);
        }

        if (s.length() != config.codeLength)
            return null;
        StringBuilder returnVal = new StringBuilder();
        finished = true;
        for (int i = 0; i < config.codeLength; i++){
            if (s.charAt(i) == solution.charAt(i)){
                returnVal.append("+");
            } else {
                finished = false;
                if (solution.contains("" + s.charAt(i))){
                    returnVal.append("-");

                }
            }
        }

        if (finished){
            returnVal = new StringBuilder("SOLVED");
        }

        return returnVal.toString();
    }
}
