package edu.sustech.xiangqi.model;

public class PaoPiece extends AbstractPiece{
    public PaoPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }
    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        int rowDiff = targetRow - currentRow;
        int colDiff = Math.abs(targetCol - currentCol);
        int inColDiff = targetCol - currentCol;

        //炮移动的方法，我这里没有涉及吃子的写法，单纯是写移动的方法。但是现在我尝试结合起来，这样才更简单完成这个代码！
        if ( (Math.abs(rowDiff) !=0 &&  Math.abs(colDiff) ==0) || (Math.abs(rowDiff) ==0 &&  Math.abs(colDiff) !=0)) {
            if (isAPiece(targetRow, targetCol, model) && model.getPieceAt(targetRow, targetCol) != null) {//这里先看看
                return true;
            } else {
                if (isNoPiece(targetRow, targetCol, model) && model.getPieceAt(targetRow, targetCol) == null) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
