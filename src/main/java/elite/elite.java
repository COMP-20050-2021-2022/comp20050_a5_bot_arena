/*Group: elite
Student numbers: 20332323,20450942,20206422*/

package elite;

import SimpleBot.*;
import model.*;

import java.util.ArrayList;

public class elite extends SimpleBotPlayer {
    protected Player opponent;
    protected Board board;
    protected boolean isFirstMove;

    public elite(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    @Override
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    //This is the method we'll be changing
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
            // Play the first found valid move.
            // N.B.: The game control checks that we have at least one valid move before calling this method
            //return getPlayerMoves(this, board).get(0);

            // We need to put our strategies here
            //Create an array list of all the possible current player moves for reference
            ArrayList<Move> moves = getPlayerMoves(this, board);
            return stratOne(board, moves); //call strategy 1
        }
    }

    public Move stratTwo(Board board, ArrayList<Integer> list, ArrayList<Move> movelist) {
        Integer MaxScore = 0;
        Move move = null;
        for (int i = 0; i < list.size(); i++) {
            // get the length of the possible move and save it in the pieceLength.
            int pieceLength = movelist.get(list.get(i)).getGamepiece().getLocations().length;
            //Compare the MaxScore and pieceLength and whichever is greater will be the pieceLength
            if (Math.max(MaxScore, pieceLength) == pieceLength) {
                move = movelist.get(list.get(i));
                MaxScore = pieceLength;
            }
        }
        return move;
    }

    public Move stratOne(Board board, ArrayList<Move> list){
        ArrayList<Integer> frequency = new ArrayList<>();

        for (Move move : list) {
            int count = 0;
            //Get the possible points
            int pointX = move.getLocation().getX();
            int pointY = move.getLocation().getY();

            Location[] locationBlock = move.getGamepiece().getLocations();

            for (Location location : locationBlock) {
                int boardX = location.getX();
                int boardY = location.getY();

                boardX += pointX;
                boardY += pointY;

                count += checkForOccurences(board, boardX, boardY);

            }
            frequency.add(count);

        }

        int max = frequency.get(0);
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 1; i< frequency.size();i++){
            if(max< frequency.get(i)){
                max = frequency.get(i); //If the frequency at the current index is greater than update the max
                result.clear(); //Clear the arraylist
                result.add(i); //Store the index
            }
            if(max==frequency.get(i)){
                result.add(i); //If the frequency at the current index is equal then store it in the index
            }
        }
        //System.out.println("THE FREQUENCY"+frequency.get(index));
        //System.out.println("INDEX: "+index);
        return (stratTwo(board, result, list)); //Call strat 2 on the array of highest frequencies
    }

    public int checkForOccurences(Board board, int x, int y){
        int count = 0;
        if(x+1 < 14){
            if(board.isOccupied(x+1, y)){
                int result = board.getOccupyingPlayer(x+1, y);

                if(result== opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        if(x-1> 0){
            if(board.isOccupied(x-1, y)){
                int result = board.getOccupyingPlayer(x-1, y);
                if(result == opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        if(y+1 < 14){
            if(board.isOccupied(x, y+1)){
                int result = board.getOccupyingPlayer(x, y+1);
                if(result == opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        if(y-1>0){
            if(board.isOccupied(x, y-1)){
                int result = board.getOccupyingPlayer(x, y-1);
                if(result == opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        if(x+1<14 && y+1<14){
            if(board.isOccupied(x+1, y+1)){
                int result = board.getOccupyingPlayer(x+1, y+1);
                if(result == opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        if(x+1 < 14 && y-1>0){
            if(board.isOccupied(x+1, y-1)){
                int result = board.getOccupyingPlayer(x+1, y-1);
                if(result == opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        if(x-1>0 && y-1>0){
            if(board.isOccupied(x-1, y-1)){
                int result = board.getOccupyingPlayer(x-1, y-1);
                if(result == opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        if(x-1>0 && y+1 <14){
            if(board.isOccupied(x-1, y+1)){
                int result = board.getOccupyingPlayer(x-1, y+1);
                if(result == opponent.getPlayerNo()){
                    count++;
                }
            }
        }

        return count;
    }


}
