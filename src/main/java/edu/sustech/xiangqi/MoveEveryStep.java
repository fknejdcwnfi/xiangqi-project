package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.AbstractPiece;
import java.io.Serializable; // <--- IMPORANT: Must add this

public class MoveEveryStep implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String pieceName;
    private AbstractPiece capturedPiece;
    private AbstractPiece movingPiece;
    private final boolean isRed;
    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;
    private final String capturedPieceName;
    private CurrentCamp currentCamp;

 public MoveEveryStep(AbstractPiece piece, int newRow, int newCol, AbstractPiece target, CurrentCamp camp) {//参数用的是对象
     this.pieceName = piece.getName();
     this.movingPiece = piece;
     this.isRed = piece.isRed();
     this.startRow = piece.getRow();
     this.startCol = piece.getCol();
     this.endRow = newRow;
     this.endCol = newCol;
     this.capturedPieceName = (target != null) ? target.getName() : null;
     this.capturedPiece = target;
     this.currentCamp = camp;
     //piece指的是我要移动的棋子
     //newrow是新的行
     //newcol是目标列
     //target是目标位置的棋子（如果空的话则表示空位一）
 }


    public String getMoveDescription() {
     String camp = isRed ? "红方" : "黑方";
     String action = capturedPieceName != null ? "吃掉" + capturedPieceName : "移动";
     return String.format("%s: %s 从 (%d, %d) %s 到 (%d, %d)\n", camp, pieceName, startRow, startCol, action, endRow, endCol);
    }

    public int  getStartRow() {
    return startRow;
}

    public int getStartCol() {
      return startCol;
 }

    public int getEndRow() {
      return endRow;
}

    public int getEndCol() {
      return endCol;
}

    public AbstractPiece getCapturedPiece() {
     return this.capturedPiece;
  }

    public AbstractPiece getMovingPiece() {
     return this.movingPiece;
  }

  public CurrentCamp getCurrentCamp() {
     return this.currentCamp;
  }
}
