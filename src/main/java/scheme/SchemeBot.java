package scheme;
//Team scheme

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Stack;

import model.*;

import SimpleBot.SimpleBotPlayer;

public class SchemeBot extends SimpleBotPlayer{
    Weights w = new Weights();
    private static final boolean OPPONENT_IS_INTELLIGENT = true;        //will opponent make best move
    
    public SchemeBot(int playerNo) {
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
            //String[] save = generatePieceString(getGamepieceSet());
            PositionNode root = new PositionNode(null, board, w);       //generates a dimensional arraylist that stores future positions
            //RestoreGamePieces(save, getGamepieceSet(), getPlayerNo());

            //System.out.println(root.traverse());
            Move bestMove = root.findBestMove();
            //System.out.println(bestMove.getGamepieceName() + " (" + bestMove.getLocation().getX() + "," + bestMove.getLocation().getY() + ")");
            return bestMove;
        }
    }

    private String[] generatePieceString(GamepieceSet toSave) {             //creates a string[] of the pieces that have been removed in order to re-remove them later
        Set<String> set = toSave.getPieces().keySet();
        Set<String> all = new GamepieceSet(3).getPieces().keySet();
        all.removeIf(s -> set.contains(s));
        Object[] allA = all.toArray();
        String[] saved = new String[allA.length];
        for(int i=0;i<allA.length;i++) {
            saved[i] = (String)allA[i];
        }
        return saved;
    }

    private void RestoreGamePieces(String[] previous, GamepieceSet toRestore, int playerNo) {       //resets the players pieces, then restores the previous pieces they had
        toRestore.initialise(playerNo);
        for(String s : previous) {
            toRestore.remove(s);
        }
    }

    public ArrayList<Move> getPlayerMoves(Player player, Board board) {
        return super.getPlayerMoves(player, board);
    }

    public ArrayList<Move> getMovesWithGivenGamepiece(Gamepiece gamepiece, String gamepieceName, Player player, Board board){
        return super.getMovesWithGivenGamepiece(gamepiece, gamepieceName, player, board);
    }

    public ArrayList<Move> getMovesWithGivenOrientation(Gamepiece piece, String gamepieceName, Player player, Board board) {
        return super.getMovesWithGivenOrientation(piece, gamepieceName, player, board);
    }

    class Weights {                                 //weights store the importance of each feature
        public float piecesOnCornerWeight = 1;
        public float turnNumberWeight = 5;
        public float currentPointsWeight = 0.1f;
    }

    class PositionNode implements Comparable<PositionNode>{
        static final int MAX_LOOKAHEAD = 3;                                               //the max number of moves to look ahead
        private ArrayList<PositionNode> futurePositions = new ArrayList<PositionNode>();    //the positions the player can get to from this board sorted by score
        private float score;                                                                //the chance of the player winning from this board
        private Move move;                                                                  //the move that will take you to this board from the previous board

        public PositionNode(Move m, Board board, Weights weights) {
            this(new Stack<String[]>(),m, board, 0, weights);
        }
    
        private PositionNode(Stack<String[]> stack, Move prevMove, Board board, int depth, Weights weights) {
            Player current = depth % 2 == 0 ? SchemeBot.this : opponent;
            this.move = prevMove;
            score = findScore(board, depth, weights, prevMove);
            if(depth >= MAX_LOOKAHEAD) return;
            if(current.equals(opponent) && OPPONENT_IS_INTELLIGENT) {
                findWorstFuturePosition(stack, board, depth, weights, current);     //if the oppenent is smart, they will pick the position that gives us the worst score
            } else {
                recurseFuturePositions(stack, board, depth, weights, current);      //else check all positions
            }
        }

        private void findWorstFuturePosition(Stack<String[]> stack, Board board, int depth, Weights weights, Player current) {
            ArrayList<Move> moves = getPlayerMoves(current, board);
            float min = 2;
            Move m = null;
            for (Move nextMove : moves) {               //find worst position for bot
                Board newBoard = new Board(board);
                newBoard.makeMove(nextMove);
                float check = findScore(newBoard, depth+1, weights, nextMove);
                if(check < min) {
                    min = check;
                    m = nextMove;
                } 
            }
            if(m==null) return;     
            stack.push(generatePieceString(current.getGamepieceSet()));     //make that move as the opponent
            Board newBoard = new Board(board);
            newBoard.makeMove(m);
            current.getGamepieceSet().remove(m.getGamepieceName());
            addPosition(new PositionNode(stack, m, newBoard, depth + 1, weights));
            RestoreGamePieces(stack.peek(), current.getGamepieceSet(), current.getPlayerNo()); 
            stack.pop();
        }

        private void recurseFuturePositions(Stack<String[]> stack, Board board, int depth, Weights weights, Player current) {       //recurses throught all possible positions 
            ArrayList<Move> moves = getPlayerMoves(current, board);
            stack.push(generatePieceString(current.getGamepieceSet()));
            for (Move nextMove : moves) {
                RestoreGamePieces(stack.peek(), current.getGamepieceSet(), current.getPlayerNo()); 
                Board newBoard = new Board(board);
                newBoard.makeMove(nextMove);
                current.getGamepieceSet().remove(nextMove.getGamepieceName());
                addPosition(new PositionNode(stack, nextMove, newBoard, depth + 1, weights));
            }
            RestoreGamePieces(stack.peek(), current.getGamepieceSet(), current.getPlayerNo()); 
            stack.pop();
        }
    
        private float findScore(Board b, int d, Weights w, Move m) {  //calculates the chance of winning for SchemeBot.this
            if (m == null){
                return 1;
            }
            float total = 0;
            float myTurn = (d % 2 == 0) ? 1 : 0;    //opponents corners shouldnt be considered for bots chance of winning

            float cornersOnPiece = move.getGamepiece().getLocations().length;
            float cornersSum = cornersOnPiece * w.piecesOnCornerWeight * myTurn;
            float turn = 21 - SchemeBot.this.getGamepieceSet().getPieces().size();
            float turnSum = turn * w.turnNumberWeight;
            float currentPoints = SchemeBot.this.playerScore() * w.currentPointsWeight;

            total += turnSum + currentPoints + cornersSum;
            total /= 100;                               //makes the weight more reasonable as tanh tails off fast
            total = (float) Math.tanh(total);           //-inf,+inf -> -1,+1
            return total;
        }

        public Move findBestMove() {                            //min maxes to find the best move
            float best = -1;
            Move m = null;
            for(PositionNode p : futurePositions) {
                float check = p.findBestMoveOther(1);
                if(check > best) {
                    m = p.move;
                    best = check;
                }
            }
            return m;
        }

        private float findBestMoveThis(int depth) {        //player should choose move that leads to the best score assuming opponent plays perfectly
            if(depth >= MAX_LOOKAHEAD || futurePositions.isEmpty()) return score;
            float best = -1;
            for(PositionNode p : futurePositions) {
                float check = p.findBestMoveOther(depth+1);
                if(check > best) {
                    best = check;
                }
            }
            return best;
        }
        
        private float findBestMoveOther(int depth) {         //opponent should choose move with the worst score for the player
            if(depth >= MAX_LOOKAHEAD || futurePositions.isEmpty()) return score;
            return futurePositions.get(0).findBestMoveThis(depth+1);            
        }
    
        private void addPosition(PositionNode p) {                   //inserts the position into the future positions
            int index = Collections.binarySearch(futurePositions, p);
            futurePositions.add(index<0 ? -index - 1 : index, p);
        }
    
        public int compareTo(PositionNode other) {      
            if(other.score > this.score) return 1;
            if(other.score < this.score) return -1;
            return 0;
        }

        public String traverse() {                      //traverses all the positions
            StringBuilder s = new StringBuilder();
            traverse(0, s);
            return s.toString();
        }

        private void traverse(int depth, StringBuilder s) {
            for(int i=0;i<depth;i++) s.append("\t");
            String moveString = move == null ? "root" : move.getGamepieceName() + " (" + move.getLocation().getX() + "," + move.getLocation().getY() + ")";
            String playerString = (depth % 2 == 1 || depth == 0) ? SchemeBot.this.getName() : opponent.getName();
            s.append(playerString + "\t" + moveString + "\t" + score + "{");
            if(futurePositions.isEmpty()) s.append("}\n");
            else {
                s.append("\n");
                for (PositionNode positionNode : futurePositions) {
                    positionNode.traverse(depth+1, s);
                }
                for(int i=0;i<depth;i++) s.append("\t");
                s.append("}\n");
            }
        }
    }
}


