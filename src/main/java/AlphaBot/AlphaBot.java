//Team name: Alpha
package AlphaBot;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Move;

public class AlphaBot extends SimpleBotPlayer {

    public AlphaBot(int playerNo) {
        super(playerNo);
    }

    public Move makeMove(Board board) {
        PossibleBoard pb = new PossibleBoard(board, this);
        return pb.getMove();
    }

}
