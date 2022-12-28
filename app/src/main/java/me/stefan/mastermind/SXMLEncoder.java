package me.stefan.mastermind;

import java.util.Map;

public class SXMLEncoder {
    private static StringBuilder output = new StringBuilder();

    public static String encode(Map<String, Object> sXML){
        recursiveEncode(sXML, 0);
        return output.toString();
    }

    private static void recursiveEncode(Map<String, Object> sXML, int indent){
        for (Map.Entry<String, Object> e : sXML.entrySet()) {
            print("<" + e.getKey() + ">", indent);
            if (e.getValue() instanceof String){
                print((String) e.getValue(), indent + 1);
            } else {
                recursiveEncode((Map<String, Object>) e.getValue(), indent + 1);
            }
            print("</" + e.getKey() + ">", indent);
        }
    }

    private static void print(String msg, int indent){
        for (int i = 0; i < indent * 4; i++) {
            output.append(" ");
        }

        output.append(msg + "\n");
    }
}
