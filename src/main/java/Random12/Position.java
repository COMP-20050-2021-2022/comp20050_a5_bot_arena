// Team Random12
// Adam Goodwin 18322006
package Random12;

import model.Board;

import model.Move;
import model.Player;

public class Position {
    public Board board;
    public Player us;
    public Player opponent;
    boolean maximisingPlayer;

    public Position(Board board,Player us, Player opponent,boolean maximisingPlayer){
        this.board = board;
        this.us = us;
        this.opponent = opponent;
        this.maximisingPlayer = maximisingPlayer;
    }

    public Position makeChild(Move m){
        if(maximisingPlayer){
            Board modifiedBoard = new Board(board);
            modifiedBoard.makeMove(m);
            m.getPlayer().getGamepieceSet().remove(m.getGamepieceName());
            return new Position(modifiedBoard,us,opponent,false);
        } else{
            Board modifiedBoard = new Board(board);
            modifiedBoard.makeMove(m);
            m.getPlayer().getGamepieceSet().remove(m.getGamepieceName());
            return new Position(modifiedBoard,us,opponent,true);
        }

    }

    public void unmakeChild(Move m){
        m.getPlayer().getGamepieceSet().getPieces().put(m.getGamepieceName(), m.getGamepiece());

    }


}
