package SmarterBot;

import model.Move;

public class ScoredMove {
    private Move move;
    private float score;

    ScoredMove() {
    }

    ScoredMove(Move move, float score) {
        setMove(move);
        setScore(score);
    }

    ScoredMove(ScoredMove scoredMove) {
        setScore(scoredMove.getScore());
        setMove(scoredMove.getMove());
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Move getMove() {
        return move;
    }

    public float getScore() {
        return score;
    }
}
