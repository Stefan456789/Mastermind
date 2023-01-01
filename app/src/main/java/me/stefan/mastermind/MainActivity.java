package me.stefan.mastermind;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Entity;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private Game game;

    private ListView gameView;
    private EditText guess;

    private Config config;

    private ArrayList<String> gameList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    private boolean isSettings = false;

    private final String saveFileName = "savefile.sxml";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.LGame);
        guess = findViewById(R.id.TGuess);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                gameList
        );
        this.mAdapter = mAdapter;
        gameView.setAdapter(mAdapter);

        AssetManager assets = getAssets();
        String configString = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(assets.open("config.conf")));
            configString = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = new Config(configString ,this::sendToast);

        game = new Game(this, config);

    }

    public void sendToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void addList(String value){
        gameList.add(value);
        mAdapter.notifyDataSetChanged();
    }

    public void clearList(){
        gameList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showSettings(View v){
        gameList.clear();
        if (isSettings){
            isSettings = false;
        } else {
            isSettings = true;
            gameList.add("Alphabet: " + Arrays.stream(config.alphabet).map(String::valueOf).collect(Collectors.joining(", ")));
            gameList.add("CodeLength: " + config.codeLength);
            gameList.add("CorrectCodeElementSign: " + config.correctCodeElementSign);
            gameList.add("CorrectPositionSign: " + config.correctPositionSign);
            gameList.add("DoubleAllowed: " + config.doubleAllowed);
            gameList.add("GuessRounds: " + config.guessRounds);
        }
        mAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void load(View v){
        if (isSettings){
            showSettings(v);
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(saveFileName)));
            String encoded = reader.lines().collect(Collectors.joining());
            Map<String, Object> saveState = (Map<String, Object>) SXMLDecoder.decode(encoded).get("<saveState>");
            clearList();
            for (Map.Entry<String, Object> e : saveState.entrySet()){
                if (e.getKey().equals("<code>")){
                    String newSolution = getMapEntryValue(e.getValue());
                    if (newSolution != null)
                        game.setSolution(newSolution);
                }
                if (e.getKey().contains("guess")){
                    String result = getMapEntryValue(((Map<?, ?>) e.getValue()).get("<result>"));
                    String userInput = getMapEntryValue(((Map<?, ?>) e.getValue()).get("<userInput>"));

                    addList(removeComma(userInput) + "|" + removeComma(result));
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String removeComma(String e){
        return Arrays.stream(e.split(",")).map(String::trim).collect(Collectors.joining());
    }

    private static String getMapEntryValue(Object e){
        if (e instanceof String){
            return (String) e;
        } else if (e instanceof Map) {
            return (String)((Map<?, ?>) e).values().toArray()[0];
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void save(View v){

        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(saveFileName, MODE_PRIVATE));
            String encoded = SXMLEncoder.encode(convertBoardToMap(game.solution, gameList));
            writer.write(encoded);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Map<String, Object> convertBoardToMap(String solution, List<String> list){

        Map<String, Object> out = new HashMap<>();
        Map<String, Object> outer = new HashMap<>();
        out.put("code", solution);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> guess = new HashMap<>();
            guess.put("userInput" ,list.get(i).split("\\|")[0].chars().mapToObj(x -> (char) x + ", ").collect(Collectors.joining()));
            var debug = list.get(i).split("\\|");
            guess.put("result" ,list.get(i).split("\\|").length < 2 ? "" : list.get(i).split("\\|")[1].chars().mapToObj(x -> (char) x + ", ").collect(Collectors.joining()));;
            out.put("guess" + i, guess);
        }
        outer.put("saveState", out);
        return outer;
    }

    public void submit(View v){
        String guessString = guess.getText().toString();
        String s = game.submit(guessString, gameList.size());
        guess.getText().clear();
        if (s == null)
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        else if (s.contains("SOLVED")){
            addList(guessString + "|" + s);
            gameView.setOnItemClickListener((adapterView, view, pos, id) -> {
                if (adapterView.getAdapter().getItem(pos).toString().contains("SOLVED")){
                    clearList();
                }
            });
        } else
            addList(guessString + "|" + s);
    }

}