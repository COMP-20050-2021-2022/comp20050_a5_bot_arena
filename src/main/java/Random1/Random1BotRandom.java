/*Team: Random1
  Team members: Niall Meagher - 20768511
                Nathan Mahady - 20522563
                Floriana Melania Munteanu - 20349023
*/

package Random1;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.ArrayList;
import java.util.Random;

public class Random1BotRandom extends SimpleBotPlayer {
    public Random1BotRandom(int playerNo) {
        super(playerNo);
    }

    @Override
    public Move makeMove(Board board) {
        Random rand = new Random();
        if(isFirstMove){
            isFirstMove = false;
            // Play gamepiece "F" at the starting location in default orientation
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        }else{
            ArrayList<Move> myPossibleMoves = getPlayerMoves(this, board);
            return myPossibleMoves.get(rand.nextInt(myPossibleMoves.size()));
        }
    }
}
