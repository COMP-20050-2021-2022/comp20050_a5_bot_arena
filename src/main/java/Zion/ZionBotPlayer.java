package Zion;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Move;

import java.util.ArrayList;

public class ZionBotPlayer extends SimpleBotPlayer {

    public ZionBotPlayer(int playerNo) {
        super(playerNo);
    }

    @Override
    public Move makeMove(Board board) {

        ArrayList<Move> moves = getPlayerMoves(this, board);

        int depth = 20;
        boolean maximizingPlayer = true;

        Move bestMove;
        int bestScore = -1 * Integer.MAX_VALUE;

        Board possibleBoard = new Board(board);

        for(Move move : moves){
            int score = minimax(possibleBoard, depth,0,0, maximizingPlayer);
            if(score>bestScore){
                bestScore = score;
                bestMove = move;
            }
        }

        return super.makeMove(board);
    }

    public int SimpleScore(Board board,int playerNo){
        int eval = 0;
        for (int y=(Board.HEIGHT-1); y>=0; y--) {
            for (int x=0; x<Board.WIDTH; x++) {
                eval+= board.getOccupyingPlayer(x,y) == playerNo? +board.getOccupyingPlayer(x,y):-board.getOccupyingPlayer(x,y);
            }
        }
        return eval;
    }
    public int minimax(Board board,int depth, int alpha, int beta, boolean maximizingPlayer){
        //Check if depth is 0 or game is over
        if(depth==0 || !board.playerHasMoves(this)) {
            //if true return evaluation of that position
            return SimpleScore(board,this.getPlayerNo());
        }
        if(maximizingPlayer) {
            Integer maxEval = -1 * Integer.MAX_VALUE;
            ArrayList<Move> moves = getPlayerMoves(this, board);
            //for each child in positiion
            for(Move move : moves) {
                board.makeMove(move);
                //eval = this child position by using recurcion minimax(child, depth-1,false)
                int eval = minimax(board, depth-1, alpha,beta, false);
                maxEval = Integer.max(maxEval, eval);
                alpha = Integer.max(alpha,eval);
                if(beta<=alpha)
                    break;

            }
            return maxEval;
        }
        else {
            Integer minEval = Integer.MAX_VALUE;
            //for each child in positiion
            ArrayList<Move> moves = getPlayerMoves(opponent, board);
            for(Move move : moves) {
                //Do the child move
                board.makeMove(move);
                //eval = this child position by using recurcion minimax(child, depth-1,true)
                int eval = minimax(board, depth-1, alpha,beta, true);
                //minEval = min(maxEval, eval)
                minEval = Integer.min(minEval, eval);
                beta = Integer.min(beta, eval);
                if(beta<=alpha)
                    break;
            }
            return minEval;
        }
    }

}
