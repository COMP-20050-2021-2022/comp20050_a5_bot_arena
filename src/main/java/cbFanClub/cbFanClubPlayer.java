// cbFanClub: 20437244 - 17328666 - 20371616
package cbFanClub;

import model.*;
import java.util.ArrayList;
import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Move;

public class cbFanClubPlayer extends SimpleBotPlayer{

    public cbFanClubPlayer(int playerNo) { super(playerNo);}

    private final double[] InitialWeights = {0.85, 0.05, 0.1};

    @Override
    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;

            // Play gamepiece "F" at the starting location in default orientation
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        } else {
            ArrayList<Move> playerMoves = getPlayerMoves(this, board);
            Move playerBestMove = playerMoves.get(0);
            double playerBestMoveScore = 0;

            for (Move playerMove : playerMoves) {
                Board possibleBoard = new Board(board);
                possibleBoard.makeMove(playerMove);

                ArrayList<Move> opponentMoves = getPlayerMoves(opponent, possibleBoard);
                double opponentBestMoveScore = 0;
                Move opponentBestMove;
                Board possibleBoard1 = new Board(possibleBoard);

                if (!opponentMoves.isEmpty()) {
                    opponentBestMove = opponentMoves.get(0);
                } else break; // If opponent has no moves break

                for (Move opponentMove : opponentMoves) {
                    double opponentCurrentMoveScore = positionEvaluation(possibleBoard1, opponent.getGamepieceSet(), getGamepieceSet());
                    // Get the best opponent move
                    if (opponentCurrentMoveScore > opponentBestMoveScore) {
                        opponentBestMoveScore = opponentCurrentMoveScore;
                        opponentBestMove = opponentMove;
                    }
                }
                possibleBoard1.makeMove(opponentBestMove);


                for (Move playerMove1 : playerMoves) {
                    Board possibleBoard4 = new Board(possibleBoard1);
                    possibleBoard4.makeMove(playerMove1);

                    double playerCurrentMoveScore = positionEvaluation(possibleBoard4, getGamepieceSet(), opponent.getGamepieceSet());
                    // Get the best move for player and assign initial move that led to that move as best move
                    if (playerCurrentMoveScore > playerBestMoveScore) {
                        playerBestMoveScore = playerCurrentMoveScore;
                        playerBestMove = playerMove;
                    }
                }
            }

            return playerBestMove;
        }
    }

    public double positionEvaluation(Board board, GamepieceSet pGamepieces, GamepieceSet oGamepieces) {
        int pSquares = getNumberSquares(board);

        int pNumCorners = getCorners(board, this.getPlayerNo());
        int oNumCorners = getCorners(board, opponent.getPlayerNo());

        return (pSquares/196.0) * InitialWeights[0] + (pNumCorners/49.0)*InitialWeights[1] + (1-(oNumCorners/49.0)) * InitialWeights[2];
    }

    // Get the number of squares occupied by the player
    public int getNumberSquares(Board board) {
        int pSquares = 0;

        for(int y = 0; y< Board.HEIGHT; y++) {
            for(int x = 0; x< Board.WIDTH; x++) {
                if(board.isOccupied(x, y)) {
                    if(board.getOccupyingPlayer(x, y) == this.getPlayerNo()) {
                        pSquares++;
                    }
                }
            }
        }

        return pSquares;
    }

    // Get the number of corners a player has
    public int getCorners(Board board, int playerNo) {
        int pNumCorners = 0;
        // Potential corners relative to a square
        int[][] cornerOffsets = {{-1, -1}, {1, 1}, {-1, 1}, {1, -1}};

        for (int y = 0; y < board.HEIGHT; y++) {
            for (int x = 0; x < board.WIDTH; x++) {
                if ((board.isOccupied(x, y))) {
                    if(board.getOccupyingPlayer(x, y) == playerNo) {
                        for (int i = 0; i < 4; i++) {
                            // If corner is on board
                            if ((x + cornerOffsets[i][0] >= 0 && x + cornerOffsets[i][0] < board.WIDTH) && (y + cornerOffsets[i][1] >= 0 && y + cornerOffsets[i][1] < board.HEIGHT)) {
                                // If squares surrounding corner aren't occupied
                                if (!board.isOccupied(x + cornerOffsets[i][0], y + cornerOffsets[i][1]) && !board.isOccupied(x, y + cornerOffsets[i][1]) && !board.isOccupied(x + cornerOffsets[i][0], y)) {
                                    pNumCorners++;
                                }
                            }
                        }
                    }
                }
            }
        }

        return pNumCorners;
    }
}
