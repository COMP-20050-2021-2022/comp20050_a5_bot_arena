package Random4;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Random4Player  extends SimpleBotPlayer {

    protected Player opponent;
    protected Board board;
    protected boolean isFirstMove;

    public Random4Player(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    @Override
    public void setBoard(Board board) {
        this.board = board;
    };

    @Override
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    // This bot is VERY simple-minded it returns the first move it finds.
    // TODO: Implement good algo here
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
            ArrayList<Move> allMoves = getPlayerMoves(this, board);
            double score = Double.NEGATIVE_INFINITY;
            Move bestMove = allMoves.get(0);
            //Get smaller list of random possible moves to eval so algo doesn't run forever
            ArrayList<Move> moves = getRandomMoves(allMoves.size() / 20, allMoves);

            for (Move m : moves) {
                double s = minimax(getFollowingMoveOptions(this, m, board), board, Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY, true, 2);
                if (s > score) {
                    bestMove = m;
                    score = s;
                }
            }
            return bestMove;
        }
    }

    private ArrayList<Move> getRandomMoves(int numberOfElements, ArrayList<Move> allMoves) {
        Random rand = new Random();
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < numberOfElements; i++) {
            int randomIndex = rand.nextInt(allMoves.size());
            moves.add(allMoves.get(randomIndex));
            allMoves.remove(randomIndex);
        }
        return moves;
    }

    private double minimax(ArrayList<Move> moves, Board currentBoard, double alpha, double beta, boolean maximizingPlayer, int maxDepth) {
        if (!currentBoard.playerHasMoves(this) || maxDepth <= 0) {
            return this.playerScore();
        }
        if (maximizingPlayer) {      // for Maximizer Player
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move m : moves) {
                Board newBoard = testPlacePiece(this, m, currentBoard);
                ArrayList<Move> nextMoves = getPlayerMoves(this, newBoard);
                ArrayList<Move> randomMoves = getRandomMoves(nextMoves.size() / 20, nextMoves);
                double eval = minimax(randomMoves, newBoard, alpha, beta, false, maxDepth - 1 );
                maxEval = max(maxEval, eval);
                alpha = max(alpha, maxEval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Move m : moves) {
                Board newBoard = testPlacePiece(this, m, currentBoard);
                ArrayList<Move> nextMoves = getPlayerMoves(this, newBoard);
                ArrayList<Move> randomMoves = getRandomMoves(nextMoves.size() / 20, nextMoves);
                double eval = minimax(randomMoves, newBoard, alpha, beta, true, maxDepth - 1);
                minEval = min(minEval, eval);
                beta = min(beta, minEval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    public ArrayList<Move> getFollowingMoveOptions(Player player, Move move, Board board) {
        Board newBoard = testPlacePiece(player, move, board);
        return getPlayerMoves(player, newBoard);
    }

    private Board testPlacePiece(Player player, Move move, Board board) {
        Gamepiece gamepiece = move.getGamepiece();
        int x = move.getLocation().getX();
        int y = move.getLocation().getY();

       Board newBoard = new Board(board);
       /* boolean occupied[][] = newBoard.getOccupied();
        int occupyingPlayer[][] = newBoard.getOccupyingPlayer();;

        for (Location l : gamepiece.locations) {
            occupied[l.getX() + x][l.getY() + y] = true;
            occupyingPlayer[l.getX() + x][l.getY() + y] = player.getPlayerNo();
        }
        newBoard.setOccupied(occupied);
        newBoard.setOccupyingPlayer(occupyingPlayer);
        return newBoard;
        */

        newBoard.makeMove(move);

        return newBoard;
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


