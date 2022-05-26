/*
 * Team Name: HawkEye
 * Team Members:
 * - Brendan Sadlier (20402884)
 * - Cian Kelly (20429616)
 * - Conor Tobin (19748189)
 */

package Hawkeye;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;

public class HawkeyeBotPlayer extends SimpleBotPlayer { //SubClass extends SimpleBotPlayer Class

    ArrayList<String> availablePieces = new ArrayList<>();

    //Constructor
    public HawkeyeBotPlayer(int playerNo) { //inherits super() constructor
        super(playerNo);

        //Initialize Array in descending order (The Largest Pieces to Smallest)
        availablePieces.add("W");
        availablePieces.add("P");
        availablePieces.add("U");
        availablePieces.add("F");
        availablePieces.add("X");
        availablePieces.add("Y");
        availablePieces.add("Z5");
        availablePieces.add("N");
        availablePieces.add("V5");
        availablePieces.add("T5");
        availablePieces.add("L5");
        availablePieces.add("I5");
        availablePieces.add("I4");
        availablePieces.add("L4");
        availablePieces.add("Z4");
        availablePieces.add("O4");
        availablePieces.add("T4");
        availablePieces.add("V3");
        availablePieces.add("I3");
        availablePieces.add("I2");
        availablePieces.add("I1");
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
                    new Location(Board.startLocations[getPlayerNo()]));
        }
        else {
            // Play the largest legal piece found
            // N.B.: The game control checks that we have at least one valid move before calling this method
            ArrayList<Move> available_moves = getPlayerMoves(this, board);
            return findBiggestLegalPiece(available_moves, availablePieces);
        }
    }

    public Move findBiggestLegalPiece(ArrayList<Move> listOfMoves, ArrayList<String> remainingPieces){
        for(int i = 0; i < remainingPieces.size(); i++){
            for (Move listOfMove : listOfMoves) {
                //System.out.println("\n" + i + " " + j);
                if (remainingPieces.get(i).equals(listOfMove.getGamepieceName())) {
                    remainingPieces.remove(i);
                    return listOfMove;
                }
            }
        }
        return null;
    }
}
