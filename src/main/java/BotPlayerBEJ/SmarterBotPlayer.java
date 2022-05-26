package BotPlayerBEJ;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * --- Team: BEJ ---
 * --- Game Winning Bot ---
 */
public class SmarterBotPlayer extends SimpleBotPlayer {


    public SmarterBotPlayer(int playerNo) {
        super(playerNo);
    }

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
            return bestMove(board);
        }
    }

    //method which returns the best move from 20 possible boards, looking 5 moves in advance
    private Move bestMove(Board board) {

        Random random = new Random();
        ArrayList<Board> boards = new ArrayList<>();
        ArrayList<Move> firstMoves = new ArrayList<>();

        //make 20 possible boards
        for (int i = 0; i < 20; i++) {
            Board possibleBoard = new Board(board);
            int j = 0;
            //loop 5 times unless there are no possible moves remaining
            while ((possibleBoard.playerHasMoves(this) || possibleBoard.playerHasMoves(this.opponent)) && (j < 5)) {

                ArrayList<Move> moves = getPlayerMoves(this, possibleBoard);
                ArrayList<Move> opponentMoves = getPlayerMoves(this.opponent, possibleBoard);
                int maxGamepieceLength = 0;

                //record the max length move (number of squares)
                Move maxLengthMove = moves.get(random.nextInt(moves.size()));
                for (Move move : moves) {
                    if (move.getGamepiece().getLocations().length > maxGamepieceLength) {
                        maxLengthMove = move;
                        maxGamepieceLength = move.getGamepiece().getLocations().length;
                    }
                }

                //record the first move played on this board
                if (j == 0) {
                    firstMoves.add(maxLengthMove);
                }
                possibleBoard.makeMove(maxLengthMove);

                //choose a random gamepiece to be played for the opponent
                if (opponentMoves.size() > 0) {
                    possibleBoard.makeMove(opponentMoves.get(random.nextInt(opponentMoves.size())));
                }
                j++;
            }
            //add board to arraylist
            boards.add(possibleBoard);
        }

        //record max evaluation from possible boards
        double maxEval = 0;
        int indexOfBestMove = 0;
        Move bestMove = firstMoves.get(indexOfBestMove);

        for (Board possibleBoard : boards) {
            if (evaluate(possibleBoard, this.getGamepieceSet(), this.opponent.getGamepieceSet()) > maxEval) {
                //record max evaluation and corresponding first move for that board
                maxEval = evaluate(possibleBoard, this.getGamepieceSet(), this.opponent.getGamepieceSet());
                bestMove = firstMoves.get(indexOfBestMove);
            }
            indexOfBestMove++;
        }
        return bestMove;
    }

    //method to evaluate the probability of the bot winning given a board state
    private Double evaluate(Board board, GamepieceSet pieces, GamepieceSet opponentPieces) {
        int numberOfSquares = getNumberOfSquares(board, this.getPlayerNo());
        int numberOfSquaresOpponent = getNumberOfSquares(board, opponent.getPlayerNo());
        int numberOfPieces = pieces.getPieces().size();
        int numberOfPiecesOpponent = opponentPieces.getPieces().size();

        //ratios converted to a %
        double ratioOfSquares = ((double) numberOfSquares / numberOfSquaresOpponent) / 100;
        double ratioOfPieces = ((double) numberOfPieces / numberOfPiecesOpponent) / 100;

        //ratio * ratio of occupied/unoccupied tiles + ratio of remaining gamepieces
        //the ratio of occupied/unoccupied tiles is to increase the probability of the bot winning as it reaches the end game
        return (ratioOfSquares * ((numberOfSquares + numberOfSquaresOpponent) / 196)) + ratioOfPieces;
    }

    //method to count the number of tiles occupied on the board of a given player
    private int getNumberOfSquares(Board board, int playerNo) {
        int numberOfSquares = 0;
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (board.isOccupied(x, y)) {
                    if (board.getOccupyingPlayer(x, y) == playerNo) {
                        numberOfSquares++;
                    }
                }
            }
        }
        return numberOfSquares;
    }
}
