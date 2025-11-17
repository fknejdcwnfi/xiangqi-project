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

        //炮移动的方法，我这里没有涉及吃子的写法，单纯是写移动的方法。但是现在我尝试结合起来，这样才更简单完成这个代码！
        if ( (Math.abs(rowDiff) !=0 &&  Math.abs(colDiff) ==0) || (Math.abs(rowDiff) ==0 &&  Math.abs(colDiff) !=0)) {
            AbstractPiece target = model.getPieceAt(targetRow, targetCol);
            int between = countPiecesBetween(currentRow, currentCol, targetRow, targetCol, model);
            if (between < 0) return false;

            if (target == null) {
                // 普通移动：中间不能有子
                return between == 0;
            } else {
                // 吃子：目标必须是敌方，且中间恰好有 1 个子
                if (target.isRed() == this.isRed()) return false;
                return between == 1;
            }
        } else {
            return false;
        }
    }

    private int countPiecesBetween(int r1, int c1, int r2, int c2, ChessBoardModel model) {
        int count = 0;
        if (r1 == r2) {
            int start = Math.min(c1, c2) + 1;
            int end = Math.max(c1, c2) - 1;
            for (int c = start; c <= end; c++) {
                if (model.getPieceAt(r1, c) != null) count++;
            }
        } else if (c1 == c2) {
            int start = Math.min(r1, r2) + 1;
            int end = Math.max(r1, r2) - 1;
            for (int r = start; r <= end; r++) {
                if (model.getPieceAt(r, c1) != null) count++;
            }
        } else {
            // 不是直线，不是炮的移动
            return -1;
        }
        return count;
    }
}
