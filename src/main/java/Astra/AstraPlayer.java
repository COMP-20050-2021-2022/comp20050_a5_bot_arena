/**
 * Team name: Astra
 * This project was worked on by Alexander Fedkovych (20395981), Gareth Nomayo (20921972) and Desen Chen (20387876)
 */
package Astra;

import model.*;
import java.util.ArrayList;

public class AstraPlayer extends SimpleBot.SimpleBotPlayer {

    public AstraPlayer(int playerNo) {
        super(playerNo);
    }

    /**
     * For the AI bot, we used a greedy algorithm.
     * This heuristic does not intend to find the best solution, but it terminates in a reasonable number of steps;
     * finding an optimal solution to such a complex problem typically requires unreasonably many steps.
     * A greedy heuristic can yield locally optimal solutions that approximate a globally optimal solution
     * in a reasonable amount of time.
     * @param board - game board
     * @return possible moves for the bot
     */
    @Override
    public Move makeMove(Board board) {
        // store list of possible moves for the bot
        ArrayList<Move> possibleMoves = new ArrayList<>();
        // declare and initialise variable for max weight of a piece
         int Max = 0;

         Board possibleBoard = new Board(board);

        if (isFirstMove) {
            isFirstMove = false;
            // Play gamepiece "X" at the starting location in default orientation
            // this piece has most amount of corners, so most amount of possible moves to continue off with
            return new Move(this,
                    new Gamepiece(getGamepieceSet().get("X")),
                    "X",
                    new Location(board.startLocations[getPlayerNo()]));
        }

        // for loop to determine if max weight of piece is less than the actual weight
        for (Move move : getPlayerMoves(this,possibleBoard)){
            int weight = move.getGamepiece().getLocations().length;
            if (Max < weight) Max = weight;
        }

        for (Move move : getPlayerMoves(this,possibleBoard)){
            // weight is determined by the amount of space it takes up on the board
            int weight = move.getGamepiece().getLocations().length;
            if (Max == weight) possibleMoves.add(move);
        }

        // loop to look for moves with the highest amount corners for growth
        for (Move move : getPlayerMoves(this,possibleBoard)){
            // position of corners at x and y
            int weight = move.getGamepiece().getLocations().length;
            int cornerX = move.getLocation().getX();
            int cornerY = move.getLocation().getY();
            if (Max == weight && cornerX > Max && cornerY > Max) possibleMoves.add(move);
        }


        return possibleMoves.get(0);
    }
}