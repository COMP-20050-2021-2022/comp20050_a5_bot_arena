// Team Random12
// Adam Goodwin 18322006
package Random12;


import SimpleBot.SimpleBotPlayer;
import model.Move;

import java.util.ArrayList;

import static model.Board.HEIGHT;
import static model.Board.WIDTH;

public class Search {
    ScoreNode bestMove;
    int posEvald = 0;
    int totalMoves = 0;
    int breakCounter = 0;
    int INF = 999999999;
    double runningTime;
    double speed;
    long timeB;
    long timeA;
    float approxCompletionToken;
    float approxCompletionNumber;
    private final int depth = 2;
    private final int turn;


    public Search(Position position, int turn) {
        this.turn = turn;
        this.timeA = System.nanoTime();
        this.approxCompletionToken = 0f;
        this.approxCompletionNumber = (float) ((AlphaBetaBot) position.us).getPlayerMoves(position.board).size();
        this.bestMove = minimax(position, depth, -INF, INF);
        this.timeB = System.nanoTime();
        this.runningTime = (timeB - timeA) / (1E9);
        this.speed = posEvald / runningTime;

    }

    private int Evaluate(Position p) { //To be implemented by Nayana
        posEvald++;
        if (turn < 5) {
            return controlDiagonal(p);
        } else {
            return maximiseSpace(p);
        }

    }


    private ArrayList<Move> orderMoves(ArrayList<Move> moves) { //to be implemented by Darren
        return moves;
    }


    private int evalNumPositions(Position p) { // Approx 227 Evals/sec @ depth 2, Approx 375 Evals/sec @ depth 1
        // Approx 35sec @ depth 1
        //Way too slow, needs either adapting or changing.
        return ((AlphaBetaBot) p.us).getPlayerMoves(p.board).size() - ((AlphaBetaBot) p.opponent).getPlayerMoves(p.board).size();
    }

    public int controlDiagonal(Position p) { //Approx 144690 Evals/sec @ depth 3,  Approx 14000 Evals/sec @ depth 2, Approx 14637 Evals/sec @ depth 1
        //Approx 270 sec @ depth 3,  Approx 12 sec @ depth 2, Approx <1 sec @ depth 1
        //Very fast, play first few moves with this
        int score = 0;
        for (int y = (HEIGHT - 1); y >= 0; y--) {
            for (int x = 0; x < WIDTH; x++) {
                if (!(p.board.isOccupied(x, y))) {
                    continue;
                } else if (p.board.getOccupyingPlayer(x, y) == p.us.getPlayerNo()) {
                    score += 14 - Math.abs(x - y);
                } else {
                    score -= 14 - Math.abs(x - y);
                }
            }
        }
        return score;
    }

    public int maximiseSpace(Position p) {
        int score = 0;
        for (int y = (HEIGHT - 1); y >= 0; y--) {
            for (int x = 0; x < WIDTH; x++) {
                if (!(p.board.isOccupied(x, y))) {
                    continue;
                } else {
                    if (p.board.getOccupyingPlayer(x, y) == p.us.getPlayerNo()) {
                        score++;
                    } else {
                        score--;
                    }
                }
            }
        }
        return score;
    }

    public ScoreNode minimax(Position p, int depth, int alpha, int beta) {
        if (depth == this.depth - 1) {
            approxCompletionToken++;
            //System.out.println("Eval stats: PosEvald " + posEvald + " TotalMoves " + totalMoves + " TotalBreaks " + breakCounter);
            //System.out.println("Time stats: Running Time " + (System.nanoTime() - timeA) / (1E9) + " Speed " + posEvald/((System.nanoTime() - timeA) / (1E9)));
            //System.out.print("Bot is thinking: " + (int) (100 * approxCompletionToken / approxCompletionNumber) + "% |" +
            //        "=".repeat((int) (60 * approxCompletionToken / approxCompletionNumber)) + ">" +
            //" ".repeat((int) (60 * (approxCompletionNumber - approxCompletionToken) / approxCompletionNumber)) + "| " +
            //        "Time remaining:" + (float) (((approxCompletionNumber / approxCompletionToken) - 1d) * (System.nanoTime() - this.timeA) / (1E9)) + " seconds \r");
        }
        if (depth == 0) {
            return new ScoreNode(Evaluate(p), null);
        }
        if (p.maximisingPlayer) {
            ScoreNode maxNode = new ScoreNode(-INF, null);
            ArrayList<Move> moves = ((AlphaBetaBot) p.us).getPlayerMoves(p.board);
            if (moves.size() == 0) {
                return new ScoreNode(Evaluate(p), null);
            }
            totalMoves += moves.size();
            moves = orderMoves(moves);
            for (Move move : moves) {
                Position childPos = p.makeChild(move);
                ScoreNode node = minimax(childPos, depth - 1, alpha, beta);
                p.unmakeChild(move);
                node.setMove(move);
                if (node.getScore() > maxNode.getScore()) {
                    maxNode = node;
                }
                if (node.getScore() > alpha) {
                    alpha = node.getScore();
                }
                if (beta <= alpha) {
                    breakCounter++;
                    break;
                }
            }
            return maxNode;
        } else {
            ScoreNode minNode = new ScoreNode(INF, null);
            ArrayList<Move> moves = ((SimpleBotPlayer) p.opponent).getPlayerMoves(p.opponent,p.board);
            if (moves.size() == 0) {
                return new ScoreNode(Evaluate(p), null);
            }
            totalMoves += moves.size();
            moves = orderMoves(moves);
            for (Move move : moves) {
                Position childPos = p.makeChild(move);
                ScoreNode node = minimax(childPos, depth - 1, alpha, beta);
                p.unmakeChild(move);
                node.setMove(move);
                if (node.getScore() < minNode.getScore()) {
                    minNode = node;
                }
                if (node.getScore() < beta) {
                    beta = node.getScore();
                }
                if (beta <= alpha) {
                    breakCounter++;
                    break;
                }
            }
            return minNode;
        }
    }
}







