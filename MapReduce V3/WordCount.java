import java.io.*;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCount {

    public static void main(String[] args) {

        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        TreeMap<String,Integer> wordCount = new TreeMap<String,Integer>();

        try (
                BufferedReader src = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter dst = new BufferedWriter(new FileWriter("output.txt"));
        ){

            Matcher matcher ;
            String str = src.readLine();
            while(str!=null){
                if(!str.equals("")){
                    matcher = pattern.matcher(str);
                    while(matcher.find()){
                        String word = matcher.group();
                        if(!wordCount.containsKey(word))
                            wordCount.put(word,1);
                        else
                            wordCount.put(word,wordCount.get(word)+1);
                    }
                }
                str = src.readLine();
            }

            wordCount.keySet().forEach(k-> {
                try {
                    dst.write(k + "," + wordCount.get(k) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
