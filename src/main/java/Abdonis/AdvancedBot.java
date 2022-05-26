package Abdonis;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.ArrayList;

public class AdvancedBot extends SimpleBotPlayer {
    public AdvancedBot(int playerNo) {
        super(playerNo);
    }

    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;
            // Play gamepiece "F" at the starting location in default orientation
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        } else {
            //List to store all possible boards after the bots turn.
            ArrayList<Board> possibleBoards = new ArrayList<Board>();
            double maxEval = -999999999;
            Move bestMove = getPlayerMoves(this, board).get(0);

            //Add all possible boards to a list
            for( Move m : getPlayerMoves(this, board)) {
                Board b = new Board(board);
                b.makeMove(m);
                possibleBoards.add(b);
                double currentEval = evaluate(b);
                if(currentEval > maxEval){
                    maxEval = currentEval;
                    bestMove = m;
                }

            }

            System.out.println(maxEval);
            return bestMove;
        }
    }

    public double evaluate(Board b){
        int botPossibleMoves = getPlayerMoves(this,b).size();
        int opponentPossibleMoves = getPlayerMoves(opponent,b).size();

        int botOwnedSquares = 0;


        for(int i=0; i<board.HEIGHT;i++){
            for(int j=0; j<board.WIDTH;j++){
                if(board.getOccupyingPlayer(i,j) == this.getPlayerNo()) botOwnedSquares += 1;
            }
        }

        if(botOwnedSquares > 15) return (botPossibleMoves - opponentPossibleMoves);
        else return (botPossibleMoves - opponentPossibleMoves) + botOwnedSquares;
    }

}
