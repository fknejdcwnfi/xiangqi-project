package edu.sustech.xiangqi;
import edu.sustech.xiangqi.model.ChessBoardModel;
import java.io.Serializable;

//这是玩家状态属性的类，用来让玩家成为一个对象
public class PlayGameSession implements Serializable{
    private static final long serialVersionUID = 1L;
    private String PlayerNameID;
    private ChessBoardModel chessBoardModel;
    private CurrentCamp currentCamp;
    private String playingTime;
    private int secondsElapsed;
    private int redCampScore;
    private int blackCampScore;
    private int coins = 5;

    public PlayGameSession(String playerName) {
           this.PlayerNameID = playerName;
           this.chessBoardModel = new ChessBoardModel();
           this.currentCamp = new CurrentCamp();

    }

    public ChessBoardModel getChessBoardModel() {
        return chessBoardModel;
    }

    public CurrentCamp getCurrentCamp() {
        return currentCamp;
    }

    public void setCurrentCamp(CurrentCamp currentCamp) {
        this.currentCamp = currentCamp;
    }

    public String getPlayerNameID() {
        return PlayerNameID;
    }

    public void setModel(ChessBoardModel newModel) {
        this.chessBoardModel = newModel;
    }

    public void setPlayingTime(String playingTime) {
        this.playingTime = playingTime;
    }

    public String getPlayingTime() {
        return playingTime;
    }

    public void setSecondsElapsed(int secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
    }
    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void setRedCampScore(int score) {
        this.redCampScore = score;
    }
    public int getRedCampScore() {
        return redCampScore;
    }
    public void setBlackCampScore(int score) {
        this.blackCampScore = score;
    }
    public int getBlackCampScore() {
        return blackCampScore;
    }
    public int getCoins() {
        return coins;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }
}
