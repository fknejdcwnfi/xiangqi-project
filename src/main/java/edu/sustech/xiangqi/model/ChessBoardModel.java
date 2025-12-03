package edu.sustech.xiangqi.model;

import edu.sustech.xiangqi.MoveEveryStep;
import edu.sustech.xiangqi.ui.ChessBoardPanel;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;


public class ChessBoardModel implements Serializable {
    // 储存棋盘上所有的棋子，要实现吃子的话，直接通过pieces.remove(被吃掉的棋子)删除就可以
    private final List<AbstractPiece> pieces;
    private static final int ROWS = 10;
    private static final int COLS = 9;
    private final List<MoveEveryStep> moveHistory;
    private static final long serialVersionUID = 1L;
    private transient ChessBoardPanel view; // Add this line

    public ChessBoardModel() {
        pieces = new ArrayList<>();
        moveHistory = new ArrayList<>();
        initializePieces();
    }

    public void setView(ChessBoardPanel view) {
        this.view = view;
    }

    // 添加棋子的方法
    public void addPiece(AbstractPiece piece) {
        pieces.add(piece);
    }

    private void initializePieces() {
        // 黑方棋子
        pieces.add(new GeneralPiece("將", 0, 4, false));
        pieces.add(new SoldierPiece("卒", 3, 0, false));
        pieces.add(new SoldierPiece("卒", 3, 2, false));
        pieces.add(new SoldierPiece("卒", 3, 4, false));
        pieces.add(new SoldierPiece("卒", 3, 6, false));
        pieces.add(new SoldierPiece("卒", 3, 8, false));
        pieces.add(new HousePiece("马", 0, 1, false));
        pieces.add(new HousePiece("马", 0, 7, false));
        pieces.add(new CarPiece("車", 0, 0, false));
        pieces.add(new CarPiece("車", 0, 8, false));
        pieces.add(new ShiPiece("士", 0, 3, false));
        pieces.add(new ShiPiece("士", 0, 5, false));
        pieces.add(new XiangPiece("象", 0, 2, false));
        pieces.add(new XiangPiece("象", 0, 6, false));
        pieces.add(new PaoPiece("炮", 2, 1, false));
        pieces.add(new PaoPiece("炮", 2, 7, false));


        // 红方棋子
        pieces.add(new GeneralPiece("帅", 9, 4, true));
        pieces.add(new SoldierPiece("兵", 6, 0, true));
        pieces.add(new SoldierPiece("兵", 6, 2, true));
        pieces.add(new SoldierPiece("兵", 6, 4, true));
        pieces.add(new SoldierPiece("兵", 6, 6, true));
        pieces.add(new SoldierPiece("兵", 6, 8, true));
        pieces.add(new HousePiece("馬", 9, 1, true));
        pieces.add(new HousePiece("馬", 9, 7, true));
        pieces.add(new CarPiece("车", 9, 0, true));
        pieces.add(new CarPiece("车", 9, 8, true));
        pieces.add(new ShiPiece("仕", 9, 3, true));
        pieces.add(new ShiPiece("仕", 9, 5, true));
        pieces.add(new XiangPiece("相", 9, 2, true));
        pieces.add(new XiangPiece("相", 9, 6, true));
        pieces.add(new PaoPiece("炮", 7, 1, true));
        pieces.add(new PaoPiece("炮", 7, 7, true));
    }

    public List<AbstractPiece> getPieces() {
        return pieces;
    }

    public AbstractPiece getPieceAt(int row, int col) {
        for (AbstractPiece piece : pieces) {
            if (piece.getRow() == row && piece.getCol() == col) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }


    public boolean movePiece(AbstractPiece piece, int newRow, int newCol) {//指AbstractPiece piece当前的棋子对象
        if (!isValidPosition(newRow, newCol)) {
            return false;
        }

        if (!piece.canMoveTo(newRow, newCol, this)) {//这里的this指当前调用该方法的对象‌，即‌调用 movePiece 方法的棋盘对象
            return false;
        }
        piece.moveTo(newRow, newCol);
        return true;
    }

    public boolean movePieceForce(AbstractPiece piece, int newRow, int newCol) {
        if (!isValidPosition(newRow, newCol)) return false;
        piece.moveTo(newRow, newCol);
        return true;
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }

    public void remove(AbstractPiece getTargetPiece){
        pieces.remove(getTargetPiece);
    }

    public void recordMove(MoveEveryStep move){
        moveHistory.add(move);
        System.out.print("Recorded move " + move.getMoveDescription());
    }

    public List<MoveEveryStep> getMoveHistory() {
        return moveHistory;
    }

    public void removeLastMove() {

        if (moveHistory.isEmpty()) {
            System.out.println("Error: Cannot take back a move. Move history is empty.");
            return;
        }
        MoveEveryStep lastMove = moveHistory.remove(moveHistory.size() - 1);
        trulyPhysicalMove(lastMove);

    }

    private void trulyPhysicalMove(MoveEveryStep move) {
        //下面的是获取最后一步的移动棋子

        AbstractPiece movingPiece = move.getMovingPiece();

        if (movingPiece == null) {
            System.err.println("CRITICAL: Moving piece in history is null. Cannot undo.");
            // You might consider re-adding the move to history here if you can't undo.
            return;
        }

        //下面是将移动棋子移动会原来的位置（开始位置）
        if  (movingPiece != null) {
            movingPiece.moveTo(move.getStartRow(), move.getStartCol());
        }
        //下面的是获取最后一步的被吃棋子
        AbstractPiece capturedPiece = move.getCapturedPiece();
        if (capturedPiece != null) {
            pieces.add(capturedPiece);//在当前的棋子model中加上被吃的棋子
            capturedPiece.moveTo(move.getEndRow(), move.getEndCol());//把背吃的棋子移动到最终的位置
        }
    }

    public AbstractPiece findGeneral(boolean isRed) {
        for (AbstractPiece piece : getPieces()) {
            // 假设帅/将的name是“帅”或“将”，或通过类型判断（如GeneralPiece）
            if ( (piece.getName().equals("帅") || piece.getName().equals("將"))
                    && piece.isRed() == isRed) {
                return piece;
            }
        }
        return null; // 理论上不会出现，游戏中帅/将必存在
    }

    /**
     * 判断当前回合方是否被将军（isRedTurn为true时，红方帅是否被黑方攻击）
     */
    public boolean isInCheck(boolean isRedTurn) {
        // 1. 找到当前回合方的帅/将
        AbstractPiece general = findGeneral(isRedTurn);

        if (general == null) {
            // Treat as "in check" or immediate loss
            return true; // Or throw an exception/log error
        }

        AbstractPiece opponentGeneral = findGeneral(!isRedTurn);
        if (general == null || opponentGeneral == null) return false;
        // ----------------------------------------------------
        // *** CRITICAL FIX: General Face-Off (Flying General) Check ***
        // ----------------------------------------------------
        int genRow = general.getRow();
        int genCol = general.getCol();
        int oppRow = opponentGeneral.getRow();
        int oppCol = opponentGeneral.getCol();

        if (genCol == oppCol) {
            boolean clearPath = true;

            int startRow = Math.min(genRow, oppRow) + 1;
            int endRow = Math.max(genRow, oppRow);

            for (int i = startRow; i <= endRow; i++) {
                if (getPieceAt(i, genCol) != null) {
                    clearPath = false;
                    break;
                }
            }
            if (clearPath) {
                return true;
            }
        }
        //==============================================================

        // 2. 遍历对方所有棋子，检查是否能移动到帅/将位置
        boolean isRedGeneral = general.isRed();
        for (AbstractPiece attacker : getPieces()) {
            // 只检查对方棋子（红帅被黑棋攻击，黑将被红棋攻击）
            if (attacker.isRed() == isRedGeneral) continue;
            // 关键：调用棋子的canMoveTo，判断是否能攻击帅/将
            if (attacker.canMoveTo(genRow, genCol, this)) {
                //ChessBoardPanel.setStatusMessage();
                return true; // 存在能攻击帅/将的棋子，判定为将军
            }
        }
        return false;
    }

    public ChessBoardModel deepCopy() {
        ChessBoardModel copy = new ChessBoardModel();
        // 复制所有棋子（需确保AbstractPiece实现Cloneable）

        copy.pieces.clear();//"Ghost" Pieces?

        for (AbstractPiece piece : getPieces()) {
            AbstractPiece pieceCopy = (AbstractPiece) piece.clone();
            copy.addPiece(pieceCopy);
        }
        return copy;
    }

    public boolean hasLegalMoves(boolean isRed) {
        for (AbstractPiece piece : pieces) {
            if (piece.isRed() != isRed) continue; // Skip opponent's pieces

            // Calculate potential moves (reuse logic similar to calculateLegalMoves)
            for (int row = 0; row < getRows(); row++) {
                for (int col = 0; col < getCols(); col++) {
                    AbstractPiece target = getPieceAt(row, col);
                    if (target != null && target.isRed() == piece.isRed()) continue; // Can't capture own piece

                    if (piece.canMoveTo(row, col, this)) {
                        // Simulate the move
                        ChessBoardModel copy = deepCopy();
                        AbstractPiece pieceCopy = copy.getPieceAt(piece.getRow(), piece.getCol());
                        AbstractPiece targetCopy = copy.getPieceAt(row, col);

                        if (targetCopy != null) {
                            copy.remove(targetCopy);
                        }

                        if (pieceCopy != null) {
                            copy.movePieceForce(pieceCopy, row, col);
                        }

                        // If simulation doesn't result in self-check, it's a legal move
                        if (!copy.isInCheck(isRed)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false; // No legal moves found
    }
}

