/*
Random 2
 */

package Random2;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;

public class Random2Bot extends SimpleBotPlayer {
    int opponentMoveCountWeight = 5;        // range from 0 to 100s
    int pieceWeight = 800;                   // range from 1 to 5
    int distWeight = 500;                    // range from 0 to 7
    int countCornerWeight = 450;             // range from 0 to 10s

    boolean opponentHasMoves = true;

    public Random2Bot(int playerNo) {
        super(playerNo);
    }

    @Override
    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;
            // default first move
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(Board.startLocations[getPlayerNo()]));
        } else {
            return getBestMove(getPlayerMoves(this, board), board);
        }

    }

    /**
     * Calculates the score of a move
     * @param move A possible move
     * @return The score of the move
     */
    private int calculateMoveScore(Move move, Board board) {
        int score = 0;

        // only run if opponent have moves left
        if (opponentHasMoves) {
            score += opponentPossibleMoveCountScorer(move, board);
        }
        score += pieceSize(move);
        score += movePosition(move);
        score += countCorners(move, board, this);
        score += opponentCountCorners(move, board);

        return score;
    }

    /**
     * Calculates the score of every move and return the first move with the highest score
     * @param possibleMoves An ArrayList of all possible moves
     * @return The move with the highest score
     */
    private Move getBestMove(ArrayList<Move> possibleMoves, Board board) {
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = possibleMoves.get(0);

        // loop through all possible moves to find the one with best score
        for (Move move : possibleMoves) {
            int score = calculateMoveScore(move, board);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        //System.out.println("Score from num of opponent moves: " + opponentPossibleMoveCountScorer(bestMove, board) + ", Total score: " + bestScore);

        // adjust weight for next turn
        adjustWeight();

        return bestMove;
    }

    private int movePosition(Move move){

        int distX = Math.abs(move.getLocation().getX() - 7);
        int distY = Math.abs(move.getLocation().getY() - 7);
//if getX = 7 then distX = 0 then score = 7

        int scoreX = 7 - distX;
        int scoreY = 7 - distY;

        return (scoreX + scoreY) * distWeight;
    }

    private int pieceSize(Move move){
        int score = move.getGamepiece().getLocations().length * pieceWeight;

        return score;

    }

    /**
     * Calculates a score based on the number of opponent's possible move after the bot plays this move
     * @return Score
     */
    private int opponentPossibleMoveCountScorer(Move move, Board board) {
        // simulate the move being placed onto a copy of the board
        Board newBoard = new Board(board);
        newBoard.makeMove(move);

        int oppMoveCount = getPlayerMoves(opponent, newBoard).size();

        if (oppMoveCount == 0) {
            // make the score calculation not run this method anymore
            opponentHasMoves = false;
        }

        return - oppMoveCount * opponentMoveCountWeight;
    }

    private int opponentCountCorners(Move move, Board board) {
        return -countCorners(move, board, opponent) * countCornerWeight;
    }

    private int countCorners(Move move, Board board, Player player) {
        Board newBoard = new Board(board);  //use new board as we do not want to make the move

        //count corners before move
        int corners = 0;
        for(int i=0; i < Board.WIDTH; i++) {
            for(int j=0; j < Board.HEIGHT; j++) {
                if(newBoard.boardSquareTouchesAtACorner(i, j, player.getPlayerNo())) {
                    corners++;
                }
            }
        }

        newBoard.makeMove(move);

        //count corners after move
        int newCorners = 0;
        for(int i=0; i < Board.WIDTH; i++) {
            for(int j=0; j < Board.HEIGHT; j++) {
                if(newBoard.boardSquareTouchesAtACorner(i, j, player.getPlayerNo())) {
                    newCorners++;
                }
            }
        }

        //returns the difference in corners for the player
        return (newCorners - corners) * countCornerWeight;
    }

    private void adjustWeight() {
        // prioritize making the opponent run out of moves towards the end of the game
        opponentMoveCountWeight *= 5;
    }
}
