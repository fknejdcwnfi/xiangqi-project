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

        //移动的方法
        if ( (Math.abs(rowDiff) !=0 &&  Math.abs(colDiff) ==0) || (Math.abs(rowDiff) ==0 &&  Math.abs(colDiff) !=0)) {
            if (rowDiff >0 &&  Math.abs(colDiff) ==0) {
                boolean isNoPiece = true;

                for (int i = 1; i < rowDiff; i++) {
                    if (model.getPieceAt(currentRow + i, currentCol) == null) {
                        continue;
                    } else {
                        isNoPiece = false;
                        break;
                    }
                }

                if (isNoPiece) {
                    return true;
                }  else {
                    return false;
                }
            }

            if (rowDiff <0 && Math.abs(colDiff) ==0) {
                boolean isNoPiece = true;
                for (int i = -1; i > rowDiff; i--) {
                    if (model.getPieceAt(currentRow + i, currentCol) == null) {
                        continue;
                    }  else {
                        isNoPiece = false;
                        break;
                    }
                }
                if (isNoPiece) {
                    return true;
                }   else {
                    return false;
                }
            }

            if (rowDiff == 0 && inColDiff >0) {
                boolean isNoPiece = true;
                for (int i = 1; i < inColDiff; i++) {
                    if (model.getPieceAt(currentRow, currentCol + i) == null) {
                        continue;
                    }   else {
                        isNoPiece = false;
                        break;
                    }
                }
                if (isNoPiece) {
                    return true;
                } else {
                    return false;
                }
            }

            if (rowDiff == 0 && inColDiff < 0) {
                boolean isNoPiece = true;
                for (int i = -1; i > inColDiff; i--) {
                    if (model.getPieceAt(currentRow, currentCol + i) == null) {
                        continue;
                    }    else {
                        isNoPiece = false;
                        break;
                    }
                }
                if (isNoPiece) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
          return  true;
    }
}
