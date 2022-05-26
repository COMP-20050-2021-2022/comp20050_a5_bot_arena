package Umbra;
//Team Umbra
import SimpleBot.SimpleBotPlayer;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class UmbraBotPlayer extends SimpleBotPlayer {

    public UmbraBotPlayer(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    @Override
    public Move makeMove(Board board) {

        ArrayList<Move> moves = getPlayerMoves(this, board);
        if (isFirstMove) {
            isFirstMove = false;
            // Play gamepiece "F" at the starting location in default orientation
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        }
        else {

            return findBestMove(moves);//getPlayerMoves(this, board).get(0);

        }

        //Generating positions by applying moves
        //  to a copy of the current board:
        //Board possibleBoard = new Board(board);
        //possibleBoard.makeMove(move);

        //  super.makeMove(board);
    }

    public Move findBestMove(ArrayList<Move> moves){

        if((this.getGamepieceSet().getPieces().size() > 9) || this.playerScore() < opponent.playerScore()-5)

            return build(moves);

        return block(moves);
    }

    /**
     * mostPossibleMoves is the move that gives the bot the most possible moves.
     * we make a move on a possibleBoard and check how it affects the bot's possible moves .
     * mostGrowth stores the int of the most possible moves.
     * If the possible move gives the largest amount of growth, it becomes the mostPossibleMoves
     * @param moves - Possible moves for current board
     * @return mostPossibleMoves
     */


    public Move build(ArrayList<Move> moves){
        Board possibleBoard;
        int mostPossibleMoves = 0;
        Move mostGrowth = moves.get(1);
        for(int i = 0; i < moves.size(); i++) {
            int bestMoveScore = mostGrowth.getGamepiece().getLocations().length;
            possibleBoard = new Board(board);
            Move move = moves.get(i);
            int newMoveScore = move.getGamepiece().getLocations().length;
            possibleBoard.makeMove(move);
            int possibleBoardMoves = getPlayerMoves(this, possibleBoard).size();
            if((possibleBoardMoves > mostPossibleMoves) || (possibleBoardMoves == mostPossibleMoves && bestMoveScore < newMoveScore)){
                mostGrowth = move;
                mostPossibleMoves = possibleBoardMoves;
            }
        }
        return mostGrowth;
    }

    /**
     * leastOppGrowth is the move that gives the opponent the smallest number of possible moves
     * we make a move on a possibleBoard and check how it affects the opponent's possible moves
     * leastPossibleMoves stores the int of the smallest sized opponent growth.
     * If the possible move gives the smallest sized opponent growth, it becomes the leastOppGrowth
     * @param moves - Possible moves for current board
     * @return leastOppGrowth
     */

    public Move block(ArrayList<Move> moves){
        Board possibleBoard;
        int leastPossibleMoves = Integer.MAX_VALUE;

        Move leastOppGrowth = moves.get(0);

        for(int i = 0; i < moves.size(); i++) {
            int bestMoveScore = leastOppGrowth.getGamepiece().getLocations().length;
            possibleBoard = new Board(board);
            Move move = moves.get(i);
            int newMoveScore = move.getGamepiece().getLocations().length;
            possibleBoard.makeMove(move);
            int possibleBoardMoves = getPlayerMoves(opponent, possibleBoard).size();

            if((possibleBoardMoves < leastPossibleMoves) || (possibleBoardMoves == leastPossibleMoves && bestMoveScore < newMoveScore)){    // minimises opponent growth
                leastOppGrowth = move;
                leastPossibleMoves = possibleBoardMoves;
            }
        }
        return leastOppGrowth;
    }
}
