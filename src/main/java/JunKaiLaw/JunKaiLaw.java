package JunKaiLaw;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;

public class JunKaiLaw extends SimpleBotPlayer {

    public JunKaiLaw(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }


    @Override
    public Move makeMove(Board board) {

        ArrayList<Move> moves = getPlayerMoves(this, board);

        if(isFirstMove){
            isFirstMove = false;

            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        }
        else {
            int[] bestMove = new int[moves.size()];

            int maxPiece = 0;

            for(Move move : moves) {
                Board testboard = new Board(board);
                testboard.makeMove(move);
                for(int i = 0; i < moves.size(); i++){
                    bestMove[i] += moves.get(i).getGamepiece().getLocations().length;
                    if(bestMove[maxPiece] < bestMove[i]){
                        maxPiece = i;
                        System.out.println(maxPiece);
                    }
                }

            }
            return moves.get(maxPiece);
        }

    }



}


