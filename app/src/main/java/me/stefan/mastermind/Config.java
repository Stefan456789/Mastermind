package me.stefan.mastermind;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Config {
    public final Character[] alphabet;
    public final int codeLength;
    public final boolean doubleAllowed;
    public final int guessRounds;
    public final char correctPositionSign;
    public final char correctCodeElementSign;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Config(String config, Consumer<String> error) {
        String[] configArr = config.split("\n");

        Character[] alphabet = {'1','2','3','4','5','6'};
        int codeLength = 4;
        boolean doubleAllowed = false;
        int guessRounds =12;
        char correctPositionSign = '+';
        char correctCodeElementSign = '-';

        boolean alphabetIsSet = false;
        boolean codeLengthIsSet = false;
        boolean doubleAllowedIsSet = false;
        boolean guessRoundsIsSet =false;
        boolean correctPositionSignIsSet = false;
        boolean correctCodeElementSignIsSet = false;

        for (String confItem : configArr) {
            String name = config.split("=")[0].trim();
            String value;
            if (config.split("=").length == 3)
                value = "=";
            else
                value = config.split("=")[1].trim();



            switch (name){
                case "alphabet":
                    alphabet = Arrays.stream(value.split(",")).map(x -> {
                        x = x.trim();
                        return x.charAt(0);
                    }).toArray(Character[]::new);
                    alphabetIsSet = true;
                    break;
                case "codeLength":
                    codeLength = Integer.parseInt(value.trim());
                    codeLengthIsSet = true;
                    break;
                case "doubleAllowed":
                    doubleAllowed = Boolean.parseBoolean(value.trim());
                    doubleAllowedIsSet = true;
                    break;
                case "guessRounds":
                    guessRounds = Integer.parseInt(value.trim());
                    guessRoundsIsSet = true;
                    break;
                case "correctPositionSign":
                    correctPositionSign = value.trim().charAt(0);
                    correctPositionSignIsSet = true;
                    break;
                case "correctCodeElementSign":
                    correctCodeElementSign = value.trim().charAt(0);
                    correctCodeElementSignIsSet = true;
                    break;
            }
        }
        if (!alphabetIsSet || !codeLengthIsSet || !correctCodeElementSignIsSet || !correctPositionSignIsSet || !doubleAllowedIsSet || !guessRoundsIsSet)
            error.accept("Error reading config!");

        this.alphabet = alphabet;
        this.codeLength = codeLength;
        this.guessRounds = guessRounds;
        this.doubleAllowed = doubleAllowed;
        this.correctPositionSign = correctPositionSign;
        this.correctCodeElementSign = correctCodeElementSign;
    }
}
