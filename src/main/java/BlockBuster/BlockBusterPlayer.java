/*
 * Toluwabori Akinola - 20720321
 * Daniel Adeoye - 20475956
 * Najat Bashiru - 20325793
 */

package BlockBuster;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Location;
import model.Move;
import model.Player;

import java.util.ArrayList;
import java.util.Comparator;

public class BlockBusterPlayer extends SimpleBotPlayer {

    static final int DEPTH = 3;
    boolean isFirstMove;

    public BlockBusterPlayer(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    @Override
    public Move makeMove(Board board){

        if(isFirstMove){
            isFirstMove = false;
            return super.makeMove(board);
        }
        return bestMove(board);

    }

    private Move bestMove(Board board) {
        int value = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Move move = null;
        Board tempBoard = new Board(board);

        ArrayList<Move> moves = getPlayerMoves(this, tempBoard);

        moves.sort(Comparator.comparingInt(o -> o.getGamepiece().getLocations().length));

        int j = moves.size()-1;

        for(int i = 0; i < 20; i++){

            if(j<0)
                break;

            tempBoard.makeMove(moves.get(j));
            int value2 = heuristic(moves.get(j));

            int newValue = value2 + minMax(tempBoard, 0, alpha, beta, false);

            if(newValue > value){
                value = newValue;
                move = new Move(moves.get(j));
            }

            alpha = Math.max(alpha, value);

            if(beta <= alpha)
                break;

            tempBoard = new Board(board);
            j--;
        }

        return move;
    }

    int minMax(Board board, int depth, int alpha, int beta, boolean maximize){
        if(depth == DEPTH || !board.playerHasMoves(this))
            return  heuristic2(board, this);


        Board tempBoard = new Board(board);
        int value = 0;

        if(maximize){
            ArrayList<Move> moves = getPlayerMoves(this, board);
            if(moves.isEmpty())
                return value;
            moves.sort(Comparator.comparingInt(o -> o.getGamepiece().getLocations().length));
            int j = moves.size()-1;


            value = Integer.MIN_VALUE;

            for(int i =0; i< 20; i++){
                if(j<0)
                    break;

                tempBoard.makeMove(moves.get(j));
                int value2 = heuristic(moves.get(j));
                int newValue = value2 + minMax(tempBoard, depth+1, alpha, beta, false);

                tempBoard = new Board(board);

                if(newValue > value){
                    value = newValue;
                }

                alpha = Math.max(value, alpha);

                if( beta <= alpha)
                    break;
            }
        } else {
            ArrayList<Move> moves = getPlayerMoves(this.opponent, board);

            if(moves.isEmpty())
                return value;

            moves.sort(Comparator.comparingInt(o -> o.getGamepiece().getLocations().length));

            int j = moves.size()-1;

            value = Integer.MAX_VALUE;

            for(int i=0; i<20; i++){

                if(j<0)
                    break;

                tempBoard.makeMove(moves.get(j));

                int value2 = heuristic(moves.get(j));

                int newValue = value2 + minMax(tempBoard, depth+1,alpha, beta, true);
                tempBoard = new Board(board);

                if(newValue < value){
                    value = newValue;
                }
                alpha = Math.min(alpha, value);

                if( beta <= alpha)
                    break;
            }
        }
        return value;

    }

    // Heuristic method that measures the score of a move based on how
    // close the piece is to the centre of the board
    int heuristic(Move m){


        int[][] scoreBoard = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 1},
                {1, 2, 3, 5, 5, 5, 5, 5, 5, 5, 5, 3, 2, 1},
                {1, 2, 3, 5, 7, 7, 7, 7, 7, 7, 5, 3, 2, 1},
                {1, 2, 3, 5, 7, 10, 10, 10, 10, 7, 5, 3, 2, 1},
                {1, 2, 3, 5, 7, 10, 14, 14, 10, 7, 5, 3, 2, 1},
                {1, 2, 3, 5, 7, 10, 14, 14, 10, 7, 5, 3, 2, 1},
                {1, 2, 3, 5, 7, 10, 10, 10, 10, 7, 5, 3, 2, 1},
                {1, 2, 3, 5, 7, 7, 7, 7, 7, 7, 5, 3, 2, 1},
                {1, 2, 3, 5, 5, 5, 5, 5, 5, 5, 5, 3, 2, 1},
                {1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        int total = 0;

        int x = m.getLocation().getX();
        int y = m.getLocation().getY();

        for(Location loc: m.getGamepiece().getLocations()){
            total += scoreBoard[x+loc.getX()][y+ loc.getY()];
        }

        return total;
    }

    // Heuristic method that evaluates the "score" of a player in a certain state
    // by the number of corners available for the player to play in
    int heuristic2(Board board, Player p){
        int corners = 0;
        for(int i=0; i<Board.HEIGHT; i++){
            for(int j =0; j<Board.WIDTH; j++){
                if(!board.isOccupied(i, j)){
                    if(isCorner(board, i, j, p) && sideNoTouch(board, i, j, p))
                        corners++;
                }
            }
        }

        return corners;
    }

    private boolean sideNoTouch(Board board, int i, int j, Player p) {
        int dRow, dCol;

        for(dRow = -1; dRow < 2; dRow += 2){
            if(i + dRow >= Board.WIDTH || i + dRow < 0)
                break;

            if(board.getOccupyingPlayer(i + dRow, j) == p.getPlayerNo())
                return false;
        }

        for(dCol = -1; dCol < 2; dCol += 2){
            if(j + dCol >= Board.WIDTH || j + dCol < 0)
                break;

            if(board.getOccupyingPlayer(i, j + dCol) == p.getPlayerNo())
                return false;
        }

        return true;
    }

    private boolean isCorner(Board b, int i, int j, Player p) {

        int dRow, dCol;

        for(dRow = -1; dRow < 2; dRow+=2){
            for (dCol = -1; dCol < 2; dCol+=2){

                if(i+dRow >= Board.WIDTH || i+dRow < 0 || j+dCol >= Board.WIDTH
                        || j+dCol < 0)
                    break;

                if(b.getOccupyingPlayer(i+dRow, j+dCol) == p.getPlayerNo())
                    return true;
            }
        }

        return false;
    }

}
