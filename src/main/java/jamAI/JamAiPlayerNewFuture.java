package jamAI;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.*;

import static com.badlogic.gdx.math.MathUtils.random;
import static java.util.Comparator.comparing;

public class JamAiPlayerNewFuture extends SimpleBotPlayer {
    static int turn = 0;
    int[] weightFirstPlayer = {50,20};
    int[] weightSecondPlayer = {80, 60};
    int[] weight = new int[2];

    public JamAiPlayerNewFuture(int playerNo) {
        super(playerNo);

        //Different weighting is needed depending on whether you go first or second


    }

    @Override
    public Move makeMove(Board board) {

        if (this.opponent.getGamepieceSet().getPieces().size() == 20){
            this.weight = weightFirstPlayer;
        }else {
            this.weight = weightSecondPlayer;
        }

        if (isFirstMove) {
            isFirstMove = false;
            Location start;
            //Place F first as it has the most vertices
            Gamepiece gamepiece = new Gamepiece(getGamepieceSet().get("F"));
            if (this.getPlayerNo() == 0) {
                start = new Location(5,8);
                gamepiece.flipAlongY();
            } else {
                start = new Location(8,5);
                gamepiece.flipAlongY();
                gamepiece.rotateRight();
                gamepiece.rotateRight();
            }

            return new Move(
                    this,
                    gamepiece,
                    "F",
                    start);
        }

        else if(turn < 10){ //Use this algo for first 10 moves as it's a lot faster


            ArrayList<Integer> maxOpportunities = new ArrayList<>();
            Board possibleBoard;
            ArrayList<Move> moves = getPlayerMoves(this, board);
            moves = removeDuplicates(moves);
            moves = sortbyRubiks(moves);
            for (int i = 0; i < moves.size(); i++) {

                int currentMoveNet = 0;
                possibleBoard = new Board(board);
                possibleBoard.makeMove(moves.get(i));

                //Move net that uses the best criteria we found
                currentMoveNet += getPlayerMoves(this, possibleBoard).size();
                currentMoveNet += ((checkVertexes(moves.get(i),possibleBoard,true) + checkVertexes(moves.get(i),possibleBoard,false))) * weight[0];
                currentMoveNet += moves.get(i).getGamepiece().getLocations().length;
                currentMoveNet += getRubiksLength(moves.get(i).getGamepieceName()) * weight[1];
                ArrayList<Move> opponentMoves = getPlayerMoves(this.opponent, possibleBoard);
                opponentMoves = removeDuplicates(opponentMoves);
                currentMoveNet -= opponentMoves.size();
                maxOpportunities.add(currentMoveNet);

            }

            int maxIndex = 0;
            for (int i = 1; i < maxOpportunities.size(); i++) {
                if (maxOpportunities.get(i) > maxOpportunities.get(maxIndex)) {
                    maxIndex = i;
                }
            }
            turn++;
            return moves.get(maxIndex);
        }
        else {
            ArrayList<Move> moves = getPlayerMoves(this, board);
            moves = removeDuplicates(moves);
            moves = sortbyRubiks(moves);
            ArrayList<Move> opponentMoves;

            Board possibleBoard = new Board(board);
            Board possibleBoardOpponent;

            ArrayList<Integer> maxOpportunities = new ArrayList<>();
            for (int i = 0; i < moves.size(); i++) {                            //for all of my moves
                int opponentMovesMaxIndex = 0;
                int opponentMinimize;
                possibleBoard.makeMove(moves.get(i));                           //I make move
                opponentMoves = getPlayerMoves(this.opponent, possibleBoard);   //they find their possible moves
                opponentMoves = removeDuplicates(opponentMoves);
                opponentMoves = sortbyRubiks(opponentMoves);

                int currentMoveNet = 0;
                possibleBoard = new Board(board);
                possibleBoard.makeMove(moves.get(i));

                //Add move net to move evaluation
                currentMoveNet += getPlayerMoves(this, possibleBoard).size();
                currentMoveNet += ((checkVertexes(moves.get(i),possibleBoard,true) + checkVertexes(moves.get(i),possibleBoard,false))) * 50;
                currentMoveNet += moves.get(i).getGamepiece().getLocations().length;
                currentMoveNet += getRubiksLength(moves.get(i).getGamepieceName()) * 20;
                opponentMoves = removeDuplicates(opponentMoves);
                currentMoveNet -= opponentMoves.size();

                if (!opponentMoves.isEmpty()){
                    possibleBoardOpponent = new Board(possibleBoard);
                    possibleBoardOpponent.makeMove(opponentMoves.get(opponentMovesMaxIndex));
                    opponentMinimize = getPlayerMoves(this, possibleBoardOpponent).size();

                    for (int j = 1; j < opponentMoves.size() / 70; j++) {                                   //find their move that minimizes our moves
                        int temp;
                        possibleBoardOpponent = new Board(possibleBoard);
                        possibleBoardOpponent.makeMove(opponentMoves.get(j));
                        temp = getPlayerMoves(this,possibleBoardOpponent).size();    //get my possible moves

                        if (temp < opponentMinimize) {
                            opponentMovesMaxIndex = j;
                            opponentMinimize = temp;
                        }


                    }

                    Board stepTwoBoard = new Board(possibleBoard);
                    stepTwoBoard.makeMove(opponentMoves.get(opponentMovesMaxIndex));
                    maxOpportunities.add((getPlayerMoves(this,stepTwoBoard).size())*30 + currentMoveNet*10);
                    possibleBoard = new Board(board);
                }
            }

            //Find our best move
            int maxIndex = 0;
            for (int i = 1; i < maxOpportunities.size(); i++) {
                if (maxOpportunities.get(i) > maxOpportunities.get(maxIndex)) {
                    maxIndex = i;
                }
            }
            turn++;
            return moves.get(maxIndex);
        }
    }

    private int checkVertexes(Move move, Board board, boolean self) {
        int vertexes = 0;
        int xTarget, yTarget;

        for (int i = 0; i < move.getGamepiece().getLocations().length; i++) {
            xTarget = move.getGamepiece().getLocations()[i].getX();
            yTarget = move.getGamepiece().getLocations()[i].getY();
            //construct
            if (self) {
                if (inBoard(xTarget+1,yTarget+1) && !board.isOccupied(xTarget+1,yTarget+1)) {
                    vertexes++;
                }
                if (inBoard(xTarget+1,yTarget-1) && !board.isOccupied(xTarget+1,yTarget-1)) {
                    vertexes++;
                }
                if (inBoard(xTarget-1,yTarget+1) && !board.isOccupied(xTarget-1,yTarget+1)) {
                    vertexes++;
                }
                if (inBoard(xTarget-1,yTarget-1) && !board.isOccupied(xTarget-1,yTarget-1)) {
                    vertexes++;
                }
            }
            //destruct
            else {
                if (inBoard(xTarget+1,yTarget+1) && board.isOccupied(xTarget+1,yTarget+1) &&
                        board.getOccupyingPlayer(xTarget+1,yTarget+1) != move.getPlayer().getPlayerNo()) {

                    vertexes++;
                }
                if (inBoard(xTarget+1,yTarget-1) && board.isOccupied(xTarget+1,yTarget-1) &&
                        board.getOccupyingPlayer(xTarget+1,yTarget-1) != move.getPlayer().getPlayerNo()) {

                    vertexes++;
                }
                if (inBoard(xTarget-1,yTarget-1) && board.isOccupied(xTarget-1,yTarget-1) &&
                        board.getOccupyingPlayer(xTarget-1,yTarget-1) != move.getPlayer().getPlayerNo()) {

                    vertexes++;
                }
                if (inBoard(xTarget-1,yTarget+1) && board.isOccupied(xTarget-1,yTarget+1) &&
                        board.getOccupyingPlayer(xTarget-1,yTarget+1) != move.getPlayer().getPlayerNo()) {

                    vertexes++;
                }
            }
        }

        return vertexes;
    }

    public int getRubiksLength(String gamepiece){
        HashMap<String, Integer> rubiksLength = new HashMap<String, Integer>();
        rubiksLength.clear();
        rubiksLength.put("I1", 2);
        rubiksLength.put("I2", 3);
        rubiksLength.put("I3", 4);
        rubiksLength.put("I4", 5);
        rubiksLength.put("I5", 6);
        rubiksLength.put("V3", 4);
        rubiksLength.put("L4", 5);
        rubiksLength.put("Z4", 5);
        rubiksLength.put("O4", 4);
        rubiksLength.put("L5", 6);
        rubiksLength.put("T5", 5);
        rubiksLength.put("V5", 6);
        rubiksLength.put("N", 6);
        rubiksLength.put("Z5", 6);
        rubiksLength.put("T4", 4);
        rubiksLength.put("P", 5);
        rubiksLength.put("W", 6);
        rubiksLength.put("U", 5);
        rubiksLength.put("F", 5);
        rubiksLength.put("X", 4);
        rubiksLength.put("Y", 5);

        return rubiksLength.get(gamepiece);

    }
    private Integer[][] locationToArray(Location[] locations, Location location) {
        Integer[][] array = new Integer[locations.length+1][2];

        for (int i = 0; i < locations.length; i++) {
            array[i][0] = locations[i].getX();
            array[i][1] = locations[i].getY();
        }

        array[locations.length][0] = location.getX();
        array[locations.length][1] = location.getY();


        return array;
    }
    public ArrayList<Move> removeDuplicates(ArrayList<Move> moves) {
        HashMap<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < moves.size(); i++) {
            Integer[][] currentMoveArray = locationToArray(moves.get(i).getGamepiece().getLocations(), moves.get(i).getLocation());
            if (!result.containsKey(Arrays.deepHashCode(currentMoveArray))) {
                result.put(Arrays.deepHashCode(currentMoveArray), i);
            }
        }

        ArrayList<Move> output = new ArrayList<>();
        for (Integer value : result.values()) {
            output.add(moves.get(value));
        }

        return output;
    }

    public  ArrayList<Move> sortbyRubiks(ArrayList<Move> moves){

        ArrayList<Move> sortedMoves = new ArrayList<>();

        while (!moves.isEmpty()) {
            Move currentMax = moves.get(0);
            for (int i = 0; i < moves.size(); i++) {

                if (getRubiksLength(currentMax.getGamepieceName()) < getRubiksLength(moves.get(i).getGamepieceName())){
                    currentMax = moves.get(i);
                }
            }
            sortedMoves.add(currentMax);
            moves.remove(currentMax);
        }

        return sortedMoves;
    }


    public boolean inBoard(int x, int y) {
        return x >= 0 && x <= 13 && y >= 0 && y <= 13;
    }
}