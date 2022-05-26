package enoughToPass;

import SimpleBot.SimpleBotPlayer;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class EnoughToPassPlayer extends SimpleBotPlayer {

    public EnoughToPassPlayer(int playerNo) {
        super(playerNo);
    }

    @Override
    public Move makeMove(Board board) {
        ArrayList<Node> moveNodes = new ArrayList<>();
        ArrayList<Move> moves = getPlayerMoves(this, board);
        for(int i = 0; i < moves.size(); i++){
            moveNodes.add(new Node(moves.get(i)));
        }

        if (isFirstMove) {
            isFirstMove = false;
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        } else {

            Board possibleBoard = new Board(board);

            Node bestPiece = moveNodes.get(0);

            for(int i = 1; i < moveNodes.size(); i++) {
                //fights towards player to take up as much of their space as possible and free more of their space
                if (getPlayerNo() == 1) {
                    moveNodes.get(i).addToWeight(moveNodes.get(i).getMove().getLocation().getX());

                    moveNodes.get(i).addToWeight(13 - moveNodes.get(i).getMove().getLocation().getY());

                    if (moveNodes.get(i).getWeight() > bestPiece.weight) {
                        bestPiece = moveNodes.get(i);

                    }
                }
                else{
                    moveNodes.get(i).addToWeight(13 - moveNodes.get(i).getMove().getLocation().getX());

                    moveNodes.get(i).addToWeight(moveNodes.get(i).getMove().getLocation().getY());

                    if (moveNodes.get(i).getWeight() > bestPiece.weight) {
                        bestPiece = moveNodes.get(i);

                    }
                }
            }
            System.out.println(bestPiece.getMove().getLocation().toString() + bestPiece.getMove().getGamepieceName());

            return bestPiece.getMove();
        }
    }


    public class Node
    {
        private List<Node> children = null;

        public Move getMove() {
            return move;
        }

        public int getWeight() {
            return weight;
        }



        private Move move;

        public void addToWeight(int weightAdd) {
            this.weight = this.weight += weightAdd;
        }

        private int weight = 0;

        public Node(Move move)
        {
            this.move = move;
            this.children = new ArrayList<>();
            this.weight = move.getGamepiece().getLocations().length * 13;
        }

        public void addChild(Node child)
        {
            children.add(child);
        }

    }


}