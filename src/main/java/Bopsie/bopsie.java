package Bopsie;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Move;
import model.Player;

import java.util.ArrayList;
import java.util.Comparator;

public class bopsie extends SimpleBotPlayer {
    boolean isFirstMove; // boolean variable for players first move

    public bopsie(int playerNo) {
        super(playerNo); // referencing superclass object
        isFirstMove = true; //setting player first move to true
    }


    @Override
    public Move makeMove(Board board){
        if(isFirstMove){ // if it is the first move
            isFirstMove = false; // set isFirstMove to false
            return super.makeMove(board); // returns the first move it finds
        }
        return bestMove(board); // if it is not first move play the best move on the board
    }

    private Move bestMove(Board board) {
        Move move = null;
        Board testBoard = new Board(board);
        ArrayList<Move> moves = getPlayerMoves(this, testBoard);
        int value = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        moves.sort(Comparator.comparingInt(o -> o.getGamepiece().getLocations().length));
        int j = moves.size()-1;
        for(int i = 0; i < 2; i++){
            testBoard.makeMove(moves.get(j));
            int newValue = minimax(testBoard, 0, alpha, beta, false);

            if(newValue > value){
                value = newValue;
                move = new Move(moves.get(j));
            }

            alpha = value;
            if(beta <= alpha)
                break;

            testBoard = new Board(board);
            j--;
        }

        return move;
    }int minimax(Board board, int depth, int alpha, int beta, boolean max){
        // base case
        if(depth == 2 || !board.playerHasMoves(this)) {
            return heuristic(board, this);
        }
        Board testBoard = new Board(board);
        int value = 0;
        //Finding the maximum reachable value if the current move is a maximizer
        if(max){
            ArrayList<Move> moves = getPlayerMoves(this, testBoard); // all possible moves
            if(moves.isEmpty()) {
                return value;
            }
            moves.sort(Comparator.comparingInt(sort -> sort.getGamepiece().getLocations().length));
            int j = moves.size()-1;
            value = Integer.MIN_VALUE;
            for(int i=0; i<2; i++){
                testBoard.makeMove(moves.get(j));
                int newValue = minimax(testBoard, depth+1, alpha, beta, false);
                testBoard = new Board(board);
                if(newValue > value){
                    value = newValue;
                }
                alpha = Math.max(value, alpha);
                if( beta <= alpha) {
                    break;
                }j--;
            }
        } else { //Finding the minimum reachable value if the current move is a minimizer
            ArrayList<Move> moves = getPlayerMoves(this.opponent, board); // all possible moves
            if(moves.isEmpty()) {
                return value;
            }
            moves.sort(Comparator.comparingInt(sort -> sort.getGamepiece().getLocations().length));
            int j = moves.size()-1;
            value = Integer.MAX_VALUE;
            for(int i=0; i<2; i++){
                testBoard.makeMove(moves.get(j));
                int newValue = minimax(testBoard, depth+1,alpha, beta, true);
                testBoard = new Board(board);
                if(newValue < value){
                    value = newValue;
                }
                alpha = Math.min(alpha, value);
                if( beta <= alpha) {
                    break;
                }j--;
            }
        }
        return value;

    }

    //we take the "score" of a player to be the number of playable corners to the player in its
    //current state - this is the heuristic
    int heuristic(Board board, Player p){
        int corners = 0;
        for(int i=0; i<Board.HEIGHT; i++){
            for(int j =0; j<Board.WIDTH; j++){
                if(!board.isOccupied(i, j)){
                    //player calculates all the corners of itself
                    if(corner(board, i, j, p))
                        corners++;
                }
            }
        }return corners;
    }

    private boolean corner(Board board, int i, int j, Player p) {
        //also checks if no sides are touching
        int row, col;
        for(row = -1; row < 2; row+=2){
            for (col = -1; col < 2; col+=2){
                if( (i+row < 0) ||(j+col) < 0 || (col + j) >= Board.HEIGHT || (row + i) >= Board.WIDTH ) {
                    break;
                }
                // checking if the there's a move available for the player
                //  and returns true if there is it return false if its touching sides
                if(board.getOccupyingPlayer(i + row, j) == p.getPlayerNo()) {
                    return false;
                }
                if(board.getOccupyingPlayer(i, j + col) == p.getPlayerNo()) {
                    return false;
                }if(board.getOccupyingPlayer(i+row, j+col) == p.getPlayerNo()) {
                    return true;
                }
            }
        }return false;
    }
}