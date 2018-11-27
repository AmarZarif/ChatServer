import java.io.*;
import java.util.ArrayList;

public class ChatFilter {

    private ArrayList<String> badWordList = new ArrayList<String>();
    String badWord;

    public ChatFilter(String badWordsFileName) {

        File f = new File(badWordsFileName);

        try (
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr)
        ) {


            while (true) {
                if ((badWord = br.readLine()) == null) {
                    break;
                }
                badWordList.add(badWord);
            }

        } catch (FileNotFoundException e) {
            System.out.println("No File");
        } catch (IOException e) {
            System.out.println("IO Exception");
        }

    }

    public String filter(String msg) {
        StringBuilder msgSB = new StringBuilder(msg);
        msg = msg.toLowerCase();
        for (String word : badWordList) {
            word = word.toLowerCase();
            String censor = "";

            for (int i = 0; i < word.length(); i++) {
                censor += "*";
            }

            msg = msg.replaceAll(word, censor);
        }
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '*')
                msgSB.replace(i, i + 1, "*");
//            msgSB.replace()
        }
        return msgSB.toString();

    }
}
