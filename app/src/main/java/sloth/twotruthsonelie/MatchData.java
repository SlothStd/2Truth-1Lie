package sloth.twotruthsonelie;

import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by Robo on 29-Apr-16.
 */
public class MatchData {
    private static final String TAG = "2T1L MatchData";

    private String sentenceAuthor;
    private ArrayList<String> sentences;
    private int liePos;
    private ArrayList<Integer> scores;
    private int currentRound;
    private int hisXp;

    public MatchData(String sentenceAuthor, ArrayList<String> sentences, int liePos, ArrayList<Integer> scores, int currentRound) {
        this.sentenceAuthor = sentenceAuthor;
        this.sentences = sentences;
        this.liePos = liePos;
        this.scores = scores;
        this.currentRound = currentRound;
    }

    public MatchData(byte[] data){
        getData(data);
    }

    public MatchData(String data){
        getData(data);
    }

    public MatchData() {
        this.sentenceAuthor = "";
        this.sentences = new ArrayList<>();
        this.liePos = -1;
        this.scores = new ArrayList<>(Arrays.asList(new Integer[]{0, 0}));
        this.currentRound = 0;
        this.hisXp = 0;
    }

    public String getSentenceAuthor() {
        return sentenceAuthor;
    }

    public void setSentenceAuthor(String sentenceAuthor) {
        this.sentenceAuthor = sentenceAuthor;
    }

    public ArrayList<String> getSentences() {
        return sentences;
    }

    public void setSentences(ArrayList<String> sentences) {
        this.sentences = sentences;
    }

    public int getLiePos() {
        return liePos;
    }

    public void setLiePos(int liePos) {
        this.liePos = liePos;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public int getScores(int pos) {
        return scores.get(pos);
    }

    public void setScores(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    public void setScores(int pos, int value) {
        this.scores.set(pos, value);
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getHisXp() {
        return hisXp;
    }

    public void setHisXp(int hisXp) {
        this.hisXp = hisXp;
    }

    /**
     * 0. ID of the sentence author.
     * 1,2,3. 3 sentences.
     * 4. Position of the lie.
     * 5,6. p_1 score, p_2 score.
     * 7. current round
     *
     * ID~1|2|3~pos~score|score~round
     **/
    private void getData(byte[] data){
        String string = new String(data, Charset.forName("UTF-16"));
        char[] chars = string.toCharArray();

        String temp = "";
        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<Integer> tempIntList = new ArrayList<>();

        int i = 0;
        for (; i < chars.length; i++) {
            if (chars[i] == '~') {
                i++;
                break;
            }
            temp = temp + String.valueOf(chars[i]);
        }
        setSentenceAuthor(temp);
        temp = "";

        int pos = 0;
        for (; i < chars.length; i++) {
            if (chars[i] == '~') {
                i++;
                break;
            }
            if (chars[i] == '|') {
                pos++;
                continue;
            }

            try {
                tempList.set(pos, tempList.get(pos) + String.valueOf(chars[i]));
            } catch (IndexOutOfBoundsException e) {
                tempList.add(String.valueOf(chars[i]));
            }
        }
        setSentences(tempList);
        tempList = new ArrayList<>();

        for (; i < chars.length; i++) {
            if (chars[i] == '~') {
                i++;
                break;
            }
            temp = temp + String.valueOf(chars[i]);
        }
        setLiePos(Integer.parseInt(temp));
        temp = "";

        int scorePos = 0;
        for (; i < chars.length; i++) {
            if (chars[i] == '~') {
                i++;
                break;
            }
            if (chars[i] == '|') {
                scorePos++;
                continue;
            }
            try {
                tempIntList.set(scorePos, tempIntList.get(scorePos) + Integer.parseInt(String.valueOf(chars[i])));
            } catch (IndexOutOfBoundsException e) {
                tempIntList.add(Integer.parseInt(String.valueOf(chars[i])));
            }
        }
        setScores(tempIntList);
        tempIntList = new ArrayList<>();

        for (; i < chars.length; i++) {
            if (chars[i] == '~') {
                i++;
                break;
            }
            temp = temp + String.valueOf(chars[i]);
        }
        setCurrentRound(Integer.parseInt(temp));
        temp = "";

        for (; i < chars.length; i++) {
            if (chars[i] == '~') {
                i++;
                break;
            }
            temp = temp + String.valueOf(chars[i]);
        }
        setHisXp(Integer.parseInt(temp));
        temp = "";

        Log.d(TAG, "Received: " + string);
    }

    private void getData(String data){
        getData(data.getBytes(Charset.forName("UTF-16")));
    }

    public byte[] convertData(){
        return convertDataToString().getBytes(Charset.forName("UTF-16"));
    }

    public String convertDataToString(){

        String data;

        data = sentenceAuthor;

        for (int i = 0; i < sentences.size(); i++) {
            String temp = sentences.get(i);

            temp = temp.replace('~', '-');
            temp = temp.replace('|', '\\');

            sentences.set(i, temp);
        }

        try {
            data = data + "~" + (sentences.get(0));
            data = data + "|" + (sentences.get(1));
            data = data + "|" + (sentences.get(2));
        }catch (IndexOutOfBoundsException e){
            data = data + "~";
        }

        data = data + "~" + liePos;

        data = data + "~" + scores.get(0) + "|" + scores.get(1);

        data = data + "~" + currentRound;

        data = data + "~" + hisXp;

        Log.d(TAG, "Sent: " + data);

        return data;
    }

    public Boolean didIWin(int player){
        if (getScores(player) == getScores(Math.abs(player-1))){
            return null;
        }
        else if (getScores(player) > getScores(Math.abs(player-1))){
            return true;
        }
        else if (getScores(player) < getScores(Math.abs(player-1))){
            return false;
        }
        return null;
    }
}
