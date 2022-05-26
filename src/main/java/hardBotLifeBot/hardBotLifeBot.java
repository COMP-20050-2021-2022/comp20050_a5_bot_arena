/* Name: hardBotLife
    Conor Nolan - 20444426
    James Needham - 20316686
    Jack Byrne - 20413314
 */
package hardBotLifeBot;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.*;

public class hardBotLifeBot extends SimpleBotPlayer {
    int turnNo = 0;

    // Constructor for Bot
    public hardBotLifeBot(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }
    // Method to get the rubiks length of a gamepiece
    public int getRubiksLength(String pieceName) {
        switch (pieceName) {
            case "I1":
                return 2;
            case "I2":
                return 3;
            case "I3":
            case "V3":
            case "O4":
            case "T4":
            case "X":
            case "Y":
                return 4;
            case "I4":
            case "L3":
            case "Z4":
            case "T5":
            case "P":
            case "U":
            case "F":
                return 5;
            case "I5":
            case "L5":
            case "V5":
            case "N":
            case "Z5":
            case "W":
                return 6;
            default: return -1;
        }
    }
    // Method to get the true length of a game piece
    public int getTrueLength(String pieceName) {
        int value = getRubiksLength(pieceName);

        if (value > 0) {
            return value - 1;
        } else {
            return -1;
        }
    }
    // Copy of method from board class, used for move validation
    private boolean playerCanPlayInOrientation(Board board, Gamepiece piece) {
        boolean result = false;
        Move move = new Move(this, piece, "", new Location(0, 0));
        for (int x = 0; x < Board.WIDTH; x++) {
            for (int y = 0; y < Board.HEIGHT; y++) {
                move.getLocation().setX(x);
                move.getLocation().setY(y);
                if (board.isValidSubsequentMove(move)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    // Copy of method from board class altered to return the number of plays with a gamepiece
    private int playerCanPlayGamepiece(Board board, Gamepiece piece) {

        int noOfOrientations = 0;
        Gamepiece clonedPiece = new Gamepiece(piece);
        for (int i = 0; i < 4; i++) {
            if (playerCanPlayInOrientation(board, clonedPiece)) {
                noOfOrientations++;
            }
            clonedPiece.rotateRight();
        }
        clonedPiece.flipAlongY();
        for (int i = 0; i < 4; i++) {
            if (playerCanPlayInOrientation(board, clonedPiece)) {
                noOfOrientations++;
            }
            clonedPiece.rotateRight();
        }
        return noOfOrientations;
    }
    // Method to give the number of possible moves
    public int noOfPossibleMoves(Board board) {
        int noOfMoves = 0;
        Collection<Gamepiece> gamepieces = getGamepieceSet().getPieces().values();
        for (Gamepiece gamepiece : gamepieces) {
            noOfMoves += playerCanPlayGamepiece(board, gamepiece);
        }
        return noOfMoves;
    }
    // Method to calculate the distance from a gamepiece to a location on the board
    private double distanceFromGamepieceToLocation(Move move, Location location) {
        double smallestDistance = Double.MAX_VALUE;
        for (Location loc : move.getGamepiece().getLocations()) {
            double distance = Math.sqrt(Math.pow((loc.getX() + move.getLocation().getX()) - location.getX(), 2)
                    + Math.pow((loc.getY() + move.getLocation().getY()) - location.getY(), 2));
            if (distance < smallestDistance) {
                smallestDistance = distance;
            }
        }
        return smallestDistance;
    }
    // Method to get the number of moves after making a move
    private int getSubsequentMovesCreated(Board board, Move move) {
        Board copyboard1 = new Board(board);
        copyboard1.makeMove(move);
        return noOfPossibleMoves(copyboard1);
    }
    // Make move method
    @Override
    public Move makeMove(Board board) {
        Move bestMove;
        if (isFirstMove) {
            isFirstMove = false;
            // If it is the first move randomly choose a number between 0 and 5 inclusive
            int rand = new Random().nextInt(6);
            // Depending on the random number play a piece with rubiks length 6
            switch (rand) {
                case 0:
                    bestMove = new Move(this, new Gamepiece(getGamepieceSet().get("I5")),
                            "I5", new Location(Board.startLocations[getPlayerNo()]));
                    break;
                case 1:
                    bestMove = new Move(this, new Gamepiece(getGamepieceSet().get("L5")),
                            "L5", new Location(Board.startLocations[getPlayerNo()]));
                    break;
                case 2:
                    bestMove = new Move(this, new Gamepiece(getGamepieceSet().get("V5")),
                            "V5", new Location(Board.startLocations[getPlayerNo()]));
                    break;
                case 3:
                    bestMove = new Move(this, new Gamepiece(getGamepieceSet().get("N")),
                            "N", new Location(Board.startLocations[getPlayerNo()]));
                    break;
                case 4:
                    bestMove = new Move(this, new Gamepiece(getGamepieceSet().get("Z5")),
                            "Z5", new Location(Board.startLocations[getPlayerNo()]));
                    break;
                case 5:
                    bestMove = new Move(this, new Gamepiece(getGamepieceSet().get("W")),
                            "W", new Location(Board.startLocations[getPlayerNo()]));
                    break;
                default:
                    return null;
            }
        } else {
            // Get all the possible moves the bot has
            ArrayList<Move> moves = getPlayerMoves(this, board);
            bestMove = moves.get(0);
            double moveScore, maxScore = 0;
            // For every move the bot has, calculate the features and produce a move score
            for (Move m : moves) {
                int f1 = getSubsequentMovesCreated(board, m);
                int f2 = getRubiksLength(m.getGamepieceName());
                double f3 = distanceFromGamepieceToLocation(m, new Location(7, 7));
                int f4 = 0;
                if (m.getGamepieceName().equals("I1")) {
                    f4 = 1;
                }
                // Calculating the move score using the weights of the features
                moveScore = (f1 * 0.5) + (f2 * 6) + ((f3 * -0.05) / turnNo) + (f4 * turnNo);
                // If the move score exceeds the max score change the best move to be that move
                if (moveScore > maxScore) {
                    maxScore = moveScore;
                    bestMove = m;
                }
            }

        }
        turnNo++;
        // Return the best move found
        return bestMove;
    }


}
