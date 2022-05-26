//Team Name: DEA
package DEA;
import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;

public class BotPlayer extends SimpleBotPlayer { //SubClass extends SimpleBotPlayer Class
    ArrayList<String> remainingPieces = new ArrayList<>();

    //Constructor
    public BotPlayer(int playerNo) { //inherits super() constructor
        super(playerNo);

        //Initialize Array in descending order (The Largest Pieces to Smallest)
        remainingPieces.add("W");
        remainingPieces.add("P");
        remainingPieces.add("U");
        remainingPieces.add("F");
        remainingPieces.add("X");
        remainingPieces.add("Y");
        remainingPieces.add("Z5");
        remainingPieces.add("N");
        remainingPieces.add("V5");
        remainingPieces.add("T5");
        remainingPieces.add("L5");
        remainingPieces.add("I5");
        remainingPieces.add("I4");
        remainingPieces.add("L4");
        remainingPieces.add("Z4");
        remainingPieces.add("O4");
        remainingPieces.add("T4");
        remainingPieces.add("V3");
        remainingPieces.add("I3");
        remainingPieces.add("I2");
        remainingPieces.add("I1");
    }

    //Override makeMove method to improve SimpleBots Strategy
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
        }
        else {
            // Play the largest legal piece found
            // N.B.: The game control checks that we have at least one valid move before calling this method
            ArrayList<Move> available_moves = getPlayerMoves(this, board);
            return findBiggestLegalPiece(available_moves, remainingPieces);
        }
    }

    //Implementing a Greedy Algorithm:
    public Move findBiggestLegalPiece(ArrayList<Move> movelist, ArrayList<String> remainingPieces){
        for(int i = 0; i < remainingPieces.size(); i++){
            for(int j = 0; j < movelist.size(); j++){
//                System.out.println("\n" + i + " " + j);
                if(remainingPieces.get(i).equals(movelist.get(j).getGamepieceName())){
                    remainingPieces.remove(i);
                    return movelist.get(j);
                }
            }
        }
        return null;
    }
}
