package ApplePlus;

/**
 * Team ApplePlus
 * Members: Ao Peng     20202688
 *          Oscar Yeoh  20403662
 *          KarYen Yap  20202149
 *
 * Bot class
 *  - implementation of a smarter version of SimpleBotPlayer
 */

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.ArrayList;
import java.util.List;

public class ApplePlusBot extends SimpleBotPlayer {
    // Game Tree implementation
    protected static class Node<T> {
        private List<Node<T>> children = new ArrayList<Node<T>>();
        private Node<T> parent = null;
        private T data = null;
        private double value = 0.0;

        public Node(T data, double value) {
            this.data = data;
            this.value = value;
        }

        public Node(T data, Node<T> parent) {
            this.data = data;
            this.parent = parent;
        }

        public T getData() {
            return data;
        }

        public double getValue() {
            return value;
        }

        public Node<T> getParent() {
            return parent;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public void setData(T data, double value) {
            this.data = data;
            this.value = value;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public void addChild(Node<T> child) {
            child.setParent(this);
            this.children.add(child);
        }

        public void addChild(T data, double value) {
            this.addChild(new Node<T>(data, value));
        }

        public void addChildren(List<Node<T>> children) {
            for (Node<T> node : children) {
                node.setParent(this);
            }
            this.children.addAll(children);
        }

        public boolean isLeafNode() {
            return this.children.isEmpty();
        }
    }

    Node<Move> optimal = null;
    private double builderWeight = 0.383841621015268;
    private double blockerWeight = 0.23846851026921964;
    private double bigPieceWeight = 0.37768986871551236;

    public ApplePlusBot(int playerno) {
        super(playerno);
    }

    public ApplePlusBot(int playerno, double builderWeight, double blockerWeight, double bigPieceWeight) {
        super(playerno);
        this.builderWeight = builderWeight;
        this.blockerWeight = blockerWeight;
        this.bigPieceWeight = bigPieceWeight;
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
            ArrayList<Move> moves = getPlayerMoves(this, board);
            return getOptimalMove(moves);
        }
    }

    /**
     * Iterates through bot's moves and gets the best move
     *
     * @param moves the bot's available moves
     * @return optimal move
     */
    private Move getOptimalMove(ArrayList<Move> moves) {
        Node<Move> root = new Node<Move>(null, 0.0);

        // Temporary board to make the moves of current player
        //Board possible = new Board(board);

        for (int i = 0; i < moves.size(); i++) {
            // Get the grade point for each possible move 
            double point = gradeMove(moves.get(i), moves.size(), board);
            root.addChild(moves.get(i), point);

            /**
             * The algorithm should check for the next few moves done by both player and pick the optimal
             * move that the current player can make. However, the execution time is longer than we expected
             * since the size of the list is too large. So we decided to reduce the deptth to be searched in
             * the game tree to increase the efficiency of the algorithm.
             * 
             * Below is the pseudocode to evaluate the moves after few game turns. Each move is made to obtain 
             * the possible moves of opponent player. A game tree is built with the possible moves of opponent player
             * is the children of each move of current player. Then, the moves are graded and passed into minimax
             * algorithm to look for the optimal move can be make by the current player.
             */
            /*root.addChild(moves.get(i), 0.0);
            possible.makeMove(moves.get(i));
            ArrayList<Move> opMove = getPlayerMoves(this.opponent, possible);
            Board possible2 = new Board(possible);
            for (int j = 0; j < opMove.size(); j++) {
                List<Node<Move>> child = root.getChildren();
                child.get(i).addChild(opMove.get(j), 0.0);
                ArrayList<Move> curMove = getPlayerMoves(this, possible2);
                for (int k = 0; k < curMove.size(); k++) {
                    List<Node<Move>> grandchild = child.getChildren();
                    double point = gradeMove(curMove.get(k), curMove.size(), possible2);
                    grandchild.get(k).addChild(curMove.get(k), point);
                }
            }*/
        }

        // Obtain the optimal node
        Node<Move> optimal = minimax(root, 1, Double.MIN_VALUE, Double.MAX_VALUE, true);

        return optimal.getData();
    }

    /**
     * Calculates how good the move is and assigns it points based on how good the move is.
     *
     * @param move move to be graded
     * @return the points the move got
     */
    public double gradeMove(Move move, int numMoves, Board b) {
        return builder(move, numMoves, b) * builderWeight + blocker(move, b) * blockerWeight + bigPiece(move) * bigPieceWeight;
    }

    /**
     * Grades move based on the value of its size in the current stage of the game
     *
     * @param move move to be graded
     * @return the points the move got
     */
    private double bigPiece(Move move) {
        return (double) move.getGamepiece().getLocations().length / this.getGamepieceSet().getPieces().size();
    }

    /**
     * Grades move based on how many growth points it creates
     *
     * @param move move to be graded
     * @param numMovesBefore the number of moves before this move is made
     * @param b the current board state
     * @return the points the move got
     */
    private int builder(Move move, int numMovesBefore, Board b) {
        Board possibleBoard = new Board(b);
        possibleBoard.makeMove(move);

        //Remove piece since this piece is used in order to get the possible board
        getGamepieceSet().remove(move.getGamepieceName());
        //Check number of possible moves after piece is placed
        int numMovesAfter = getPlayerMoves(this, possibleBoard).size();
        //Put piece back into player's set
        getGamepieceSet().getPieces().put(move.getGamepieceName(), move.getGamepiece());

        return numMovesAfter - numMovesBefore;
    }

    /**
     * Grades move based on the number of the opponent's growth points the move obstructs
     *
     * @param move move to be graded
     * @param b the current board state
     * @return the points the move got
     */
    private int blocker(Move move, Board b) {
        Board possibleBoard = new Board(b);
        possibleBoard.makeMove(move);

        int before = getPlayerMoves(this.opponent, b).size();
        int after = getPlayerMoves(this.opponent, possibleBoard).size();

        return before - after;
    }

    /**
     * A searching function which implements alpha-beta pruning algorithm 
     * that allows the program searches much faster.
     * 
     * @param point Node of game tree
     * @param depth The depth to search in game tree
     * @param alpha The best value that the maximizer currently can guarantee at that level or above
     * @param beta The best value that the minimizer currently can guarantee at that level or above
     * @param maximizingPlayer Check if the current player is maxiziming or minimizing player
     * @return The optimal node
     */
    private Node<Move> minimax(Node<Move> point, int depth, double alpha, double beta, boolean maximizingPlayer) {
        Node<Move> optimal = null;

        // Return the current node if it is a leaf node
        if (depth == 0 || point.isLeafNode()) { 
            return point;
        }

        if (maximizingPlayer) {
            double max = -10000;

            // Recur for all the children of the node
            for (Node<Move> n : point.getChildren()) {
                double value = minimax(n, depth - 1, alpha, beta, false).value;
                max = Math.max(max, value);

                // Update the optimal node
                if (max == value) {
                    optimal = n;
                }

                // Alpha beta pruning
                if (max >= beta) {
                    break;
                }
                alpha = Math.max(alpha, max);
            }

            return optimal;
        } else {
            double min = Double.MAX_VALUE;

            for (Node<Move> n : point.getChildren()) {
                double value = minimax(n, depth - 1, alpha, beta, true).value;
                min = Math.min(min, value);
                if (min == value) {
                    optimal = n;
                }
                if (min <= alpha) {
                    break;
                }
                beta = Math.min(beta, min);
            }

            return optimal;
        }
    }

    /*private void findMedian(ArrayList<Double> pointList, ArrayList<Move> moves) {
        sort(pointList, moves, 0, pointList.size() - 1);
        System.out.println("a" + pointList.size() + " " + moves.size());

        int quartile = pointList.size() * 25 / 100;

        for (int i = 0, j = pointList.size() - 2; i < quartile; i++, j-=2) {
            pointList.remove(0);
            pointList.remove(j);
            moves.remove(0);
            moves.remove(j);
        }

        System.out.println("b" + pointList.size() + " " + moves.size());
    }

    private void merge(ArrayList<Double> points, ArrayList<Move> moves, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        ArrayList<Double> leftPoint = new ArrayList<>();
        ArrayList<Double> rightPoint = new ArrayList<>();
        ArrayList<Move> leftMove = new ArrayList<>();
        ArrayList<Move> rightMove = new ArrayList<>();

        for (int i = 0; i < n1; i++) {
            leftPoint.add(points.get(left + i));
            leftMove.add(moves.get(left + i));
        }
        for (int j = 0; j < n2; j++) {
            rightPoint.add(points.get(mid + 1 + j));
            rightMove.add(moves.get(mid + 1 + j));
        }

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (leftPoint.get(i) <= rightPoint.get(j)) {
                points.set(k, leftPoint.get(i));
                moves.set(k, leftMove.get(i));
                i++;
            } else {
                points.set(k, rightPoint.get(j));
                moves.set(k, rightMove.get(j));
                j++;
            }
            k++;
        }

        while (i < n1) {
            points.set(k, leftPoint.get(i));
            moves.set(k, leftMove.get(i));
            i++;
            k++;
        }

        while (j < n2) {
            points.set(k, rightPoint.get(j));
            moves.set(k, rightMove.get(j));
            j++;
            k++;
        }
    }

    private void sort(ArrayList<Double> points, ArrayList<Move> moves, int left, int right) {
        if (left < right) {
            int mid = (right + left) / 2;

            sort(points, moves, left, mid);
            sort(points, moves, mid + 1, right);

            merge(points, moves, left, mid, right);
        }
    }*/
}
