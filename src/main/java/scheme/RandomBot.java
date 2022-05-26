package scheme;
//This is just a test bot that moves randomly

import java.util.ArrayList;
import java.util.Random;
import model.*;

import SimpleBot.SimpleBotPlayer;

public class RandomBot extends SimpleBotPlayer{
    
    public RandomBot(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

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
        } else {
            //String[] save = generatePieceString(getGamepieceSet());
            ArrayList<Move> moves = getPlayerMoves(this, board);

            return moves.get(new Random().nextInt(moves.size()));
        }
    }

    public ArrayList<Move> getPlayerMoves(Player player, Board board) {
        return super.getPlayerMoves(player, board);
    }

    public ArrayList<Move> getMovesWithGivenGamepiece(Gamepiece gamepiece, String gamepieceName, Player player, Board board){
        return super.getMovesWithGivenGamepiece(gamepiece, gamepieceName, player, board);
    }

    public ArrayList<Move> getMovesWithGivenOrientation(Gamepiece piece, String gamepieceName, Player player, Board board) {
        return super.getMovesWithGivenOrientation(piece, gamepieceName, player, board);
    }
}


