package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutoWarning {
    public static AbstractPiece warningPiece(ChessBoardModel chessBoardModel, CurrentCamp camp) {//the camp refers to the user who is now to move the piece
        List<AbstractPiece> myPieces = new ArrayList<>();
        for (AbstractPiece piece : chessBoardModel.getPieces()) {
            if (piece.isRed() == camp.isRedTurn()) {
                myPieces.add(piece);
            }
        }

        AbstractPiece bestPiece = null;
        int maxScore = Integer.MIN_VALUE;

        // Iterate through all friendly pieces
        for (AbstractPiece piece : myPieces) {
            // Calculate the score of this piece's best available move
            int pieceBestMoveScore = pieceTotalMoveCount(chessBoardModel, piece, camp);

            if (pieceBestMoveScore > maxScore) {
                maxScore = pieceBestMoveScore;
                bestPiece = piece;
            }
            // Tie-breaking (randomly choose if score is the same)
            else if (pieceBestMoveScore == maxScore) {
                if (Math.random() < 0.5) { // 50% chance to replace on a tie
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
        int maxScore = Integer.MIN_VALUE;

        for (int row = 0; row < chessBoardModel.getRows(); row++) {
            for (int col = 0; col < chessBoardModel.getCols(); col++) {
                AbstractPiece target = chessBoardModel.getPieceAt(row, col);

                // 1. Check basic legality (Can the piece move there? Is it not a friendly piece?)
                if (!abstractPiece.canMoveTo(row, col, chessBoardModel) ||
                        (target != null && target.isRed() == abstractPiece.isRed())) {
                    continue;
                }

                // 2. Simulate the move and check for self-check (Crucial)
                ChessBoardModel currentModel = chessBoardModel.deepCopy();
                AbstractPiece currentPieceInCopy = currentModel.getPieceAt(abstractPiece.getRow(), abstractPiece.getCol());
                AbstractPiece targetPieceInCopy = currentModel.getPieceAt(row, col);

                if (targetPieceInCopy != null) {
                    currentModel.remove(targetPieceInCopy);
                }
                if (currentPieceInCopy != null) {
                    currentModel.movePieceForce(currentPieceInCopy, row, col);
                }

                // 3. Discard if it results in self-check
                if (currentModel.isInCheck(currentCamp.isRedTurn())) {
                    continue;
                }

                int moveScore = evaluateSingleMove(chessBoardModel, abstractPiece, row, col, currentCamp);

                // 5. Check if this is the best move found so far
                if (moveScore > maxScore) {
                    maxScore = moveScore;
                    bestDestination = new Point(row, col);
                }
                // Optional: Add tie-breaking logic (e.g., random selection or preference for certain moves)
                else if (moveScore == maxScore && Math.random() < 0.5) {
                    bestDestination = new Point(row, col);
                }

                // Fill the original lists (if required by calling code, otherwise delete this part)
                if (target == null) {
                    autoMoves.add(new Point(row, col));
                } else {
                    autoEats.add(new Point(row, col));
                }
            }
        }

        // Return the single best destination point in a list
        java.util.List<Point> chooseWhich = new java.util.ArrayList<>();
        if (bestDestination != null) {
            chooseWhich.add(bestDestination);
        }
        return chooseWhich;
    }

    public static int pieceTotalMoveCount(ChessBoardModel chessBoardModel, AbstractPiece abstractPiece, CurrentCamp currentCamp) {
        int totalScore = 0;
        int maxMoveScore = Integer.MIN_VALUE;

        boolean isCurrentPlayerRed = currentCamp.isRedTurn();

        for (int row = 0; row < chessBoardModel.getRows(); row++) {
            for (int col = 0; col < chessBoardModel.getCols(); col++) {
                AbstractPiece target = chessBoardModel.getPieceAt(row, col);
                if ((!abstractPiece.canMoveTo(row, col, chessBoardModel)) || (target != null && target.isRed() == isCurrentPlayerRed)) {
                    continue;
                }
                ChessBoardModel currentModel = chessBoardModel.deepCopy();
                AbstractPiece currentPieceInCopy = currentModel.getPieceAt(abstractPiece.getRow(), abstractPiece.getCol());

                if (target != null) {
                    AbstractPiece targetPieceInCopy = chessBoardModel.getPieceAt(row, col);
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

                int moveScore = 0;

                if (target != null) {
                    int captureValue = target.getValue();
                    if (captureValue == 10000) {
                        moveScore = moveScore + 15000;
                    } else {
                        moveScore = moveScore + captureValue;
                    }
                } else {
                    moveScore = moveScore + 100;
                }

                int attackPenalty = 0;
                for (AbstractPiece opponentPiece : currentModel.getPieces()) {
                    if (opponentPiece != null && opponentPiece.isRed() != isCurrentPlayerRed) {
                        if (opponentPiece.canMoveTo(row, col, currentModel)) {
                            attackPenalty += 1500;
                        }
                    }
                }

                int helpSocre = 0;
                for (AbstractPiece opponentPiece : chessBoardModel.getPieces()) {
                    if (opponentPiece != null && opponentPiece.isRed() != isCurrentPlayerRed) {
                        if (opponentPiece.canMoveTo(abstractPiece.getRow(), abstractPiece.getCol(), currentModel)) {
                            helpSocre += 1500;
                        }
                    }
                }
                moveScore = moveScore - attackPenalty +  helpSocre;


                if (currentModel.isInCheck(!isCurrentPlayerRed)) {
                    moveScore += 1400;
                }


                if (moveScore > maxMoveScore) {
                    maxMoveScore = moveScore;
                }
            }
        }

        // If no legal moves, return a huge penalty.
        return (maxMoveScore <= 0) ? -5000 : maxMoveScore;
    }

    private static int evaluateSingleMove(ChessBoardModel chessBoardModel, AbstractPiece abstractPiece, int toRow, int toCol, CurrentCamp currentCamp) {
        int score = 0;
        boolean isCurrentPlayerRed = currentCamp.isRedTurn();


        ChessBoardModel currentModel = chessBoardModel.deepCopy();

        AbstractPiece currentPieceInCopy = currentModel.getPieceAt(abstractPiece.getRow(), abstractPiece.getCol());
        AbstractPiece targetPieceInCopy = currentModel.getPieceAt(toRow, toCol);
        AbstractPiece originalTarget = chessBoardModel.getPieceAt(toRow, toCol); // Reference to the piece being captured


        if (targetPieceInCopy != null) {
            currentModel.remove(targetPieceInCopy);
        }
        if (currentPieceInCopy != null) {
            currentModel.movePieceForce(currentPieceInCopy, toRow, toCol);
        }


        // 2. --- Calculate Immediate Rewards (Benefits) ---

        // a. Capture Reward
        if (originalTarget != null) {
            int capturedValue = originalTarget.getValue();
            if (capturedValue == 10000) {
                // Huge reward for capturing the General/King (Checkmate opportunity)
                score += 15000;
            } else {
                // Reward for capturing a regular piece
                score += capturedValue;
            }
        } else {
            // Basic Mobility Reward (Slightly reward any safe, non-capture move)
            score += 100;
        }

        // b. Check Reward
        // Check if the move puts the opponent in check
        if (currentModel.isInCheck(!isCurrentPlayerRed)) {
            score += 1400;
        }

        int attackPenalty = 0;
        for (AbstractPiece opponentPiece : currentModel.getPieces()) {
            if (opponentPiece != null && opponentPiece.isRed() != isCurrentPlayerRed) {
                if (opponentPiece.canMoveTo(toRow, toCol, currentModel)) {
                    attackPenalty += 1500;
                }
            }
        }

        int helpSocre = 0;
        for (AbstractPiece opponentPiece : chessBoardModel.getPieces()) {
            if (opponentPiece != null && opponentPiece.isRed() != isCurrentPlayerRed) {
                if (opponentPiece.canMoveTo(abstractPiece.getRow(), abstractPiece.getCol(), currentModel)) {
                    helpSocre += 1500;
                }
            }
        }

        score = score - attackPenalty +  helpSocre;


        if (currentPieceInCopy != null && currentPieceInCopy.getName().equals("Pawn")) {
            // Assuming river is at row 5 for Red (top side) and row 4 for Black (bottom side)
            int riverRow = isCurrentPlayerRed ? 4 : 5;
            if ((isCurrentPlayerRed && toRow < riverRow) || (!isCurrentPlayerRed && toRow > riverRow)) {
                score += 200;
            }
        }

        return score;
    }
}
