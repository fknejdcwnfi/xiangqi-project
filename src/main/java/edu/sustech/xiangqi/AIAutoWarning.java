package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AIAutoWarning {
    private static final int MAX_DEPTH = 3;

    public static AbstractPiece warningPiece(ChessBoardModel chessBoardModel, CurrentCamp camp) {//the camp refers to the user who is now to move the piece
        List<AbstractPiece> myPieces = new ArrayList<>();
        for (AbstractPiece piece : chessBoardModel.getPieces()) {
            if (piece.isRed() == camp.isRedTurn()) {
                myPieces.add(piece);
            }
        }

        AbstractPiece bestPiece = null;
        boolean isCurrentPlayerRed = camp.isRedTurn();
        int bestScore = isCurrentPlayerRed ? Integer.MIN_VALUE : Integer.MAX_VALUE;


        for (AbstractPiece piece : myPieces) {
            int pieceBestMoveScore = pieceTotalMoveCount(chessBoardModel, piece, camp);

            boolean isNewScoreBetter;
            if (isCurrentPlayerRed) {
                isNewScoreBetter = pieceBestMoveScore > bestScore;
            } else {
                isNewScoreBetter = pieceBestMoveScore < bestScore;
            }

            if (isNewScoreBetter) {
                bestScore = pieceBestMoveScore;
                bestPiece = piece;
            } else if (pieceBestMoveScore == bestScore) {
                if (Math.random() < 0.5) {
                    bestPiece = piece;
                }
            }
        }

        return bestPiece;
    }

    public static java.util.List<Point> chooseToMoveOrEat (ChessBoardModel chessBoardModel,AbstractPiece abstractPiece, CurrentCamp currentCamp, java.util.List<Point> autoMoves,java.util.List<Point> autoEats) {
        //todo(isincheck, the count value, return what?)
        autoMoves.clear();
        autoEats.clear();
        Point bestDestination = null;
        boolean isCurrentPlayerRed = currentCamp.isRedTurn();
        int bestScore = isCurrentPlayerRed ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int row = 0; row < chessBoardModel.getRows(); row++) {
            for (int col = 0; col < chessBoardModel.getCols(); col++) {
                AbstractPiece target = chessBoardModel.getPieceAt(row, col);

                if (!abstractPiece.canMoveTo(row, col, chessBoardModel) || (target != null && target.isRed() == abstractPiece.isRed())) {
                    continue;
                }

                ChessBoardModel currentModel = chessBoardModel.deepCopy();
                AbstractPiece currentPieceInCopy = currentModel.getPieceAt(abstractPiece.getRow(), abstractPiece.getCol());
                AbstractPiece targetPieceInCopy = currentModel.getPieceAt(row, col);

                if (targetPieceInCopy != null) {
                    currentModel.remove(targetPieceInCopy);
                }

                if (currentPieceInCopy != null) {
                    currentModel.movePieceForce(currentPieceInCopy, row, col);
                }

                if (currentModel.isInCheck(currentCamp.isRedTurn())) {
                    continue;
                }

                currentCamp.nextTurn(); // 临时切换到对手的回合
                int moveScore = miniMaxValue(currentModel, currentCamp, MAX_DEPTH);
                currentCamp.returnTurn();

               boolean isNewScoreBetter;
               if (isCurrentPlayerRed) {
                   isNewScoreBetter = moveScore > bestScore;
               } else {
                   isNewScoreBetter = moveScore < bestScore;
               }

               if (isNewScoreBetter) {
                   bestScore = moveScore;
                   bestDestination = new Point(row, col);
               } else {
                   if (moveScore == bestScore) {
                       if (Math.random() < 0.5) {
                           bestDestination = new Point(row, col);
                       }
                   }
               }

                if (target == null) {
                    autoMoves.add(new Point(row, col));
                } else {
                    autoEats.add(new Point(row, col));
                }
            }
        }

        java.util.List<Point> chooseWhich = new java.util.ArrayList<>();
        if (bestDestination != null) {
            chooseWhich.add(bestDestination);
        }
        return chooseWhich;
    }

    public static int pieceTotalMoveCount(ChessBoardModel chessBoardModel, AbstractPiece abstractPiece, CurrentCamp currentCamp) {
        boolean isCurrentPlayerRed = currentCamp.isRedTurn();
        int bestMoveScore = isCurrentPlayerRed ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int row = 0; row < chessBoardModel.getRows(); row++) {
            for (int col = 0; col < chessBoardModel.getCols(); col++) {
                AbstractPiece target = chessBoardModel.getPieceAt(row, col);
                if ((!abstractPiece.canMoveTo(row, col, chessBoardModel)) || (target != null && target.isRed() == isCurrentPlayerRed)) {
                    continue;
                }

                // --- Simulation Start ---
                ChessBoardModel currentModel = chessBoardModel.deepCopy();
                AbstractPiece currentPieceInCopy = currentModel.getPieceAt(abstractPiece.getRow(), abstractPiece.getCol());

                if (target != null) {
                    AbstractPiece targetPieceInCopy = currentModel.getPieceAt(row, col);
                    if (targetPieceInCopy != null) {
                        currentModel.remove(targetPieceInCopy);
                    }
                }
                if (currentPieceInCopy != null) {
                    currentModel.movePieceForce(currentPieceInCopy, row, col);
                }

                if (currentModel.isInCheck(currentCamp.isRedTurn())) {
                    continue;
                }

                currentCamp.nextTurn();
                int moveScore = miniMaxValue(currentModel, currentCamp, MAX_DEPTH);
                currentCamp.returnTurn();

                if (isCurrentPlayerRed) {
                    if (moveScore > bestMoveScore) {
                        bestMoveScore = moveScore;
                    }
                } else {
                    if (moveScore < bestMoveScore) {
                        bestMoveScore = moveScore;
                    }
                }
            }
        }

        if (isCurrentPlayerRed) {
            return (bestMoveScore == Integer.MIN_VALUE) ? -5000 : bestMoveScore;
        } else {
            // 如果是黑方，找不到合法走法时返回一个极大的正值作为惩罚
            return (bestMoveScore == Integer.MAX_VALUE) ? 5000 : bestMoveScore;
     }
    }

    public static int judgeCurrentModel(ChessBoardModel model) {
        int score = 0;

        int totalNumber = 0;
        int redAttackNumber = 0;
        int blackAttackNumber = 0;

        for (AbstractPiece piece : model.getPieces()) {
            totalNumber++;
            if (piece.isRed() && (piece.getName().equals("炮") || piece.getName().equals("兵") || piece.getName().equals("馬") || piece.getName().equals("车"))) {
                redAttackNumber++;
            }
            if (!piece.isRed() && (piece.getName().equals("炮") || piece.getName().equals("車") || piece.getName().equals("卒") || piece.getName().equals("马"))) {
                blackAttackNumber++;
            }
        }

        for (AbstractPiece piece : model.getPieces()) {
            if (piece != null) {
                int pieceValue = piece.getValue();

                if (piece.isRed()) {
                    score += pieceValue;
                } else {
                    score -= pieceValue;
                }

                if (piece.getName().equals("炮")) {
                 if (totalNumber <=16) {
                     if (piece.isRed()) {
                         score -= pieceValue;
                     } else  {
                         score += pieceValue;
                     }
                 }
                }

                if (piece.getName().equals("兵") || piece.getName().equals("卒")) {
                    int riverRow = piece.isRed() ? 5 : 4;
                    if (piece.isRed() && piece.getRow() > riverRow) {
                        score += 50;
                    }
                    if (piece.isRed() && redAttackNumber <= 6) {
                        score += 50;
                    }
                    if (!piece.isRed() && piece.getRow() < riverRow) {
                        score -= 50;
                    }
                    if (piece.isRed() && blackAttackNumber <= 6) {
                        score -= 50;
                    }
                }

                if (piece.getName().equals("马") || piece.getName().equals("馬")) {
                    int currentRow = piece.getRow();
                    int currentCol = piece.getCol();
                    int normalTotal = 8;
                    int count = 0;
                    for (int row = 0; row < model.getRows(); row++) {
                        for (int col = 0; col < model.getCols(); col++) {
                            if (piece.canMoveTo(row, col, model)) {
                                count++;
                            }
                        }
                    }
                    int lessValue = normalTotal - count;
                    if (piece.isRed()) {
                        score -= lessValue * 10;
                    }  else {
                        score += lessValue * 10;
                    }
                }

                if (piece.getName().equals("象") || piece.getName().equals("相")) {
                    int currentRow = piece.getRow();
                    int currentCol = piece.getCol();
                    int normalTotal = 4;
                    int count = 0;
                    for (int row = 0; row < model.getRows(); row++) {
                        for (int col = 0; col < model.getCols(); col++) {
                            if (piece.canMoveTo(row, col, model)) {
                                count++;
                            }
                        }
                    }
                    int lessValue = normalTotal - count;

                    if (piece.isRed()) {
                        score -= lessValue * 5;
                    }  else {
                        score += lessValue * 5;
                    }

                        if (piece.isRed()) {
                            if (redAttackNumber < blackAttackNumber) {
                                score += piece.getValue();
                            }
                        }  else {
                            if (blackAttackNumber < redAttackNumber) {
                                score -= piece.getValue();
                            }
                        }
                }

                if (piece.getName().equals("士") || piece.getName().equals("仕")) {
                    if (piece.isRed()) {
                        if (redAttackNumber < blackAttackNumber) {
                            score += piece.getValue();
                        }
                    }  else {
                        if (blackAttackNumber < redAttackNumber) {
                            score -= piece.getValue();
                        }
                    }
                }

            }
        }
        if (model.isInCheck(true)) {
            score -= 2000;
        } else {
            if (model.isInCheck(false)) {
                score += 2000;
            }
        }
        return score;
    }

    //the core AI method !
    private static int miniMaxValue(ChessBoardModel model, CurrentCamp camp, int depth) {
        boolean isCurrentPlayerRed = camp.isRedTurn();

        if (isInMate(model, camp)) {
            final int MATE_BASE_SCORE = 15000;

            int finalMateScore = MATE_BASE_SCORE + (MAX_DEPTH + 1) - depth;

            if (isCurrentPlayerRed) {
                // 轮到红方走，红方被将死：返回极小负分
                return -finalMateScore;
            } else {
                // 轮到黑方走，黑方被将死：返回极大正分（红方视角）
                return finalMateScore;
            }
        }

        if (depth == 0) {
            return judgeCurrentModel(model);
        }

        List<AbstractPiece> piecesToMove = new ArrayList<>();
        for (AbstractPiece p : model.getPieces()) {
            if (p.isRed() == isCurrentPlayerRed) {
                piecesToMove.add(p);
            }
        }

        int bestEval = isCurrentPlayerRed ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (AbstractPiece piece : piecesToMove) {
            for (int row =0; row < model.getRows(); row++) {
                for (int col =0; col < model.getCols(); col++) {
                    AbstractPiece target = model.getPieceAt(row, col);
                    if (!piece.canMoveTo(row, col, model) || (target != null && target.isRed() == isCurrentPlayerRed)) {
                        continue;
                    }

                    ChessBoardModel nextModel = model.deepCopy();
                    AbstractPiece currentPieceInCopy = nextModel.getPieceAt(piece.getRow(), piece.getCol());
                    AbstractPiece targetPieceInCopy = nextModel.getPieceAt(row, col);

                    if (targetPieceInCopy != null) {
                        nextModel.remove(targetPieceInCopy);
                    }
                    if (currentPieceInCopy != null) {
                        nextModel.movePieceForce(currentPieceInCopy, row, col);
                    }
                    if (nextModel.isInCheck(isCurrentPlayerRed)) {
                        continue;
                    }
                    camp.nextTurn();
                    int eval = miniMaxValue(nextModel, camp, depth - 1);
                    camp.returnTurn();

                    if (isCurrentPlayerRed) {
                        bestEval = Math.max(bestEval, eval);
                    } else {
                        bestEval = Math.min(bestEval, eval);
                    }
                }
            }
        }
        return bestEval;
    }

    public static boolean isInMate(ChessBoardModel model, CurrentCamp camp) {
        boolean isCurrentPlayerRed = camp.isRedTurn();

        if (!model.isInCheck(isCurrentPlayerRed)) {
            return false;
        }

        List<AbstractPiece> myPieces = new ArrayList<>();
        for (AbstractPiece p : model.getPieces()) {
            if (p.isRed() == isCurrentPlayerRed) {
                myPieces.add(p);
            }
        }
        for (AbstractPiece piece : myPieces) {
            for (int row = 0; row < model.getRows(); row++) {
                for (int col = 0; col < model.getCols(); col++) {

                    // 排除不合法的走法（例如走到了自己棋子的位置）
                    AbstractPiece target = model.getPieceAt(row, col);
                    if (!piece.canMoveTo(row, col, model) || (target != null && target.isRed() == isCurrentPlayerRed)) {
                        continue;
                    }

                    // 3. 模拟走法
                    ChessBoardModel nextModel = model.deepCopy();
                    AbstractPiece currentPieceInCopy = nextModel.getPieceAt(piece.getRow(), piece.getCol());
                    AbstractPiece targetPieceInCopy = nextModel.getPieceAt(row, col);

                    if (targetPieceInCopy != null) {
                        nextModel.remove(targetPieceInCopy);
                    }
                    if (currentPieceInCopy != null) {
                        nextModel.movePieceForce(currentPieceInCopy, row, col);
                    }

                    if (!nextModel.isInCheck(isCurrentPlayerRed)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}