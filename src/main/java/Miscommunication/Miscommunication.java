package Miscommunication;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Move;
import model.Player;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Miscommunication extends SimpleBotPlayer {


    public Miscommunication(int playerNo) {
        super(playerNo);
    }
    @Override
    public ArrayList<Move> getPlayerMoves(Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
        Collection<String> gamepieceNames = player.getGamepieceSet().getPieces().keySet();
        for (String gamepieceName : gamepieceNames) {
            moves.addAll(
                    getMovesWithGivenGamepiece(
                            player.getGamepieceSet().getPieces().get(gamepieceName),
                            gamepieceName,
                            player,
                            board)
            );
        }
        ///testing sizes of each piece before sort
//        for (int i=0; i < moves.size(); i++){
//            System.out.println(moves.get(i).getGamepiece().getLocations().length);
//        }
        ////sorting array of valid moves using bubble sort
        for (int i = 0; i < moves.size(); i++){
            for(int j = 0; j < moves.size()-1-1;j++){
                if( moves.get(j).getGamepiece().getLocations().length > moves.get(j+1).getGamepiece().getLocations().length){
                    Collections.swap(moves, j, j+1);
                }
            }
        }
        //printing the moves array, now its sorted by piece size
        ////now, the above command return getPlayerMoves(this, board).get(0) will return the biggest pieces first
//        for (int i=0; i < moves.size(); i++){
//            System.out.println("NEW  SSSSSSSSSSSSSSSSorted ----------------");
//            System.out.println(moves.get(i).getGamepiece().getLocations().length);
//        }

        return moves;
    }




    @Override
    public Move makeMove(Board board) {

        ArrayList<Move> moves = getPlayerMoves(this, board);


        return super.makeMove(board);
    }

}