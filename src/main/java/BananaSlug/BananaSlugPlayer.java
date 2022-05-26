/* Team BananaSlug
 * The BananaSlugPlayer class overrides the makeMove method to return the bot's next move
 *
 * @author Cheyenne Deibert 21209744,
 * Jasiu Latocha 21209746,
 * Jamie Carney 20368431
 * @version 5/1/2022
 */
package BananaSlug;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.ArrayList;

public class BananaSlugPlayer extends SimpleBotPlayer {

    private final static double NUM_CORNERS_OPEN_WEIGHT = 0.5;
    private final static double OPPONENT_CORNERS_OPEN_WEIGHT = -0.25;
    private final static double PLAYER_OCCUPIED_SQUARES_WEIGHT = 1.25;
    private final static double OPPONENT_OCCUPIED_SQUARES_WEIGHT = -1.0;


    public BananaSlugPlayer(int playerNo) {
        super(playerNo);
    }

    /**
     * makeMove - returns the best possible move
     *
     * @param board - current gameboard
     * @return the move that the bot has selected
     */
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

            // N.B.: The game control checks that we have at least one valid move before calling this method
            ArrayList<Move> moves = getPlayerMoves(this, board);
            Move bestMove = moves.get(0); //current best move
            double bestScore = 0; //current max score

            for (Move move : moves
            ) {
                double score;
                Board possibleBoard = new Board(board);
                possibleBoard.makeMove(move); //applies move to board
                score = evalFunction(possibleBoard, getPlayerNo()); //gets score from helper


                if (score > bestScore) {
                    bestMove = move;
                    bestScore = score;
                }
            }

            return bestMove;
        }
    }

    /**
     * evalFunction - Calculates a score for the board based on features
     *
     * @param board    - gameboard representation
     * @param playerNo - number of player
     * @return - score representing how optimal the state of the board is after a move
     */
    private double evalFunction(Board board, int playerNo) {
        int numCornersOpen = 0;
        int numOccupiedSquares = 0;
        int numOccupiedSquaresOpponent = 0;
        int numCornersOpenOpponent = 0;
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (board.isOccupied(x, y)) {

                    if (board.getOccupyingPlayer(x, y) == playerNo) {
                        ++numOccupiedSquares;
                        numCornersOpen += checkFourCorners(board, x, y);
                    } else {
                        ++numOccupiedSquaresOpponent;
                        numCornersOpenOpponent += checkFourCorners(board, x, y);
                    }

                }

            }
        }
        //Provides less weight to the size of the piece as the game continues
        //slowly diminishes over time, arbitrary number is just so that it doesn't go too low
        int totalSquares = ((Board.HEIGHT) * (Board.WIDTH));
        int totalOccupied = numOccupiedSquares + numOccupiedSquaresOpponent;
        int arbitraryNum = 50;
        double inverseWeightForPieceSize = (((totalSquares - totalOccupied) + arbitraryNum) / (totalSquares + arbitraryNum));
        double score = (NUM_CORNERS_OPEN_WEIGHT * numCornersOpen)
                + (PLAYER_OCCUPIED_SQUARES_WEIGHT * numOccupiedSquares * inverseWeightForPieceSize)
                + (OPPONENT_OCCUPIED_SQUARES_WEIGHT * numOccupiedSquaresOpponent * inverseWeightForPieceSize)
                + (OPPONENT_CORNERS_OPEN_WEIGHT * numCornersOpenOpponent);
        return score;

    }

    /**
     * checks if corners around a coordinate are open
     *
     * @param board - The current state of the board
     * @param x     - the x coordinate
     * @param y     - the y coordinate
     * @return - returns an int for the number of corners that are open
     */
    private int checkFourCorners(Board board, int x, int y) {
        int numCornersOpen = 0;
        if (inRange((x - 1), (y + 1))) {
            if (!board.isOccupied((x - 1), (y + 1))) {
                ++numCornersOpen;
            }
        }
        //top right corner
        if (inRange((x + 1), (y + 1))) {
            if (!board.isOccupied((x + 1), (y + 1))) {
                ++numCornersOpen;
            }
        }

        //bottom left corner
        if (inRange((x - 1), (y - 1))) {
            if (!board.isOccupied((x - 1), (y - 1))) {
                ++numCornersOpen;
            }
        }

        //bottom right corner
        if (inRange((x + 1), (y - 1))) {
            if (!board.isOccupied((x + 1), (y - 1))) {
                ++numCornersOpen;
            }
        }
        return numCornersOpen;
    }


    /**
     * Helper method to determine if a coordinate pair is in the correct range of board indices
     *
     * @param x -- x coordinate
     * @param y -- y coordinate
     * @return -- true if coordinate is in range
     */
    private Boolean inRange(int x, int y) {
        return ((x >= 0) && (x < Board.HEIGHT) && (y >= 0) && (y < Board.WIDTH));
    }


}
