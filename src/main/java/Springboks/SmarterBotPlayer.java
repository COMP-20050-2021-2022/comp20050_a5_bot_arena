package Springboks;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;
import static model.Board.HEIGHT;
import static model.Board.WIDTH;

// This is the class we use for assignment 5
// we only code in here and leave all the other classes

/**
 * The algorithm for picking the best available move is as follows:
 *
 * Step 1: Iterate over every all valid player moves using getPlayerMoves
 * Step 2: For every valid move, assign a score to the move
 * Step 3: Pick the move with the highest score
 *
 * To evaluate the score for a move:
 *      - Find all the locations that the individual squares of the piece would land on.
 *      - For each square, assign a score to it based on its location on the board and in relation to the other players pieces.
 *      - add up the scores of all the squares in the piece and their sum will be the score assigned to that given move.
 *
 * The scores for each tile are assigned as follows:
 *      - +3 for every square in the piece being placed (e.g. a 3 square piece = 9 points by default)
 *      - +5 if it lands on one of the centre squares on the board
 *      - +2 if it lands on a square around the centre squares
 *      - -10 for placing the single piece, unless it's the last piece
 *      - +5 for every square that is touching an opponents piece at the corners
 */

public class SmarterBotPlayer extends SimpleBotPlayer{
    public SmarterBotPlayer(int playerNo) {
        super(playerNo);
    }

    private final Location[] CENTRE_TILES = {new Location(6,6), new Location(6,7), new Location(7,6), new Location(7,7)};
    private final Location[] TILES_AROUND_CENTRE = {new Location(5,5), new Location(5,6), new Location(5,7), new Location(5,8),
            new Location(6,5), new Location(6,8), new Location(7,5), new Location(7,8),
            new Location(8,5), new Location(8,6), new Location(8,7), new Location(8,8)};

    private boolean occupied[][] = new boolean[WIDTH][HEIGHT];
    private int occupyingPlayer[][] = new int[WIDTH][HEIGHT];
    private ArrayList<Location> TILES_TOUCHING_OPPONENT_CORNERS = new ArrayList<>();

    public boolean[][] getOccupied() {
        return occupied;
    }
    public int[][] getOccupyingPlayer() {
        return occupyingPlayer;
    }
    public Location[] getTILES_AROUND_CENTRE() {
        return TILES_AROUND_CENTRE;
    }
    public Location[] getCENTRE_TILES() {
        return CENTRE_TILES;
    }
    public ArrayList<Location> getTILES_TOUCHING_OPPONENT_CORNERS() {
        return TILES_TOUCHING_OPPONENT_CORNERS;
    }

    // This method gets all the locations of the opponetns corners and then adds them to the
    // TILES_TOUCHING_OPPONENT_CORNERS ArrayList so we know the positions on the board that they are located
    // in order to assign a score for the best move available; i.e. out of the bots pieces left to play, which one
    // has the highest points. That will be the best move. In this case, it blocks the opponents corners.
    public void getLocationsForCorners(){
        // initialize the occupied boolean and occupied player.
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                getOccupied()[x][y] = board.isOccupied(x,y);
                getOccupyingPlayer()[x][y] = board.getOccupyingPlayer(x,y);
            }
        }
        // loop through the board and add the opponents corners locations to the ArrayList.
        for(int i =0; i<WIDTH; i++){
            for(int j=0; j<HEIGHT; j++){
                if(boardSquareTouchesAtACorner(i,j, opponent.getPlayerNo())){
                    //TILES_TOUCHING_OPPONENT_CORNERS2 = new Location[]{new Location(i, j)};
                    getTILES_TOUCHING_OPPONENT_CORNERS().add(new Location(i,j));
                }
            }
        }
        //return TILES_TOUCHING_OPPONENT_CORNERS;
    }


    // helper method that checks the corners
    private boolean boardSquareTouchesAtACorner(int x, int y, int playerNo) {
        if ( boardSquareContains(x-1,y-1,playerNo)
                || boardSquareContains(x+1,y-1,playerNo)
                || boardSquareContains(x-1,y+1,playerNo)
                || boardSquareContains(x+1,y+1,playerNo) )
            return true;
        else
            return false;
    }

    private boolean boardSquareContains(int x, int y, int playerNo) {
        if ( x < 0 || x >= occupied.length || y < 0 || y >= occupied[0].length) return false;
        if (! occupied[x][y]) return false;
        if (occupyingPlayer[x][y] != playerNo) return false;
        return true;
    }


    @Override
    public Move makeMove(Board board) {
        if(isFirstMove){
            // if this is the first move then just place a random piece of size 5
            isFirstMove = false;
            ArrayList<String> setOfPiecesLength5 = new ArrayList<String>();
            setOfPiecesLength5.add("I5");
            setOfPiecesLength5.add("L5");
            setOfPiecesLength5.add("V5");
            setOfPiecesLength5.add("Z5");
            setOfPiecesLength5.add("N");
            setOfPiecesLength5.add("P");
            setOfPiecesLength5.add("W");
            setOfPiecesLength5.add("U");
            setOfPiecesLength5.add("F");
            setOfPiecesLength5.add("X");
            setOfPiecesLength5.add("Y");
            setOfPiecesLength5.add("T5");
            // Play gamepiece "F" at the starting location in default orientation
            Random random = new Random();
            int randomInt = random.nextInt(setOfPiecesLength5.size());
            String randomPiece = String.valueOf(setOfPiecesLength5.get(randomInt));
            System.out.println(randomPiece);

            setOfPiecesLength5.remove(randomPiece);
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get(randomPiece)),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        }else {
            // Play the best available move
            // N.B.: The game control checks that we have at least one valid move before calling this method
            ArrayList<Move> validMoves = getPlayerMoves(this, board);
             return getBestMove(validMoves);
    }
}

    public Move getBestMove(ArrayList<Move> validMoves){
        int i = 0, max = 0, maxIndx = 0;
        // This is where we call getLocationsForCorners to populate the TILES_TOUCHING_OPPONENT_CORNERS ArrayList
        getLocationsForCorners();
        for(Move move : validMoves){
            // iterate over every possible valid move and get the score for each move
            int score;
            score = 3 * (move.getGamepiece().getLocations().length); // default score = (no. of squares in the gamepiece * 3)

            for(Location square : move.getGamepiece().getLocations()){
                // iterate over all the locations that each square in the piece will land on
                int x = move.getLocation().getX() + square.getX();
                int y = move.getLocation().getY() + square.getY();
                Location squareLocationOnBoard = new Location(x, y);

                for(Location centreTile : getCENTRE_TILES()){
                    // check to see if this square is on a centre tile
                    if(squareLocationOnBoard.equals(centreTile)){
                        score += 5;
                    }
                }

                for(Location tileAroundCentre : getTILES_AROUND_CENTRE()){
                    // check to see if this square in on a tile around the centre tiles
                    if(squareLocationOnBoard.equals(tileAroundCentre)){
                        score += 3;
                    }
                }

                // TODO check the square location to see if it is touching an opponents piece at the corners
                //getLocationsForCorners();
                for(Location tileTouchingOpponent : TILES_TOUCHING_OPPONENT_CORNERS){
                    // check to see, is tile touching an opponents piece at the corners
                    if(squareLocationOnBoard.equals(tileTouchingOpponent)){
                        score += 5;
                    }
                }
            }

            if(score >= max){ // check if this move has a higher score than the previous max
                max = score;
                maxIndx = i;
                //System.out.println("Max = "+ max);
            }
            i++;
        }

        System.out.println("Max = "+ max);
        return validMoves.get(maxIndx);
    }

}
