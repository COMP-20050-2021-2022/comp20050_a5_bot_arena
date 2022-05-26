package SmarterBot;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Move;
import model.Player;

import java.util.ArrayList;

public class SmarterBotPlayer extends SimpleBotPlayer {
    public SmarterBotPlayer(int playerNo) {
        super(playerNo);
    }

    @Override
    public Move makeMove(Board board) {

        //Generating positions by applying moves
        //  to a copy of the current board:
        //Board possibleBoard = new Board(board);
        //possibleBoard.makeMove(move);

        if (isFirstMove) {
            return super.makeMove(board);  // let's just re-use simple bot behaviour for the first move.
        } else {
            ScoredMove bestMove = getBestMove(this, board, 1);
            return bestMove.getMove();
        }
    }

    private ScoredMove getBestMove(Player player, Board board, int depth) {

        ArrayList<Move> possibleMoves = getPlayerMoves(player, board);
        ScoredMove result = new ScoredMove();

        // our algorithm ensures that when this method is called,
        // getPlayerMoves() returns at least one move
        do {
            // get next valid move from the list
            Move move = possibleMoves.remove(0);

            // create a copy of the current board and apply the move to it
            Board boardAfterMove = new Board(board);
            boardAfterMove.makeMove(move);

            // Let's now score the produced board
            float score;
            if (depth == 0) {
                // we are only looking one move ahead, so we score the resulting board as-is
                score = calculateBoardScore(boardAfterMove, this, opponent);
            } else {
                // get the best score (and move) for the boardAfterMove and depth-1
                Player subsequentPlayer = (player == this) ? opponent : this;
                if (boardAfterMove.playerHasMoves(subsequentPlayer)) {
                    ScoredMove bestSubsequentMove = getBestMove(subsequentPlayer, boardAfterMove, depth - 1);
                    score = bestSubsequentMove.getScore();
                } else {
                    // the other player has no moves, so it will be our turn again
                    if (boardAfterMove.playerHasMoves(player)) {
                        ScoredMove bestSubsequentMove = getBestMove(player, boardAfterMove, depth - 1);
                        score = bestSubsequentMove.getScore();
                    } else {
                        // oops... none of the players have any further moves!
                        // This is an endgame position, so we just score it as-is
                        score = calculateBoardScore(boardAfterMove, this, opponent);
                    }
                }
            }

            if (result.getMove() == null) {
                // the result value is not initialised, which means that this is the first loop iteration.
                // We assume, for now, that the move we've just analysed is the best move!
                result.setScore(score);
                result.setMove(move);
            } else {
                if (player == opponent) {
                    // we are deciding the best move for the opponent, so pick the move with the minimal score
                    if (score < result.getScore()) {
                        result.setScore(score);
                        result.setMove(move);
                    }
                } else {
                    // player == this: we are deciding the best move for the bot, so pick the move with the maximal score
                    if (score > result.getScore()) {
                        result.setScore(score);
                        result.setMove(move);
                    }
                }
            }

        } while (!possibleMoves.isEmpty());

        return result;
    }

    private float calculateBoardScore(Board board, Player self, Player opponent) {
        // Here we calculate position score = w0 * f0 + w1 * f1 + ...
        // In this example the scoring formula = number of board squares covered by own color - number of board squares covered by the opponent's color
        return (1.0f * numberOfPlayerSquares(board,self.getPlayerNo()) + (-1.0f * numberOfPlayerSquares(board,opponent.getPlayerNo())));
    }

    private float numberOfPlayerSquares(Board board, int playerNo) {
        int result = 0;
        for(int y=0; y<Board.HEIGHT; y++) {
            for (int x=0; x<Board.WIDTH; x++) {
                if (board.isOccupied(x,y)) {
                    if (board.getOccupyingPlayer(x,y) == playerNo) {
                        result += 1;
                    }
                }
            }
        }
        return result;
    }
}
