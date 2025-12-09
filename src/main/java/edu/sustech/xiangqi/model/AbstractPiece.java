package edu.sustech.xiangqi.model;
import java.io.Serializable;

public abstract class AbstractPiece implements Serializable,Cloneable,Comparable<AbstractPiece>{
    private final String name;
    private final boolean isRed;
    private int row;
    private int col;
    private int value;
    private int totalMoveCount;
    private static final long serialVersionUID = 1L;

    public AbstractPiece(String name, int row, int col, boolean isRed,  int value) {
        this.name = name;
        this.row = row;
        this.col = col;
        this.isRed = isRed;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isRed() {
        return isRed;
    }

    public int getValue() {
        return value;
    }

    public void moveTo(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    /**
     * 判断棋子是否可以移动到目标位置
     * @return 是否可以移动
     */
    public abstract boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model);

    // 重写clone方法
    @Override
    public Object clone() {
        try {
            return super.clone(); // 浅克隆足够应对当前成员变量
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("克隆失败", e);
        }
    }

    @Override
    public int compareTo(AbstractPiece o) {//the small to the large
        if (this.value > o.value) {
            return 1;
        } else {
            if (this.value < o.value) {
                return -1;
            }  else {
                return 0;
            }
        }
    }

    public void setTotalMoveCount(int totalMoveCount) {
        this.totalMoveCount = totalMoveCount +  value;
    }
    public int getTotalMoveCount() {
        return totalMoveCount;
    }
}
