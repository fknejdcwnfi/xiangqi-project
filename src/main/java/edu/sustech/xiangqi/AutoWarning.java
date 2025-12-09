package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutoWarning {
    private static final int CHECK_BONUS = 2000;
    private static final int CAPTURE_FLAT_BONUS = 500;
    private static final int ESCAPE_THREAT_BONUS = 500;
    private static final int MATE_SCORE = 15000;
    private static final int BASIC_MOVE_SCORE = 100;


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

                // 3. Discard if it results in self-check (Crucial for General safety)
                if (currentModel.isInCheck(currentCamp.isRedTurn())) {
                    continue;
                }

                // 4. Evaluate the move
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
        // This method is used by warningPiece to find the best piece. Its scoring logic must be consistent.
        int maxMoveScore = Integer.MIN_VALUE;
        boolean isCurrentPlayerRed = currentCamp.isRedTurn();

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
                // --- Simulation End ---

                // Discard move if it results in self-check
                if (currentModel.isInCheck(currentCamp.isRedTurn())) {
                    continue;
                }

                // Use the shared scoring logic
                int moveScore = evaluateSingleMove(chessBoardModel, abstractPiece, row, col, currentCamp);

                if (moveScore > maxMoveScore) {
                    maxMoveScore = moveScore;
                }
            }
        }

        // If no legal moves, return a huge penalty.
        return (maxMoveScore == Integer.MIN_VALUE) ? -5000 : maxMoveScore;
    }

    private static int evaluateSingleMove(ChessBoardModel chessBoardModel, AbstractPiece abstractPiece, int toRow, int toCol, CurrentCamp currentCamp) {
        int score = 0;
        boolean isCurrentPlayerRed = currentCamp.isRedTurn();
        AbstractPiece originalTarget = chessBoardModel.getPieceAt(toRow, toCol); // Reference to the piece being captured

        // 1. --- Simulate the move to calculate post-move scores ---
        ChessBoardModel currentModel = chessBoardModel.deepCopy();
        AbstractPiece currentPieceInCopy = currentModel.getPieceAt(abstractPiece.getRow(), abstractPiece.getCol());
        AbstractPiece targetPieceInCopy = currentModel.getPieceAt(toRow, toCol);

        if (targetPieceInCopy != null) {
            currentModel.remove(targetPieceInCopy);
        }
        if (currentPieceInCopy != null) {
            currentModel.movePieceForce(currentPieceInCopy, toRow, toCol);
        }

        // 2. --- Calculate Immediate Rewards (Benefits) ---

        // a. Capture Reward (EAT MORE PIECES CHANCE)
        if (originalTarget != null) {
            int capturedValue = originalTarget.getValue();
            if (capturedValue == 10000) {
                // Huge reward for capturing the General/King (Checkmate opportunity)
                score += MATE_SCORE;
            } else {
                // Reward for capturing a regular piece + flat bonus
                score += capturedValue + CAPTURE_FLAT_BONUS; // Enhanced capture priority
            }
        } else {
            // Basic Mobility Reward
            score += BASIC_MOVE_SCORE;
        }

        // b. Check Reward (BETTER GENERAL THREAT MANAGEMENT)
        // Check if the move puts the opponent in check
        if (currentModel.isInCheck(!isCurrentPlayerRed)) {
            score += CHECK_BONUS; // Increased check value
        }


        // 3. --- Safety and Positional Factors ---

        // c. Attacked Destination Penalty (Safety against loss)
        int MOVING_PIECE_VALUE = (currentPieceInCopy != null) ? currentPieceInCopy.getValue() : 0;
        int attackPenalty = 0;

        // Find the value of the strongest piece attacking the destination square
        for (AbstractPiece opponentPiece : currentModel.getPieces()) {
            if (opponentPiece != null && opponentPiece.isRed() != isCurrentPlayerRed) {
                if (opponentPiece.canMoveTo(toRow, toCol, currentModel)) {
                    // Penalty is the full value of the moving piece if it's attacked
                    attackPenalty = MOVING_PIECE_VALUE;
                    break;
                }
            }
        }

        // d. Escape Threat Reward (BETTER GENERAL THREAT MANAGEMENT: Moving piece out of danger)
        int helpSocre = 0;
        for (AbstractPiece opponentPiece : chessBoardModel.getPieces()) {
            if (opponentPiece != null && opponentPiece.isRed() != isCurrentPlayerRed) {
                // Check if the opponent could attack the STARTING square
                if (opponentPiece.canMoveTo(abstractPiece.getRow(), abstractPiece.getCol(), chessBoardModel)) {
                    helpSocre += ESCAPE_THREAT_BONUS; // Reduced bonus for escaping attack
                }
            }
        }

        score = score - attackPenalty +  helpSocre;


        // e. Pawn Advancement (Positional)
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