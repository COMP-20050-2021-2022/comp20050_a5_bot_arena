//Team Name: NEPA Student Numbers (Names) –
//20456362 (Patrick Mbanusi) // 20432286 (Evergreen Abagha) // 20312373 (Nadia Aminou)

package NepaBot;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;

public class NepaBot extends SimpleBotPlayer {

    static final int TotalDepth = 2;
    boolean isFirstMove;

    public NepaBot(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    @Override
    public Move makeMove(Board board){

        if(isFirstMove){
            isFirstMove = false;
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("W")),
                    "W",
                    new Location(board.startLocations[getPlayerNo()]));
        }

        return bestMove(board);
    }

    private Move bestMove(Board board) {
        int currentBestValue = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        Move bestMove = null;
        Board tempBoard = new Board(board);


        ArrayList<Move> moves = getPlayerMoves(this, tempBoard);
        ArrayList<Move> biggestMoves = findBiggestMoves(moves);


        //j indexes the biggest piece (back of the array)
        int j = biggestMoves.size() - 1;

        //iterate through the n biggest pieces available
        for(int i = 0; i < 8; i++){

            //if there aren't enough things in the array break
            if(j == 0) {
                break;
            }

            //play a move on temporary board
            tempBoard.makeMove(biggestMoves.get(j));

            //get a value of that board
            int newValue = minMax(tempBoard, 0, alpha, beta, false);

            //if that value is better than the current value we have, take that value and corresponding move
            if(newValue >= currentBestValue){
                currentBestValue = newValue;
                bestMove = new Move(biggestMoves.get(j));
            }

            alpha = Math.max(alpha, currentBestValue);
            
            if(beta <= alpha) {
                break;
            }

            //clear board
            tempBoard = new Board(board);

            //decrement j to index the next biggest piece
            j--;
        }

        //in the end return best move we found
        return bestMove;
    }

    int minMax(Board board, int depth, int alpha, int beta, boolean FindThisPlayersMoves){

        //if the player doesn't have moves or reached max depth of searching
        if(depth == TotalDepth || !board.playerHasMoves(this)){
            return heuristic(board, this) ;
        }

        //make new instance of board to manipulate
        Board tempBoard = new Board(board);

        int currentBestValue = 0;

        if(FindThisPlayersMoves){
            ArrayList<Move> availableMoves = getPlayerMoves(this, board);
            availableMoves = findBiggestMoves(availableMoves);

            if(availableMoves.isEmpty()) {
                return currentBestValue;
            }

            currentBestValue = Integer.MIN_VALUE;

            //iterate through all available moves
            for(Move currentMove: availableMoves){

                //play move on board
                tempBoard.makeMove(currentMove);

                //get value of that move
                int newValue = minMax(tempBoard, depth+1, alpha, beta, false);

                //clear board
                tempBoard = new Board(board);

                if(newValue > currentBestValue){
                    currentBestValue = newValue;
                }

                alpha = Math.max(currentBestValue, alpha);

                if( beta <= alpha) {
                    break;
                }

            }

        } else {
            //check opponents moves
            ArrayList<Move> moves = getPlayerMoves(this.opponent, board);

            moves = findBiggestMoves(moves);

            if(moves.isEmpty()) {
                return currentBestValue;
            }

            currentBestValue = Integer.MAX_VALUE;

            //iterate through all available moves
            for(Move currentMove: moves){

                //play move on that board
                tempBoard.makeMove(currentMove);

                int newValue = minMax(tempBoard, depth+1,alpha, beta, true);

                //clear board
                tempBoard = new Board(board);

                if(newValue < currentBestValue){
                    currentBestValue = newValue;
                }

                alpha = Math.min(alpha, currentBestValue);

                if( beta <= alpha) {
                    break;
                }
            }
        }
        return currentBestValue;

    }

    // Heuristic method that evaluates the "score" of a player in a certain state
    int heuristic(Board board, Player p){

        //make a board with points corresponding to position of where pieces are played on
        int c7 = 7;
        int c6 = 6;
        int c5 = 5;
        int c4 = 4;
        int c3 = 3;
        int c2 = 2;
        int c1 = 1;

        int[][] scoreBoard1 = {
                {c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7},
                {c7, c6, c6, c6, c6, c6, c6, c6, c6, c6, c6, c6, c6, c7},
                {c7, c6, c5, c5, c5, c5, c5, c5, c5, c5, c5, c5, c6, c7},
                {c7, c6, c5, c4, c4, c4, c4, c4, c4, c4, c4, c5, c6, c7},
                {c7, c6, c5, c4, c3, c3, c3, c3, c3, c3, c4, c5, c6, c7},
                {c7, c6, c5, c4, c3, c2, c2, c2, c2, c3, c4, c5, c6, c7},
                {c7, c6, c5, c4, c3, c2, c1, c1, c2, c3, c4, c5, c6, c7},
                {c7, c6, c5, c4, c3, c2, c1, c1, c2, c3, c4, c5, c6, c7},
                {c7, c6, c5, c4, c3, c2, c2, c2, c2, c3, c4, c5, c6, c7},
                {c7, c6, c5, c4, c3, c3, c3, c3, c3, c3, c4, c5, c6, c7},
                {c7, c6, c5, c4, c4, c4, c4, c4, c4, c4, c4, c5, c6, c7},
                {c7, c6, c5, c5, c5, c5, c5, c5, c5, c5, c5, c5, c6, c7},
                {c7, c6, c6, c6, c6, c6, c6, c6, c6, c6, c6, c6, c6, c7},
                {c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7, c7},
        };

        int score = 0;

        //Give points to pieces closest to the middle
        for(int i = 0; i < Board.WIDTH; i++){
            for(int j = 0; j < Board.HEIGHT; j++){
                if(board.getOccupyingPlayer(i, j) == this.getPlayerNo()) {
                    // if the piece is on this ring of the score board add 10 to it
                    //because it is the most valuable position to be in
                    if (scoreBoard1[i][j] == c4 || scoreBoard1[i][j] == c5){
                        score = score + 10;
                    } else {
                        score = score + scoreBoard1[i][j];
                    }
                }
            }
        }

        return score;
    }


    public ArrayList<Move> findBiggestMoves (ArrayList<Move> m){

        if (m.isEmpty()){
            return m;
        }

        ArrayList<Move> biggestMoves = new ArrayList<>();

        int max = m.get(0).getGamepiece().getLocations().length;
        biggestMoves.add(m.get(0));
        int current;

        // iterate through all moves get an arraylist of moves that play the biggest pieces
        for (Move move: m) {

            current = move.getGamepiece().getLocations().length;

            if (current > max){
                max = current;
                //clear array and add the biggest move into it
                biggestMoves.clear();
                biggestMoves.add(move);
            }

            //if the piece in this move is equal to the piece size of values already in list add it to array
            if (current == max){
                biggestMoves.add(move);
            }
        }

        return biggestMoves;
    }


}




