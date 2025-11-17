package edu.sustech.xiangqi.model;

public abstract class AbstractPiece {
    private final String name;
    private final boolean isRed;
    private int row;
    private int col;

    public AbstractPiece(String name, int row, int col, boolean isRed) {
        this.name = name;
        this.row = row;
        this.col = col;
        this.isRed = isRed;
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

    public void moveTo(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    /**
     * 判断棋子是否可以移动到目标位置
     * @return 是否可以移动
     */
    public abstract boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model);

    public boolean isAPiece(int targetRow1, int targetCol1, ChessBoardModel model1) {//单独写炮的吃子方法！！！！！！！！！！！
        int currentRow = getRow();
        int currentCol = getCol();

        int rowDiff = targetRow1 - currentRow;
        int colDiff = Math.abs(targetCol1 - currentCol);
        int inColDiff = targetCol1 - currentCol;

        int count = 0;

        if (rowDiff > 0 && Math.abs(colDiff) == 0) {
            for (int i = 1; i < rowDiff; i++) {
                if (model1.getPieceAt(currentRow + i, currentCol) == null) {
                    count = count ;
                } else {
                    count = count + 1;
                }
            }
        }

        if (rowDiff < 0 && Math.abs(colDiff) == 0) {
            for (int i = -1; i > rowDiff; i--) {
                if (model1.getPieceAt(currentRow + i, currentCol) == null) {
                    count = count;
                } else {
                    count = count + 1;
                }
            }
        }



        if (rowDiff == 0 && inColDiff > 0) {
            for (int i = 1; i < inColDiff; i++) {
                if (model1.getPieceAt(currentRow, currentCol + i) == null) {
                    count = count;
                } else {
                    count = count + 1;
                }
            }
        }

        if (rowDiff == 0 && inColDiff < 0) {
            for (int i = -1; i > inColDiff; i--) {
                if (model1.getPieceAt(currentRow, currentCol + i) == null) {
                    count = count;
                } else {
                    count = count + 1;
                }
            }
        }
        if (count == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNoPiece(int targetRow2, int targetCol2, ChessBoardModel model2) {
        int currentRow = getRow();
        int currentCol = getCol();

        int rowDiff = targetRow2 - currentRow;
        int colDiff = Math.abs(targetCol2 - currentCol);
        int inColDiff = targetCol2 - currentCol;

        int count = 0;

        if (rowDiff > 0 && Math.abs(colDiff) == 0) {
            for (int i = 1; i < rowDiff; i++) {
                if (model2.getPieceAt(currentRow + i, currentCol) == null) {
                    count = count ;
                } else {
                    count = count + 1;
                }
            }
        }

        if (rowDiff < 0 && Math.abs(colDiff) == 0) {
            for (int i = -1; i > rowDiff; i--) {
                if (model2.getPieceAt(currentRow + i, currentCol) == null) {
                    count = count;
                } else {
                    count = count + 1;
                }
            }
        }



        if (rowDiff == 0 && inColDiff > 0) {
            for (int i = 1; i < inColDiff; i++) {
                if (model2.getPieceAt(currentRow, currentCol + i) == null) {
                    count = count;
                } else {
                    count = count + 1;
                }
            }
        }

        if (rowDiff == 0 && inColDiff < 0) {
            for (int i = -1; i > inColDiff; i--) {
                if (model2.getPieceAt(currentRow, currentCol + i) == null) {
                    count = count;
                } else {
                    count = count + 1;
                }
            }
        }
        if (count == 0) {
            return true;
        } else {
            return false;
        }
    }
}
