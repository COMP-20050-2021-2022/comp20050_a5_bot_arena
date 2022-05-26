package p;

import SimpleBot.SimpleBotPlayer;
import com.badlogic.gdx.Game;
import model.*;
import ui.UI;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.abs;

public class pBotPlayer extends SimpleBotPlayer {

    protected Player opponent;
    protected Board board;
    protected boolean isFirstMove;


    public pBotPlayer(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    public void setBoard(Board board) {
        this.board = board;
    };

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    @Override
    public void setUI(UI ui) {
    }

    // This bot is VERY simple-minded it returns the first move it finds.
    @Override
    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;
            // Play gamepiece "F" at the starting location in default orientation
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("W")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        } else {

            // Play the first found valid move.
            // N.B.: The game control checks that we have at least one valid move before calling this method
            return getBestMove(this, board);
        }
    }

    public Move getBestMove(Player player, Board board) {
        ArrayList<Move> moves = getPlayerMoves(this, board);
        ArrayList<Integer> evals = evaluateAllMoves(this, board, moves);
        int maxIndex = 0;
        for (int eval : evals) {
            if (eval > evals.get(maxIndex)) {
                maxIndex = evals.indexOf(eval);
            }
        }
        System.out.println(moves.size() + " | " + maxIndex);

        // N.B.: The game control checks that we have at least one valid move before calling this method
        return moves.get(maxIndex);
    }

    // evaluate all moves
    public ArrayList<Integer> evaluateAllMoves(Player player, Board board, ArrayList<Move> moves) {
        ArrayList<Integer> evals = new ArrayList<>();
        for (Move move : moves) {
            int evaluation = evaluateMove(player, move);
            evals.add(evaluation);
        }
        return evals;
    }

    // evaluate a specific move
    public int evaluateMove(Player player, Move move) {
        int score = Search(this, board, 0, -10000, 10000);
        return score;
    }


    private int getScore(Move move) {
        int score = 0;
                //beginning stages of game
                // bigger blocks = higher priority
                if (move.getGamepiece().getLocations().length >= 10) {
                    score += 5;
                } else if (move.getGamepiece().getLocations().length >= 8) {
                    score += 4;
                }
                //moving towards the center of the board  = high priority
                if ((abs(8 - move.getLocation().getX()) <= 2) && (abs(8 - move.getLocation().getY()) <= 2)) {
                    score += 5;
                } else if ((abs(8 - move.getLocation().getX()) <= 4) && (abs(8 - move.getLocation().getY()) <= 4)) {
                    score += 3;
                }else

                //moving towards the center right or left of the board for growth/block high priority
                if (((abs(13 - move.getLocation().getX()) <= 2) && (abs(8 - move.getLocation().getY()) <= 2)) ||
                        ((abs(3 - move.getLocation().getX()) <= 2) && (abs(8 - move.getLocation().getY()) <= 2))){
                    score += 4;
                }else
                //moving towards edges of the board for growth/block highest priority
                if (((abs(13 - move.getLocation().getX()) <= 2) && (abs(13 - move.getLocation().getY()) <= 2)) ||
                        ((abs(3 - move.getLocation().getX()) <= 2) && (abs(3 - move.getLocation().getY()) <= 2))){
                    score += 4;
                }
        return score;
    }


    public int Search(Player player, Board board, int depth, int alpha, int beta) {
        ArrayList<Move> moves = getPlayerMoves(player, board);
        if (moves.size() == 0) {
            return 0;
        }
        if (depth == 0) {
            int bestEval = 0;
            for (Move move : moves) {
                int eval = getScore(move);
                if (eval > bestEval) {
                    bestEval = eval;
                 }
            }
            return bestEval;
        }
        for (Move move : moves){
            System.out.println("HERE");
           Board newBoard = new Board(board);
           newBoard.makeMove(move);
           int eval = Search(opponent, newBoard, depth - 1, -beta, -alpha) * -1;
           if (eval >= beta) {
               return beta;
           }
           alpha = Math.max(alpha, eval);
        }
        return alpha;
    }

    public int countBlocks(Player player, Board board) {
        int score = 0;
        int spaces = Board.WIDTH * Board.HEIGHT;
        for (int i = 0; i < spaces; i++) {
            int x = i % Board.WIDTH;
            int y = i / Board.HEIGHT;
            if (board.isOccupied(x, y)) {
                if (board.getOccupyingPlayer(x, y) == player.getPlayerNo()) {
                    score++;
                }
            }
        }
        return score;
    }

    public ArrayList<Move> getPlayerMoves(Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
        Collection<String> gamepieceNames = player.getGamepieceSet().getPieces().keySet();
        for (String gamepieceName : gamepieceNames) {
            moves.addAll(
                    getMovesWithGivenGamepiece(
                            player.getGamepieceSet().getPieces().get(gamepieceName),
                            gamepieceName,
                            player,
                            board)
            );
        }
        return moves;
    }

    public ArrayList<Move> getMovesWithGivenGamepiece(Gamepiece gamepiece, String gamepieceName, Player player, Board board){
        ArrayList<Move> moves = new ArrayList<Move>();
        Gamepiece clonedPiece = new Gamepiece(gamepiece);
        for (int i = 0; i < 4 ; i++) {
            moves.addAll(getMovesWithGivenOrientation(clonedPiece,gamepieceName,player,board));
            clonedPiece.rotateRight();
        }
        clonedPiece.flipAlongY();
        for (int i = 0; i < 4 ; i++) {
            moves.addAll(getMovesWithGivenOrientation(clonedPiece,gamepieceName,player,board));
            clonedPiece.rotateRight();
        }
        return moves;
    }

    public ArrayList<Move> getMovesWithGivenOrientation(Gamepiece piece, String gamepieceName, Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
        Move move = new Move(player,piece, gamepieceName,new Location(0,0));
        for (int x = 0; x < board.WIDTH; x++) {
            for (int y = 0; y < board.HEIGHT; y++) {
                move.getLocation().setX(x);
                move.getLocation().setY(y);
                if (board.isValidSubsequentMove(move)) {
                    moves.add(new Move(move));
                }
            }
        }
        return moves;
    }

}
