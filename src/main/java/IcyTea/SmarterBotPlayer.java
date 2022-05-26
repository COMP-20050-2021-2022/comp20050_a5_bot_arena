package IcyTea;
/**
 * Team name: IcyTea
 * Team member:
 * Cathal Poon (20343243)
 * YanHao Sun (19205434)
 * Rubing Wang (20206497)
 */

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class SmarterBotPlayer extends SimpleBotPlayer {
    private static Random rng;
    public SmarterBotPlayer(int playerNo) {
        super(playerNo);
        if (rng == null){
            rng = new Random();
        }
    }

    @Override
    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;
            // random pick a game piece with size 5

            int random_int = rng.nextInt(12);
            int counter = 0;
            for (Map.Entry<String, Gamepiece> piece: getGamepieceSet().getPieces().entrySet()) {
                if (piece.getValue().getLocations().length == 5) {
                    counter++;
                }
                if (counter > random_int){
                    return new Move(this, piece.getValue(), piece.getKey(), new Location(board.startLocations[getPlayerNo()]));
                }
            }
        }

        int value;
        Move bestMove;

        ArrayList<Move> moves = getPlayerMoves(this, board);
        bestMove = moves.get(0);
        int bestNum = moveEval(bestMove);

        for (Move move: moves) {
            value = moveEval(move);

            if (bestNum < value){
                bestMove = move;
                bestNum = value;
            }
        }
        //Generating positions by applying moves
        //  to a copy of the current board:
        //Board possibleBoard = new Board(board);
        //possibleBoard.makeMove(move);

        return bestMove;
    }

    //give a value for each move so we can compare and determine the best move
    private int moveEval(Move move) {
        int value = move.getGamepiece().getLocations().length;
        //find number of pieces available after the move
        Board played = new Board(board);
        super.makeMove(played);
        value += 2 * getPlayerMoves(this, played).size();
        //check if cover opponent's corner
//      value += blockOpponent(played, this, move);
        value += cornerDifference(played);
        return value;
    }

    private int blockOpponent(Board board, Player player, Move move) {
        int[][] around = new int[][] {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
        Location[] positionsToCheck = move.getGamepiece().getLocations();
        for (Location coordinates: positionsToCheck) {
            for (int[] xy: around) {
                try {
                    if (board.isOccupied(coordinates.getX() + xy[0], coordinates.getY() + xy[1])) {
                        if(board.getOccupyingPlayer(coordinates.getX() + xy[0], coordinates.getY() + xy[1]) != getPlayerNo()) {
                            return 5;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {};
            }
        }
        return 0;
    }

    private int cornerDifference(Board board) {
        // index 0 for playerCorners, index 1 for opponentCorners;
        int[] cornersCount = new int[2];
        int[][] toCorners = new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
        int[][] toSides = new int[][] {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for(int x=0; x<board.WIDTH; x++) {
            for (int y=0; y<board.HEIGHT; y++) {
                //if is empty
                if (! board.isOccupied(x, y)) {
                    for (int[] corner: toCorners) {
                        //if the coordinates of corners are on the board
                        try {
                            if (board.isOccupied(x+corner[0], y+corner[1])) {
                                boolean validCorner = true;
                                //check if there is any more piece with the same color around
                                int pp = board.getOccupyingPlayer(x+corner[0], y+corner[1]);
                                for (int[] side : toSides) {
                                    if (pp == board.getOccupyingPlayer(x+side[0], y+side[1])) {
                                        //if a piece is adjacent, it is not a valid corner
                                        validCorner = false;
                                        break;
                                    }
                                }

                                if (validCorner) {
                                    cornersCount[pp]++;
                                }

                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {};
                    }
                }
            }
        }

        if (getPlayerNo()==0) {
            return cornersCount[0]-cornersCount[1];
        } else {
            return cornersCount[1]-cornersCount[0];
        }
    }





}
