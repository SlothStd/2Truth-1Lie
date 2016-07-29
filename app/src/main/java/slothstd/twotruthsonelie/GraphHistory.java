package slothstd.twotruthsonelie;

import android.util.Log;
import com.jjoe64.graphview.series.DataPoint;
import java.util.ArrayList;

/**
 * Created by Robo on 12-May-16.
 */
public class GraphHistory {
    private ArrayList<Boolean> myHistory;
    private ArrayList<Boolean> hisHistory;
    private ArrayList<Integer> lastScore;
    public final String matchID;
    private static final String TAG = "2T1L GraphHistory";

    public GraphHistory(String matchID) {
        this.myHistory = new ArrayList<>();
        this.hisHistory = new ArrayList<>();
        this.lastScore = new ArrayList<>();
        this.matchID = matchID;
    }

    public GraphHistory(ArrayList<Boolean> myHistory, ArrayList<Boolean> hisHistory, String matchID){
        this.myHistory = myHistory;
        this.hisHistory = hisHistory;
        this.lastScore = new ArrayList<>();
        this.matchID = matchID;
    }

    public GraphHistory(String source, String matchID){
        this.myHistory = new ArrayList<>();
        this.hisHistory = new ArrayList<>();
        this.lastScore = new ArrayList<>();
        this.matchID = matchID;

        char[] chars = source.toCharArray();

        int i = 0;
        for (; i < chars.length; i++){
            if (chars[i] == '~'){
                break;
            }

            if (chars[i] == '1')
                myHistory.add(true);
            else if (chars[i] == '0')
                myHistory.add(false);
        }
        for (; i < chars.length; i++){

            if (chars[i] == '1')
                hisHistory.add(true);
            else if (chars[i] == '0')
                hisHistory.add(false);
        }

        Log.d(TAG, "GraphHistory(): " + myHistory.toString() + " ; " + hisHistory.toString());
    }



    public ArrayList<Boolean> getMyHistory() {
        return myHistory;
    }

    public boolean getMyHistory(int pos){
        return myHistory.get(pos);
    }

    public void setMyHistory(ArrayList<Boolean> myHistory) {
        this.myHistory = myHistory;
    }

    public void setMyHistory(boolean isWin, int pos){
        this.myHistory.set(pos, isWin);
    }

    public void addMyHistory(boolean isWin){
        this.myHistory.add(isWin);

        Log.d(TAG, "addMyHistory(): " + myHistory.toString());
    }

    public int myWinCount(){
        int count = 0;

        for (int i = 0; i < myHistory.size(); i++){
            if(myHistory.get(i))
                count++;
        }

        return count;
    }



    public ArrayList<Boolean> getHisHistory() {
        return hisHistory;
    }

    public boolean getHisHistory(int pos){
        return myHistory.get(pos);
    }

    public void setHisHistory(ArrayList<Boolean> hisHistory) {
        this.hisHistory = hisHistory;
    }

    public void setHisHistory(boolean isWin, int pos){
        this.hisHistory.set(pos, isWin);
    }

    public void addHisHistory(boolean isWin){
        this.hisHistory.add(isWin);

        Log.d(TAG, "addHisHistory(): " + hisHistory.toString());
    }

    public int hisWinCount(){
        int count = 0;

        for (int i = 0; i < hisHistory.size(); i++){
            if(hisHistory.get(i))
                count++;
        }

        return count;
    }



    public ArrayList<Integer> getLastScore() {
        return lastScore;
    }

    public int getLastScore(int pos){
        return lastScore.get(pos);
    }

    public void setLastScore(ArrayList<Integer> lastScore) {
        this.lastScore = lastScore;

        Log.d(TAG, "setLastScore(): " + lastScore.toString());
    }

    public void setLastScore(int pos, int value) {
        this.lastScore.set(pos, value);
    }



    public String encrypt(){
        String s = "";

        for (int i = 0; i < myHistory.size(); i++){
            if (myHistory.get(i))
                s = s + "1";
            else
                s = s + "0";
        }

        s = s + "~";

        for (int i = 0; i < hisHistory.size(); i++){
            if (hisHistory.get(i))
                s = s + "1";
            else
                s = s + "0";
        }

        Log.d(TAG, "encrypt(): " + s);

        return s;
    }

    public void compare(ArrayList<Integer> scores, int player){
        if (hisWinCount() == scores.get(Math.abs(player - 1))){
            addHisHistory(false);
        }
        else {
            addHisHistory(true);
        }

        Log.d(TAG, "compare(): "+ hisWinCount() + " - "+ scores.get(Math.abs(player - 1)) + "; " + hisHistory.toString());
    }

    public DataPoint[] getMyDataPoints(){
        DataPoint[] dataPoints = new DataPoint[myHistory.size() + 1];
        dataPoints[0] = new DataPoint(0, 0);

        String debug = "";

        int score = 0;

        for (int i = 0; i < myHistory.size(); i++){
            if (myHistory.get(i)){
                score++;
            }

            dataPoints[i + 1] = new DataPoint(i + 1, score);

            debug = debug + dataPoints[i+1].toString();
        }
        Log.d(TAG, "getMyDataPoints(): " + debug);

        return dataPoints;
    }

    public DataPoint[] getHisDataPoints(){
        DataPoint[] dataPoints = new DataPoint[hisHistory.size() + 1];
        dataPoints[0] = new DataPoint(0, 0);

        String debug = "";

        int score = 0;

        for (int i = 0; i < hisHistory.size(); i++){
            if (hisHistory.get(i)){
                score++;
            }

            dataPoints[i + 1] = new DataPoint(i + 1, score);

            debug = debug + dataPoints[i+1].toString();
        }
        Log.d(TAG, "getHisDataPoints(): " + debug);

        return dataPoints;
    }
}
