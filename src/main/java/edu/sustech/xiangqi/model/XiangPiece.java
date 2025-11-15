package edu.sustech.xiangqi.model;

public class XiangPiece extends AbstractPiece{
    public XiangPiece(String name, int row, int col, boolean isRed) {
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

        //象的移动（1.分黑白 2.不过楚河汉界 3.斜线的田字）
        // 1. 象必须走田字
        if (Math.abs(rowDiff) != 2 || Math.abs(colDiff) != 2) {
            return false;
        }

        // 2. 象不能过河
        if (isRed() && targetRow < 5) return false;
        if (!isRed() && targetRow > 4) return false;

        // 3. 判断象眼
        int eyeRow = getRow() + rowDiff / 2;
        int eyeCol = getCol() + inColDiff / 2;
        if (model.getPieceAt(eyeRow, eyeCol) != null) {
            return false; // 塞象眼
        }
        return true;
    }
}
