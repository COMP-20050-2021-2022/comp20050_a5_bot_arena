/*Team: Random1
  Team members: Niall Meagher - 20768511
                Nathan Mahady - 20522563
                Floriana Melania Munteanu - 20349023
*/

package Random1;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.*;

import static java.lang.Math.abs;

public class Random1Bot extends SimpleBotPlayer {

    protected boolean contact; //tells us if players have met on board yet
    protected boolean hasFirstMoveAdvantage; //true if bot makes first move, false if bot makes second move

    public Random1Bot(int playerNo) {
        super(playerNo);
        contact = false;
        hasFirstMoveAdvantage = true;
    }

    public Board getBoard() {
        return board;
    }

    @Override
    public Move makeMove(Board board){
       if(isFirstMove) {
           //if neither starting position is occupied, we make the first move
           if(board.isOccupied(9,4) || board.isOccupied(4,9))
               hasFirstMoveAdvantage = false;
           return super.makeMove(board);
       } else {
           if(!contact){ //check to see if players have made contact yet
               contact = checkContact();
           }
           ArrayList<Move> myPossibleMoves = getPlayerMoves(this, board);

           //pre-contact and if we are losing, we just want to gain as much score as possible
           if(!contact || playerAdvantage() < 0)
               return getPlayersBestMove(myPossibleMoves);

           //if we are winning or in a tie, we can sacrifice our best move to prevent the opponent
           //from making a comeback
           else return inhibitOpponentsBestMove(myPossibleMoves);
       }
    }


    /**
     * Find the best current move to make irrespective of opponent's subsequent move
     * @param possibleMoves the list of possible moves
     * @return Best move based on criteria described by compareMoves()
     */
    public Move getPlayersBestMove(ArrayList<Move> possibleMoves){
        Quicksort.quickSort(possibleMoves, this);
        return possibleMoves.get(0);
    }

    //tries to find the best move for the player that will force the opponent to choose a move that is not their best
    public Move inhibitOpponentsBestMove (ArrayList<Move> possibleMoves){
        int limit = possibleMoves.size();

        ArrayList<Move> opponentMoves = getPlayerMoves(opponent, board);
        int i = 0;

        quickSortPlayer(opponentMoves, board);

        if(possibleMoves.size() > 0 && opponentMoves.size() > 0){
            //If our best move is better than their best move, we have nothing to worry about
            if(compareMoves(possibleMoves.get(0), opponentMoves.get(0), board) > 0){
                System.out.println("first move, best move");
                return possibleMoves.get(0);
            }
            else{
                while(i<limit){
                    Board board1 = new Board(board); //create an auxiliary board

                    //place the bot's current best move on the auxiliary board
                    board1.makeMove(possibleMoves.get(i));

                    //get the opponent's possible moves after the piece was placed
                    opponentMoves = getPlayerMoves(opponent, board1);

                    //if this move gives opponent no possible moves, we play it immediately
                    if(opponentMoves.size() == 0)
                        return possibleMoves.get(i);

                    else quickSortPlayer(opponentMoves, board1); //sort the moves from best to worst

                    //if the opponent's subsequent best move isn't better than the move we just placed
                    //return this move
                    if(compareMoves(opponentMoves.get(0), possibleMoves.get(i), board, board1) < 0){
                        System.out.println("better move found");
                        return possibleMoves.get(i);
                    }
                    //if not, we keep searching
                    else{
                        i++;
                    }
                }
            }
        }
        
        //if no better move is found, return the first move from the array
        System.out.println("No better move: " + possibleMoves.get(0).getGamepieceName());
        return possibleMoves.get(0);
    }

    /**
     * Deduces whether one player has a discernible advantage over the other.
     * If bot has first move advantage, then at the start of our turn both players has made the same number
     * of moves, so score diff is easy to calculate.
     * If bot does not have first move advantage, then on our turn, the opponent will have one extra piece on the board.
     * If we can close the score gap in one turn (ie the size of our biggest piece is not less than the score gap), then
     * we cannot be sure that we are losing the game based on score alone
     * @return 1 if player has an advantage over opponent, -1 if opponent has an advantage over player,
     * and 0 if no observable advantage between players.
     */
    public int playerAdvantage(){
        //We use scoreThreshold to evaluate advantage states relative to who made the first move
        int scoreGap = 0;
        if(!hasFirstMoveAdvantage){
            Gamepiece[] gamepieces = getGamepieceSet().getPieces().values().toArray(new Gamepiece[getGamepieceSet().getPieces().size()]);
            scoreGap = -1 * gamepieces[gamepieces.length-1].getLocations().length;
        }


        if(getScoreDifference()<scoreGap)  //bot is losing
            return -1;

        else if(getScoreDifference() > 0)   //bot is winning
            return 1;

        else{ //advantage state can't be inferred from score diff
            int openCornerDiff = getOpenCornerDifference(board);
            if(openCornerDiff<0)    //bot has fewer corners open
                return -1;
            else if(openCornerDiff>0)   //bot has more corners open
                return 1;
            else        //no observable advantage
                return 0;
        }
    }

    /**
     * returns the number of open corner a location has
     * THe try-catch blocks are there to prevent the game from crashing when x or y == 0, we don't need to do
     * anything in the catch statement.
     */
    public int hasOpenCorners(Player player, Board board, int x, int y){
        int corners = 0;
        //Top-left corner
        try {
            if (!board.isOccupied(x - 1, y + 1) && board.getOccupyingPlayer(x - 1, y) != player.getPlayerNo()
                    && board.getOccupyingPlayer(x, y + 1) != player.getPlayerNo())
                corners++;
        } catch (ArrayIndexOutOfBoundsException ex){}

        //Top-right corner
        try{
            if (!board.isOccupied(x + 1, y + 1) && board.getOccupyingPlayer(x + 1, y) != player.getPlayerNo()
                    && board.getOccupyingPlayer(x, y + 1) != player.getPlayerNo())
                corners++;
        } catch(ArrayIndexOutOfBoundsException ex){}

        //Bottom-left corner
        try{
            if (!board.isOccupied(x - 1, y - 1) && board.getOccupyingPlayer(x - 1, y) != player.getPlayerNo()
                    && board.getOccupyingPlayer(x, y - 1) != player.getPlayerNo())
                corners++;
        }catch(ArrayIndexOutOfBoundsException ex){}

        //Bottom-right corner
        try{
            if (!board.isOccupied(x + 1, y - 1) && board.getOccupyingPlayer(x + 1, y) != player.getPlayerNo()
                    && board.getOccupyingPlayer(x, y - 1) != player.getPlayerNo())
                corners++;
        }catch(ArrayIndexOutOfBoundsException ex){}

        return corners;
    }

    /**
     * @param player
     * @param board
     * @return the number of open corners player has available
     */
    public int getOpenCorners(Player player, Board board){
        int corners = 0;
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if(board.getOccupyingPlayer(x,y) == player.getPlayerNo()){
                    corners += hasOpenCorners(player, board, x, y);
                }
            }
        }

        return corners;
    }

    /**
     * Compares two moves based on:
     * 1. size of piece being placed,
     * 2. number of open corners
     * @return 1 if m1 is better than m2, -1 if m2>m1, 0 if m1==m2
     */
    public int compareMoves(Move m1, Move m2, Board board){
        int moveSize1 = m1.getGamepiece().getLocations().length;
        int moveSize2 = m2.getGamepiece().getLocations().length;
        if(moveSize1 > moveSize2)
            return -1;
        else if(moveSize2 > moveSize1)
            return 1;

        else { //pieces are the same size
            Board board1 = new Board(board);
            board1.makeMove(m1);

            Board board2 = new Board(board);
            board2.makeMove(m2);

            int openCorners1 = getOpenCorners(m1.getPlayer(), board1);
            int openCorners2 = getOpenCorners(m2.getPlayer(), board2);

            if(openCorners1 > openCorners2)
                return -1;
            else if(openCorners2 > openCorners1)
                return 1;
            else{//same number of open corners so we base the move on how close to the centre it is
                int distanceFromMid1 = distanceFromMiddle(m1);
                int distanceFromMid2 = distanceFromMiddle(m2);

                //move 2 is closer to centre of board -> move 2 is better
                if(distanceFromMid1 < distanceFromMid2)
                    return -1;
                //move 1 is closer to centre of board -> move 1 is better
                else if(distanceFromMid2 < distanceFromMid1)
                    return 1;
            }
        }



        return 0;//Moves are equal
    }

    //Allows us to compare bot's move to opponent's subsequent move by passing 2 boards as arguments
    public int compareMoves(Move m1, Move m2, Board board1, Board board2){
        int moveSize1 = m1.getGamepiece().getLocations().length;
        int moveSize2 = m2.getGamepiece().getLocations().length;
        if(moveSize1 > moveSize2)
            return -1;
        else if(moveSize2 > moveSize1)
            return 1;

        else { //pieces are the same size
            Board boardM1 = new Board(board1);
            boardM1.makeMove(m1);

            Board boardM2 = new Board(board2);
            boardM2.makeMove(m2);

            int openCorners1 = getOpenCorners(m1.getPlayer(), boardM1);
            int openCorners2 = getOpenCorners(m2.getPlayer(), boardM2);

            if(openCorners1 > openCorners2)
                return -1;
            else if(openCorners2 > openCorners1)
                return 1;
            else{//same number of open corners so we base the move on how close to the centre it is
                int distanceFromMid1 = distanceFromMiddle(m1);
                int distanceFromMid2 = distanceFromMiddle(m2);

                //move 2 is closer to centre of board -> move 2 is better
                if(distanceFromMid1 < distanceFromMid2)
                    return -1;
                    //move 1 is closer to centre of board -> move 1 is better
                else if(distanceFromMid2 < distanceFromMid1)
                    return 1;
            }
        }



        return 0;//Moves are equal
    }

    /**
     * calculates difference in open corners between 2 players. return a positive int if bot has more
     * open corners than opponent. returns a negative int if bot has fewer open corners than opponent
     * @param board current board state
     * @return bot's open corners - opponent's open corners
     */
    public int getOpenCornerDifference(Board board){
        return getOpenCorners(this, board) - getOpenCorners(opponent, board);
    }

    public int getScoreDifference(){
        return playerScore() - opponent.playerScore();
    }

    public boolean boardSquareTouchesOpponentPlayer(int x, int y, int playerNo){
        int xCoordinateOffset = x-1;
        int yCoordinateOffset = y-1;
        int opponentNo = (playerNo + 1) %2;
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                if(board.getOccupyingPlayer(xCoordinateOffset+i, yCoordinateOffset+j)
                == opponentNo)
                    return true;
            }
        }
        return false;
    }

    public boolean checkContact(){
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if(board.isOccupied(x,y)){
                    if(boardSquareTouchesOpponentPlayer(x,y,getPlayerNo()))
                        return true;
                }
            }
        }
        return false;
    }

    public void quickSortPlayer(final List<Move> list, Board board) {
        class QuickSort{ //We need submethods to call quickSort between two indices
            int partition(List<Move> list, int lo, int hi){
                int i = lo, j = hi+1;
                while(true){
                    //find item to left of pivot to swap
                    while(compareMoves(list.get(++i), list.get(lo), board) < 0)
                    {if(i==hi) break;}

                    //find item to right of pivot to swap
                    while(compareMoves(list.get(lo), list.get(--j), board) < 0)
                    {if(i==lo) break;}

                    //Check if pointers cross
                    if(i>=j) break;

                    Collections.swap(list, i, j);
                }

                Collections.swap(list, lo, j);

                return j;
            }

            void sort(List<Move> list, int lo, int hi){
                if(hi<=lo) return;
                int j = partition(list, lo, hi);
                sort(list, lo, j-1);
                sort(list, j+1, hi);
            }
        }
        QuickSort qs = new QuickSort();
        Collections.shuffle(list);
        qs.sort(list, 0, list.size()-1);
    }

    /**
     * Takes a move and checks how far each square is away from the center of the board.
     * @param move
     * @return A value that represents the distance from the center a piece is
     */
    public int distanceFromMiddle(Move move){
        int distanceFromMid = 0;
        for(Location l: move.getGamepiece().getLocations()){
            distanceFromMid += abs(7 - (l.getX() + move.getLocation().getX())) + abs(7 - (l.getY() + move.getLocation().getY()));
        }
        return distanceFromMid;
    }
}
