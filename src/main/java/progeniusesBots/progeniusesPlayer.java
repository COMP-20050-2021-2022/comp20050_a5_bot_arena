// Team name: progeniuses
// student numbers: 20338321, 20715801, 19209233
// progeniuses bot that implements the algorithm
// where it places the first largest weight size on the board
// when its turn to play.

package progeniusesBots;
import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;
import java.util.ArrayList;
// import the libraries

public class progeniusesPlayer extends SimpleBotPlayer {

    public progeniusesPlayer(int playerNo) {
        super(playerNo);
    }
    // constructor

    @Override
    public Move makeMove(Board board) {

        ArrayList<Move> moves = new ArrayList<>();
        // declare variable that saves all the bigest weight moves
        int i, currentMax;
        // declare variable for the biggest valid move

        currentMax = 0; // assigning the start

        for(Move playerMove : getPlayerMoves(this, board)){
            i = playerMove.getGamepiece().getLocations().length; // gets the largest size
            if(currentMax < i){ // if its greater then the currentMax then assign it
                currentMax = i;
            }
        }

        // saves the largest weights in arraylist move  of valid move
        for(Move playerMove : getPlayerMoves(this, board)){
            i = playerMove.getGamepiece().getLocations().length;
            if(currentMax == i){
                moves.add(playerMove);
            }
        }

        if (isFirstMove) {
            isFirstMove = false;
            // Play gamepiece "X" at the starting location in default orientation
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("X")),
                    "X",
                    new Location(board.startLocations[getPlayerNo()]));
        } else {
            // returns the largest piece wight for the current game on the board
            return moves.get(0);
        }
    }

}
