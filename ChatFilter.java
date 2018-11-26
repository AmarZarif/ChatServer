import java.io.*;
import java.util.ArrayList;

/**
 * ChatFilter - Project 4
 *
 * @author Muhammad Raziq Raif Ramli, mramli@purdue.edu
 * @author Amar Zarif Azamin, aazamin@purdue.edu
 * @version 11/26/2018
 */
public class ChatFilter {

    private ArrayList<String> badWordList = new ArrayList<String>();
    String badWord;

    public ChatFilter(String badWordsFileName) {

        File f = new File(badWordsFileName);

        try(
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        ){


            while (true){
                if ((badWord=br.readLine())==null){
                    break;
                }
                badWordList.add(badWord);
            }

        }catch (FileNotFoundException e ){
            System.out.println("No File");
        }catch (IOException e){
            System.out.println("IO Exception");
        }

    }

    public String filter(String msg) {

        for (String word : badWordList){

            String censor = "";

            for (int i = 0; i < word.length(); i++) {
                censor += "*";
            }

            if (msg.equals(word)) {
                return censor;
            }
            if (word.length() == 1) {
                msg = msg.replaceAll(word, "*");
                return msg;
            }

            //if keyword is first word
            if (msg.indexOf(word) == 0 &&
                    !Character.isAlphabetic(msg.charAt(msg.indexOf(word) + word.length()))) {

                msg = censor + msg.charAt(msg.indexOf(word) + word.length()) +
                        msg.substring(msg.indexOf(word) + word.length() + 1);
            }


            if (msg.lastIndexOf(word) == msg.length() - word.length() &&
                    msg.charAt(msg.lastIndexOf(word) - 1) == ' ') {
                msg = msg.substring(0, msg.lastIndexOf(word)) + censor;
            }

            while (msg.contains(" " + word + " ")) {
                msg = msg.replaceAll("\\s" + word + "\\s", " " + censor + " ");
            }

            int index = 0;

            for (int i = -1; (msg.indexOf(" " + word, i + 1)) != -1; i++) {
//            System.out.println(i);
                i = (msg.indexOf(" " + word, i + 1));

                if (!Character.isAlphabetic(msg.charAt(i + word.length() + 1))) {
//                System.out.println("true");
                    msg = msg.substring(0, i) + " " + censor + msg.substring(i + word.length() + 1);
                }

            }



        }


        return msg;
    }
}
