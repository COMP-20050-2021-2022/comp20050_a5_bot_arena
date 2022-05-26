// Team Random12
// Adam Goodwin 18322006

package Random12;

import SimpleBot.SimpleBotPlayer;
import model.*;
import ui.UI;

import java.util.ArrayList;
import java.util.Collection;


public class AlphaBetaBot extends SimpleBotPlayer {

    protected Player opponent;
    protected Board board;
    protected boolean isFirstMove;
    int DEPTH = 2;
    public ArrayList<String> moves = new ArrayList<String>();
    public int totalPosEvald = 0;
    public int totalPosMoves = 0;
    public int turn; //increments at start of turn, not end


    public AlphaBetaBot(int playerNo) {
        super(playerNo);
        isFirstMove = true;
        this.turn = 0;
    }




    public void setBoard(Board board) {
        this.board = board;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    @Override
    public void setUI(UI ui) {
    }

    @Override
    public Move makeMove(Board board) {
        this.turn++;
        System.out.println("Turn: "+this.turn);
        if (isFirstMove) {
            if (this.getPlayerNo() == 1){
                isFirstMove = false;
                // Play gamepiece "F" at the starting location in default orientation
                Gamepiece gp = new Gamepiece(getGamepieceSet().get("F"));
                gp.rotateRight();
                this.setLastPlayedPiece("F");
                return new Move(
                        this,
                        gp,
                        "F",
                        new Location(Board.startLocations[getPlayerNo()].getX() - 1, Board.startLocations[getPlayerNo()].getY() + 1)
                );
            } else{
                isFirstMove = false;
                // Play gamepiece "F" at the starting location in default orientation
                Gamepiece gp = new Gamepiece(getGamepieceSet().get("F"));
                gp.rotateRight();
                gp.rotateRight();
                gp.rotateRight();
                this.setLastPlayedPiece("F");
                return new Move(
                        this,
                        gp,
                        "F",
                        new Location(Board.startLocations[getPlayerNo()].getX() + 1, Board.startLocations[getPlayerNo()].getY() - 1)
                );

            }
        } else {
            // After first move, searches for the best move and returns it
            Search s = new Search(new Position(this.board,this, this.opponent, true), this.turn);
            float savingsRatio = (float)s.posEvald / (float)s.totalMoves;
            System.out.println("Checked " +s.posEvald +" positions from " + s.totalMoves + " possible options.\n" +
                    "Ratio of " + savingsRatio + ". Speed of " + (int) s.speed + " positions evaluated per sec");
            System.out.println("Time: " + s.runningTime + " seconds");
            //System.out.println("Best Move: " + s.bestMove.getMove().getGamepieceName() + ". \n" +
            //        "Location: " + s.bestMove.getMove().getLocation().toString());
            moves.add(s.bestMove.getMove().getGamepieceName());
            totalPosMoves += s.totalMoves;
            totalPosEvald += s.posEvald;
            this.setLastPlayedPiece(s.bestMove.getMove().getGamepieceName());
            return s.bestMove.getMove();
        }

    }


    public void add(String gamepieceName, Gamepiece gamepiece) {
        getGamepieceSet().getPieces().put(gamepieceName, gamepiece);
    }

    public ArrayList<Move> getPlayerMoves( Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
        Collection<String> gamepieceNames = this.getGamepieceSet().getPieces().keySet();
        for (String gamepieceName : gamepieceNames) {
            moves.addAll(
                    getMovesWithGivenGamepiece(
                            this.getGamepieceSet().getPieces().get(gamepieceName),
                            gamepieceName,
                            this,
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
        for (int x = 0; x < Board.WIDTH; x++) {
            for (int y = 0; y < Board.HEIGHT; y++) {
                move.getLocation().setX(x);
                move.getLocation().setY(y);
                if (board.isValidSubsequentMove(move)) {
                    moves.add(new Move(move));
                }
            }
        }
        return moves;
    }
    public int getNumberOfMovingSquares(Board board){//Moving squares are ones that link new gamepieces to those already placed
        int numMovingSquares = 0;
        getNumMovesWithGivenOrientation( new Gamepiece(new int[]{0, 0},getPlayerNo()), "I1", this, board, numMovingSquares);
        return numMovingSquares;
    }

    public void getNumMovesWithGivenGamepiece(Gamepiece gamepiece, String gamepieceName, Player player, Board board, int numMovingSquares){
        Gamepiece clonedPiece = new Gamepiece(gamepiece);
        getNumMovesWithGivenOrientation(clonedPiece,gamepieceName,player,board, numMovingSquares);
    }

    public void getNumMovesWithGivenOrientation(Gamepiece piece, String gamepieceName, Player player, Board board, int numMovingSquares) {
        Move move = new Move(player,piece, gamepieceName,new Location(0,0));
        for (int x = 0; x < Board.WIDTH; x++) {
            for (int y = 0; y < Board.HEIGHT; y++) {
                move.getLocation().setX(x);
                move.getLocation().setY(y);
                if (board.isValidSubsequentMove(move)) {
                    numMovingSquares++;
                }
            }
        }
    }







}
