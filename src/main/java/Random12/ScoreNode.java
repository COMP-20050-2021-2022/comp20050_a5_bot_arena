// Team Random12
// Adam Goodwin 18322006
package Random12;

import model.Move;

public class ScoreNode {
    int score;
    Move move;
    ScoreNode parent;

    public ScoreNode(int score, Move move){
        this.score = score;
        this.move = move;
    }

    public int getScore() {
        return score;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public ScoreNode getParent() {
        return parent;
    }

    public void setParent(ScoreNode parent) {
        this.parent = parent;
    }
}
