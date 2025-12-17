package edu.sustech.xiangqi;
import java.io.Serializable;


public class CurrentCamp implements Serializable {
    //true = 红方回合,false = 黑方回合
    private boolean isRedTurn;
    private static final long serialVersionUID = 1L;

    public CurrentCamp() {
        //默认游戏开始时是红方先走
        this.isRedTurn = true;
    }


    public boolean isRedTurn() {
        return isRedTurn;
    }


    public void nextTurn() {
        this.isRedTurn = !this.isRedTurn;
    }

    public void returnTurn() {
        this.isRedTurn = !this.isRedTurn;
    }

    //重置阵营
    public void reset() {
        this.isRedTurn = true;
    }
}
