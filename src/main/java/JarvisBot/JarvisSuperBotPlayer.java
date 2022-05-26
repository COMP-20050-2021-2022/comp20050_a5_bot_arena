package JarvisBot;

/*
BLOKUS DUO
TEAM JARVIS - Assignment 5
Brian Monoranu : 20318381
Oskar Jasiewicz : 20377563
Nikita Dmitriev: 20204397
*/

import SimpleBot.SimpleBotPlayer;
import model.*;
import ui.UI;

import java.util.ArrayList;
import java.util.Collection;

public class JarvisSuperBotPlayer extends SimpleBotPlayer {

    private final double[] weights =
            {5.8,       // for size of the piece
            15.9,        // for amount of my possible moves
            -20.6};      // for amount of opponents moves

    public JarvisSuperBotPlayer(int playerNo) {
        super(playerNo);
        isFirstMove = playerNo == 0;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public double[] getWeights() {
        return weights;
    }

    @Override
    public void setUI(UI ui) {
    }

    // This bot is VERY simple-minded it returns the first move it finds.
    @Override
    public Move makeMove(Board board) {

        ArrayList<Move> allMoves = getPlayerMoves(this, board);

        return miniMax(allMoves);
    }

    /* Here we get the best piece for the bot */
    private Move miniMax(ArrayList<Move> allMoves){

        Move bestMove = null;
        double bestMoveValue = -999999;

        for (Move move:
                allMoves) {

            // move analysis start here
            double myValue = 0;

            Board tempBoard = new Board(board);

            tempBoard.makeMove(move);

            ArrayList<Move> currentPotentialMoves = getPlayerMoves(this, tempBoard);
            ArrayList<Move> opponentMoves = getPlayerMoves(this.opponent, tempBoard);

            myValue += evalSize(move);                                           // consider the size of the piece
            myValue += currentPotentialMoves.size() * weights[1];               // consider amount of available moves then
            myValue += opponentMoves.size() * weights[2];                       // consider amount of available moves opponent then has
            // move analysis end here

            if (myValue > bestMoveValue) {
                bestMoveValue = myValue;
                bestMove = move;
            }

        }
        return bestMove;
    }

    private double evalSize(Move move){
        return move.getGamepiece().getLocations().length * weights[0];
    }

    public ArrayList<Move> getPlayerMoves(Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<>();
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
        ArrayList<Move> moves = new ArrayList<>();
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
        ArrayList<Move> moves = new ArrayList<>();
        Move move = new Move(player,piece, gamepieceName,new Location(0,0));

        Location myStarter = Board.startLocations[getPlayerNo()];
        boolean isFirst = !board.isOccupied(myStarter.getX(), myStarter.getY());

        for (int x = 0; x < Board.WIDTH; x++) {
            for (int y = 0; y < Board.HEIGHT; y++) {
                move.getLocation().setX(x);
                move.getLocation().setY(y);
                if (isFirst) {
                    if (board.isValidFirstMove(move))
                        moves.add(new Move(move));
                } else {
                    if (board.isValidSubsequentMove(move))
                        moves.add(new Move(move));
                }
            }
        }
        return moves;
    }
}
