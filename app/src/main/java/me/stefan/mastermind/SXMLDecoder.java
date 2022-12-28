package me.stefan.mastermind;

import android.os.Build;

import androidx.annotation.RequiresApi;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public abstract class SXMLDecoder {


    private static ExecutorService s;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Map<String, Object> SXMLMapEleminateFutures(List<SXMLEntry> set, int offset){
        if (s == null)
            s = Executors.newCachedThreadPool();

        return set.stream().map(x -> {
            if (x.getValue().getClass().getSimpleName().equals("FutureTask")){
                    try {
                        return SXMLMapEleminateFutures((List<SXMLEntry>)((FutureTask) x.getValue()).get(), offset+6);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
            }
            return x;
        }).collect(Collectors.toMap(x -> x.toString(), y-> y));
    }

    private static List<SXMLEntry> recursivelyParse(String input, String currentTag) {
        List<SXMLEntry> output = new ArrayList<>();
        String[] temp = null;
        do{

            temp = input.split("<");
            if (temp[0].length() != 0)
                output.add(new SXMLEntry(currentTag, temp[0]));
            if (temp.length > 1){
                String tag = "<" + temp[1].split(">")[0] + ">";
                int indexOfEndTag = input.indexOf(new StringBuilder(tag).insert(1, '/').toString());
                if (indexOfEndTag == -1) {
                    input = input.substring(tag.length());
                    continue;
                }
                String finalInput = input;
                output.add(new SXMLEntry(tag, s.submit(() -> recursivelyParse(finalInput.substring(finalInput.indexOf(tag) + tag.length(), indexOfEndTag), tag))));
                input = input.substring(indexOfEndTag + tag.length() + 1);
            }
        } while(temp.length > 1);
        return output;
    }

    public static class SXMLEntry {
        private final String key;
        private final Object value;

        public SXMLEntry(String key, String value) {
            this.value = value;
            this.key = key;
        }

        public SXMLEntry(String key, Future<List<SXMLEntry>> value) {
            this.value = value;
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public String getValueAsString() {
            return (String) value;
        }


    }
}
