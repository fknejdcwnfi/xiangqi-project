package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AIAutoWarning {
    private static final int MAX_DEPTH = 3;
    private static final int SURRENDER_THRESHOLD = 13000;
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

                int moveScore;
                try {
                    currentCamp.nextTurn(); // 临时切换到对手的回合
                    moveScore = miniMaxValue(currentModel, currentCamp, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
                } finally {
                    currentCamp.returnTurn(); // 确保状态恢复
                }

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

                int moveScore;
                try {
                    currentCamp.nextTurn(); // 临时切换到对手的回合
                    moveScore = miniMaxValue(currentModel, currentCamp, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
                } finally {
                    currentCamp.returnTurn(); // 确保状态恢复
                }

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
        return score;
    }

    //the core AI method !
    private static int miniMaxValue(ChessBoardModel model, CurrentCamp camp, int depth, int alpha, int beta) {
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
        List<Move> potentialMoves = new ArrayList<>();
        for (AbstractPiece piece : piecesToMove) {
            for (int row =0; row < model.getRows(); row++) {
                for (int col =0; col < model.getCols(); col++) {
                    AbstractPiece target = model.getPieceAt(row, col);
                    if (!piece.canMoveTo(row, col, model) || (target != null && target.isRed() == isCurrentPlayerRed)) {
                        continue;
                    }

                    // --- 走子模拟并检查自杀（送将） ---
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

                    // 计算走法优先级分数
                    int priorityScore = getMovePriorityScore(model, piece, row, col, camp);

                    potentialMoves.add(new Move(new Point(piece.getRow(), piece.getCol()), new Point(row, col), priorityScore));
                }
            }
        }
        // 2. 对走法进行排序：优先级分数高的走法排在前面
        potentialMoves.sort(Comparator.comparingInt(move -> move.priorityScore));
        java.util.Collections.reverse(potentialMoves); // 降序排列

        int bestEval = isCurrentPlayerRed ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : potentialMoves) {
// 重新模拟走法（使用排序后的走法信息）
            ChessBoardModel nextModel = model.deepCopy();
            AbstractPiece pieceToMove = nextModel.getPieceAt(move.start.y, move.start.x);
            AbstractPiece target = nextModel.getPieceAt(move.end.y, move.end.x);

            if (target != null) {
                nextModel.remove(target);
            }
            if (pieceToMove != null) {
                nextModel.movePieceForce(pieceToMove, move.end.y, move.end.x);
            }

            // 递归调用（重点修正！）
            int eval;
            try {
                camp.nextTurn();
                // *** 修正了两个关键错误：使用 nextModel 和 depth - 1 ***
                eval = miniMaxValue(nextModel, camp, depth - 1, alpha, beta);
            } finally {
                camp.returnTurn();
            }

            if (isCurrentPlayerRed) {
                bestEval = Math.max(bestEval, eval);
                alpha = Math.max(alpha, bestEval);
            } else {
                bestEval = Math.min(bestEval, eval);
                beta = Math.min(beta, bestEval);
            }
            // 剪枝
            if (beta <= alpha) {
                break;
            }
        }

        if (isCurrentPlayerRed) {
            if (bestEval == Integer.MIN_VALUE) {
                // 红方（MAX）找不到合法走法，返回一个极差的惩罚分（小于将死分）
                return -10000;
            }
        } else {
            if (bestEval == Integer.MAX_VALUE) {
                // 黑方（MIN）找不到合法走法，返回一个极好的奖励分（红方视角，小于将死分）
                return 10000;
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

    private static int getMovePriorityScore(ChessBoardModel model, AbstractPiece piece, int targetRow, int targetCol, CurrentCamp camp) {
        int score = 0;
        AbstractPiece target = model.getPieceAt(targetRow, targetCol);

        if (target != null) {
            int targetValue = target.getValue();
            int pieceValue = piece.getValue();

            score += 1000 + (targetValue * 10) - pieceValue;
        }

        ChessBoardModel nextModel = model.deepCopy();
        AbstractPiece currentPieceInCopy = nextModel.getPieceAt(piece.getRow(), piece.getCol());

        // 模拟吃子和移动 (与 miniMaxValue 中的逻辑相同)
        if (target != null) {
            AbstractPiece targetPieceInCopy = nextModel.getPieceAt(targetRow, targetCol);
            if (targetPieceInCopy != null) {
                nextModel.remove(targetPieceInCopy);
            }
        }
        if (currentPieceInCopy != null) {
            nextModel.movePieceForce(currentPieceInCopy, targetRow, targetCol);
        }

        // 使用 try-finally 块确保 camp 状态安全恢复 (P1 优化)
        try {
            camp.nextTurn(); // 临时切换到对手回合

            if (nextModel.isInCheck(camp.isRedTurn())) {
                // P2: 将军得分 (9000分，高于所有吃子走法)
                score += 9000;

                // P1: 将死检查 (Checkmate Score)
                if (isInMate(nextModel, camp)) {
                    // 如果能将死，优先级绝对最高
                    score += 100000;
                }
            }
        } finally {
            camp.returnTurn(); // 恢复回合状态
        }
        return score;
    }

    public static boolean shouldBlackAISurrender(ChessBoardModel chessBoardModel, CurrentCamp currentCamp) {
        // 仅对黑方（非红方）进行投降检查
        if (currentCamp.isRedTurn()) {
            return false;
        }

        boolean isCurrentPlayerRed = currentCamp.isRedTurn();
        int bestPossibleScoreForBlack = Integer.MAX_VALUE;
        List<AbstractPiece> myPieces = new ArrayList<>();

        for (AbstractPiece piece : chessBoardModel.getPieces()) {
            if (piece.isRed() == isCurrentPlayerRed) {
                myPieces.add(piece);
            }
        }

        // 如果黑方棋子为空，则已经输了，无需投降
        if (myPieces.isEmpty()) {
            return false;
        }


        for (AbstractPiece piece : myPieces) {
            // pieceTotalMoveCount 返回的是该棋子能达到的最低分数
            int pieceBestMoveScore = pieceTotalMoveCount(chessBoardModel, piece, currentCamp);
            // 黑方 (MIN player) 寻找所有走法中的最小分数（即对黑方最好的结果）
            bestPossibleScoreForBlack = Math.min(bestPossibleScoreForBlack, pieceBestMoveScore);
        }


        if (bestPossibleScoreForBlack >= SURRENDER_THRESHOLD) {
            return true;
        }

        return false;
    }
}