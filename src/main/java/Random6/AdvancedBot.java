package Random6;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Group: Random 6

public class AdvancedBot extends SimpleBotPlayer {

    boolean isFirstMove;

    public AdvancedBot(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    @Override
    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;
            /*//test
            return GetBestmove(getPlayerMoves(this, board), board);*/
            // Play gamepiece "X" at the starting location in default orientation
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("X")),
                    "X",
                    new Location(board.startLocations[getPlayerNo()]));
        } else {
            // N.B.: The game control checks that we have at least one valid move before calling this method
            return GetBestmove(getPlayerMoves(this, board), board); //
        }
    }

    public Move GetBestmove(ArrayList<Move> posMov, Board board) {
        List< Integer> posMovScores = new ArrayList<>();

        for (int aa = 0; aa < posMov.size(); aa++) {
            posMovScores.add(MoveScore(posMov.get(aa), board));
        }

        return posMov.get(posMovScores.indexOf(Collections.max(posMovScores)));
    }
    private Integer SwapPlayerNo(int n){
        if (n == 0){
            return 1;
        }else return 0;
    }

    public Integer MoveScore(Move move, Board board) {
        int score = 0;
        score = BigPiece(move) + TakeCorners(move, board);
        return score;
    }

    public Integer BigPiece(Move move) {
        return move.getGamepiece().getLocations().length;
    }

    public Integer TakeCorners(Move move, Board board) {
        int x = move.getLocation().getX();
        int y = move.getLocation().getY();
        boolean blocking = false;
        blocking = touchesOtherPlayerPiecesAtCorners(board,move.getGamepiece(), x, y);
        if(blocking){
            return 5;
        } else {return 0;}

    }


    public boolean touchesOtherPlayerPiecesAtCorners(Board board, Gamepiece gamepiece, int x, int y) {
        boolean atLeastOneSquareTouchesAtCorner = false;
        int oppPlayerNo = opponent.getPlayerNo();
        for (Location l : gamepiece.getLocations()) {
            if (boardSquareTouchesOnlyAtACorner(board, l.getX() + x, l.getY() + y, oppPlayerNo)) {
                atLeastOneSquareTouchesAtCorner = true;
            }
        }
        return  atLeastOneSquareTouchesAtCorner;
    }//for AdvancedBotPlayer

    private boolean boardSquareTouchesOnlyAtACorner(Board board, int x, int y, int playerNo) {
        if ( boardSquareContains(board, x-1,y-1,playerNo) && !boardSquareContains(board, x-1,y,playerNo) && !boardSquareContains(board, x,y-1,playerNo)
                || boardSquareContains(board, x+1,y-1,playerNo) && !boardSquareContains(board, x,y-1,playerNo) && !boardSquareContains(board, x+1,y,playerNo)
                || boardSquareContains(board, x-1,y+1,playerNo) && !boardSquareContains(board, x-1,y,playerNo) && !boardSquareContains(board, x,y+1,playerNo)
                || boardSquareContains(board, x+1,y+1,playerNo) && !boardSquareContains(board, x,y+1,playerNo) && !boardSquareContains(board, x+1,y,playerNo))
            return true;
        else
            return false;
    }

    private boolean boardSquareContains(Board board, int x, int y, int playerNo) {
        if ( x < 0 || x >= 14 || y < 0 || y >= 14) return false;
        if (!board.isOccupied(x,y)) return false;
        if (board.getOccupyingPlayer(x,y) != playerNo) return false;
        return true;
    }
}
