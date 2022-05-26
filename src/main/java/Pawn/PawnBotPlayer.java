// Created by Pawn:
//Jingyi Cui   19204584
//Haocheng Sun 19203637
//Jason Lok    20366363

package Pawn;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import static java.lang.Math.abs;

//subclass of SimpleBotPlayer, with extra additional implementation with our algorithm
public class PawnBotPlayer extends SimpleBotPlayer {

    public PawnBotPlayer(int playerNo) {
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
            // Play the first found valid move.
            // N.B.: The game control checks that we have at least one valid move before calling this method
            return getOptimalMove(this, board);
        }
    }

    //returns move with most corners covered
    public Move getOptimalMove(Player player, Board board){

        //return optimalMove
        Move optimalMove = getPlayerMoves(player, board).get(0);

        //Initialise arraylists with opponentCorners and available Player(bot) moves
        ArrayList<Location> opponentCorners = getOpponentCorners(opponent, board);
        ArrayList<Move> botMoves = getPlayerMoves(player, board);

        //loop through and check if bot moves overlap with corners and keep count
        HashMap<Move, Integer> moves = new HashMap<>();
        for(Move move : botMoves){
            moves.put(move, 0);
            for(Location corner : opponentCorners){
                if(coversCorner(move, corner)){
                    moves.put(move, moves.get(move)+1);
                }
            }
        }

        //loop to find the max count in available botMoves that cover corners
        ArrayList<Move> multiMaxScores = new ArrayList<>();
        int max=0;
        for(Move move : botMoves){
            int score = moves.get(move);
            if(score>max){
                max = score;
                multiMaxScores.clear();
                multiMaxScores.add(move);
            }
            if(score==max){
                multiMaxScores.add(move);
            }
        }

        //if multiple Max Scores, find moves which uses the largest pieces
        ArrayList<Move> multiMaxSize = new ArrayList<>();
        max=0;
        for(Move move : multiMaxScores) {
            int score = 0;
            if(move.getGamepieceName().length()>1) {
                score = move.getGamepieceName().charAt(1) - '0';
            }
            else{
               score = 5;
            }

            if(score>max){
                max = score;
                multiMaxSize.clear();
                multiMaxSize.add(move);
            }
            if(score==max){
                multiMaxSize.add(move);
            }
        }


        //if multiple same max score, pick the most central one
        int min = 14;
        for(int i=0;i<multiMaxSize.size();i++){
            int center = (int)(abs(multiMaxSize.get(i).getLocation().getX()-6.5)+abs(multiMaxSize.get(i).getLocation().getY()-6.5));
            if(center<min){
                optimalMove=multiMaxSize.get(i);
                min=center;
            }
        }

        return optimalMove;
    }

    //find corner's where opponent can play next turn
    public ArrayList<Location> getOpponentCorners(Player player, Board board){

        ArrayList<Location> emptyCorners = new ArrayList<>();
        //get all opponent's playable empty spaces
        for(int i=1;i<13;i++){
            for(int j=1;j<13;j++){
                if(board.getOccupyingPlayer(i+1, j+1)==player.getPlayerNo() ||
                        board.getOccupyingPlayer(i+1, j-1)==player.getPlayerNo() ||
                        board.getOccupyingPlayer(i-1, j+1)==player.getPlayerNo() ||
                        board.getOccupyingPlayer(i-1, j-1)==player.getPlayerNo()){
                    emptyCorners.add(new Location(i, j));
                }
            }
        }

        //see if opponent can play in those empty positions with the pieces in their hand
        ArrayList<Location> opponentCorners = new ArrayList<>();
        Collection<Gamepiece> gamepieces = player.getGamepieceSet().getPieces().values();
        //for every game piece in every orientation, check if any game pieces in opponent's hand can cover the corner
        //if yes, add the corner location to arraylist
        for(Gamepiece gamepiece : gamepieces){
            for(Location l : emptyCorners) {
                for(Location pieceLocations : gamepiece.getLocations()) {

                    for(int i=0;i<2;i++) {
                        for(int j=0;j<4;j++){
                            Move test = new Move(player, gamepiece, "", new Location(l.getX()-pieceLocations.getX(), l.getY()- pieceLocations.getY()));
                            if (board.isValidSubsequentMove(test)) {
                                opponentCorners.add(l);
                            }
                            gamepiece.rotateRight();
                        }
                        gamepiece.flipAlongY();
                    }

                }
            }
        }

        return opponentCorners;
    }

    //check if a move will cover one of the opponent's corners
    public boolean coversCorner(Move move, Location corner){

        boolean covered = false;
        for(Location l : move.getGamepiece().getLocations()){
            //if one of the game piece squares will sit on top of the opponent's playable corner square
            if(move.getLocation().getX()+l.getX()==corner.getX() && move.getLocation().getY()+l.getY()==corner.getY()){
                covered =  true;
            }
        }

        return covered;
    }
}
