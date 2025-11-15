package edu.sustech.xiangqi.model;

public class HousePiece extends AbstractPiece {
    public HousePiece(String name, int row, int col, boolean isRed) {
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

            if (Math.abs(rowDiff) == 2 &&  Math.abs(colDiff) == 1 ||  Math.abs(rowDiff) == 1 &&  Math.abs(colDiff) == 2) {
                int modelRow = getRow();//压马脚
                int modelCol = getCol();
                if (((rowDiff == -2 && colDiff == -1) && (model.getPieceAt(modelRow-1,modelCol) != null)) || ((rowDiff == -2 && colDiff == 1) && (model.getPieceAt(modelRow-1,modelCol) != null))) {
                    return false;
                } else {
                    if (((rowDiff == 2 && colDiff == 1) && (model.getPieceAt(modelRow+1,modelCol) != null)) || ((rowDiff == 2 && colDiff == -1) && (model.getPieceAt(modelRow+1,modelCol) != null))) {
                        return false;
                    }  else {
                        if (((rowDiff == -1 && colDiff == 2) && (model.getPieceAt(modelRow,modelCol + 1) != null)) || ((rowDiff == 1 && colDiff == 2) && (model.getPieceAt(modelRow,modelCol + 1) != null))) {
                            return false;
                        }else {
                            if (((rowDiff == -1 && colDiff == -2) && (model.getPieceAt(modelRow,modelCol - 1) != null)) || ((rowDiff == 1 && colDiff == -2) && (model.getPieceAt(modelRow,modelCol - 1) != null))) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }

            } else {
                return false;
            }

    }
}
