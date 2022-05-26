package AloneWolfsBot;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.*;

public class AloneWolfsBotPlayer  extends SimpleBotPlayer {
    HashMap<String, Integer> NameToPoints = new HashMap<String, Integer>();
     public void InitPoitsToPiece (){
         GamepieceSet pieces = new GamepieceSet(3);
         Map<String, Gamepiece> pieceMap = pieces.getPieces();
         int i = 0;
         for (Map.Entry<String, Gamepiece> entry : pieceMap.entrySet()) {
             int point = i;
             String pieceName = entry.getKey();
             if (Objects.equals(pieceName, "F")){
                 point+=3;
             }
            else if (Objects.equals(pieceName, "X")){
                 point+=3;
             }
             else if (Objects.equals(pieceName, "W")){
                 point+=3;
             }

            else if (Objects.equals(pieceName, "N")){
                 point+=2;
             }
             else if (Objects.equals(pieceName, "Y")){
                 point+=2;
             }
             else if (Objects.equals(pieceName, "V")){
                 point+=1;
             }
             else if (Objects.equals(pieceName, "Z")){
                 point+=1;
             }
             NameToPoints.put(pieceName,point);
             i++;
         }
    }

    private HashMap<Move, Double> assignMoveValue(Board board, ArrayList<Move>  moves){
        HashMap<Move, Double> MoveToPoint = new HashMap<>();
        for (Move move: moves
        ) {
            double point=NameToPoints.get(move.getGamepieceName());
            point+=countPointsForLocalisation(board,move);
            MoveToPoint.put(move,point);
        }

        return MoveToPoint;
    }
    private double getDistance(Location start, Location end){
         return Math.sqrt((start.getX()-end.getX())*(start.getX()-end.getX()) + (start.getY()-end.getY())*(start.getY()-end.getY()));
    }
    private double countPointsForLocalisation(Board board, Move move){
        Location startLoc;
        if(this.getPlayerNo() ==1){
            startLoc  = Board.startLocations[0];
        }
        else{
            startLoc  = Board.startLocations[1];
        }
        Location futhureLoc = move.getLocation();
        return getDistance(startLoc, futhureLoc);
    }
    public AloneWolfsBotPlayer(int playerNo) {
        super(playerNo);
        InitPoitsToPiece();
    }

    @Override
    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("L4")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        }
        ArrayList<Move> moves = getPlayerMoves(this, board);
        HashMap<Move, Double> MoveValue = assignMoveValue(board,moves);
        Move move = moves.get(0);
        Double max= MoveValue.get(move);

            for (Map.Entry<Move, Double> tuple: MoveValue.entrySet()){
                if (tuple.getValue()>= max){
                    move = tuple.getKey();
                    max = tuple.getValue();
                }
            }
        return move;
    }


}
