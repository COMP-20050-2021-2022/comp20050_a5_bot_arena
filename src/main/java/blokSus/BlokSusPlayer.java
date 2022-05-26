/*
 * Team Name: BlokSus
 * Member Names/Student Numbers: Ayomide Sola-Ayodele (20338061)
 *                               Boris Sandoval (20437374)
 *                               Wiktoria Szczepaniak (20461424)
 */

package blokSus;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Move;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class BlokSusPlayer extends SimpleBotPlayer {

  // Points for each block

  // How far ahead the bot will search, has to be an even number so that it ends up looking at the board
  // from the bots view.
  // Depth of to goes from bot->opponent->bot.
  public final static int HEIGHT = 2;

  // Set for readability.
  public final static int TOP = HEIGHT;
  public final static int OPPONENT_VIEW = 1;
  public final static int BOT_VIEW = -1;
  public final static int PLAYER_ONE = 0;
  public final static int PLAYER_TWO = 1;
  private Board possibleBoard;
  private final Random random;

  // Some moves may have the same evaluation score, all grouped together as hashtable entries
  Hashtable<Integer, ArrayList<Move>> movesRanked;
  ArrayList<Move> bestMoves;

  public BlokSusPlayer(int playerNo) {
    super(playerNo);
    random = new Random();
  }

  @Override
  public Move makeMove(Board board) {
    if (isFirstMove) {
      return super.makeMove(board);
    }
    // A board that we can test all moves on.
    possibleBoard = new Board(board);
    // Chooses a random move from all moves that have the highest evaluation score
    if (!board.playerHasMoves(opponent)) {
      ArrayList<Move> moves = getPlayerMoves(this, possibleBoard); // List of all possible moves that be performed on input board
      Board previousBoard = new Board(possibleBoard); // Needed to reset board after we test a move
      int maxPossibleMovesCount = 0;
      for (Move move: moves) {
        possibleBoard.makeMove(move);
        ArrayList<Move> newPossibleMoves = getPlayerMoves(this, possibleBoard);
        if (Integer.max(maxPossibleMovesCount, newPossibleMoves.size()) == newPossibleMoves.size()) { // Want to place as many of our pieces as possible
          maxPossibleMovesCount = newPossibleMoves.size();
          movesRanked.computeIfAbsent(maxPossibleMovesCount, k -> new ArrayList<>()); // creates new entry if key is not present
          movesRanked.get(maxPossibleMovesCount).add(move);
        }
        possibleBoard = new Board(previousBoard);
      }
      bestMoves = movesRanked.get(maxPossibleMovesCount);
    } else {
      movesRanked = new Hashtable<>();
      int maxEvaluation = search(HEIGHT, BOT_VIEW);
      bestMoves = movesRanked.get(maxEvaluation);
    }
    return bestMoves.get(random.nextInt(bestMoves.size()));

  }

  private int search(int height, int pointOfView) {
    if (height == 0) {
      // Finished looking ahead, now just need to see if the state of the board is favourable for our bot
      return evaluate(possibleBoard);
    }

    // Storing all possible moves.
    ArrayList<Move> moves = new ArrayList<>();
    if (pointOfView == BOT_VIEW) {
      moves = getPlayerMoves(this, possibleBoard); // List of all bot possible moves
    } else if (pointOfView == OPPONENT_VIEW) { // List of all possible opponent moves.
      moves = getPlayerMoves(opponent, possibleBoard);
    }
    if (moves.size() == 0) {
      return 0;
    }

    int maxEvaluation = Integer.MIN_VALUE; // not 0 as evaluations can go into negatives

    Board previousBoard = new Board(possibleBoard);
    for (Move move : moves) {
      possibleBoard.makeMove(move);
      int evaluation = -search(height-1, pointOfView * -1); // Negative as the evaluation returned will be the board evaluated from the opponents POV
      if (move.getLocation().getX() >= 7 && move.getLocation().getY() >= 7 ||
          move.getLocation().getX() < 7 && move.getLocation().getY() < 7) { // Moves which are in quadrant 2 or 3 of the board are given extra credit (closes in on opponent)
        evaluation += 1;
      }
      if (height == TOP && evaluation >= maxEvaluation) {
        movesRanked.computeIfAbsent(evaluation, k -> new ArrayList<>());
        movesRanked.get(evaluation).add(move); // moves grouped by evaluation scores
      }
      maxEvaluation = Integer.max(evaluation, maxEvaluation);
      possibleBoard = new Board(previousBoard); // Undoes move from board in preparation for next move to be evaluated
    }
    return maxEvaluation;
  }

  private int evaluate(Board board) {
    int evaluation = countBlocks(this.getPlayerNo(), board);
    evaluation += quadrantEvaluation(board, this.getPlayerNo());
    return evaluation;
  }

  private int quadrantEvaluation(Board board, int playerNum) {
    int totalQuadrantEvaluation = 0;

    int quadrant1BotCount = 0;
    int quadrant1OpponentCount = 0;

    // From top left quadrant to bottom right, quadrants are labelled 1 to 4
    // Starting quadrant for player 1 (X) is quadrant one and quadrant 4 for player 2 (O)

    // Calculates number of spots occupied by player 1 and player 2 in quadrant 1
    for (int y = (Board.HEIGHT-1); y >= (Board.HEIGHT/2); y--) {
      for (int x = 0; x < Board.WIDTH/2; x++) {
        if (board.isOccupied(x, y) && board.getOccupyingPlayer(x, y) == playerNum) {
          quadrant1BotCount++;
        } else {
          quadrant1OpponentCount++;
        }
      }
    }
    int quadrant1Evaluation = (quadrant1BotCount - quadrant1OpponentCount);

    if (quadrant1Evaluation >= 0) {
      totalQuadrantEvaluation += 1; // +1 for a taken over quadrant
      if (getPlayerNo() == PLAYER_TWO) {
        totalQuadrantEvaluation += 1; // +1 for taking over opponents starting quadrant
      }
      if (quadrant1OpponentCount > 0) {
        totalQuadrantEvaluation += 1; // +1 for taken over quadrant that contains opponents pieces (limiting their moves)
      }
    }


    int quadrant2BotCount = 0;
    int quadrant2OpponentCount = 0;
    for (int y = (Board.HEIGHT-1); y >= 7; y--) {
      for (int x = Board.WIDTH/2; x < Board.WIDTH; x++) {
        if (board.isOccupied(x, y) && board.getOccupyingPlayer(x, y) == playerNum) {
          quadrant2BotCount++;
        } else {
          quadrant2OpponentCount++;
        }
      }
    }
    int quadrant2Evaluation = (quadrant2BotCount - quadrant2OpponentCount);

    if (quadrant2Evaluation >= 0) {
      totalQuadrantEvaluation += 1;
      if (quadrant2OpponentCount > 0) {
        totalQuadrantEvaluation += 1;
      }
    }


    int quadrant3BotCount = 0;
    int quadrant3OpponentCount = 0;
    for (int y = (Board.HEIGHT/2-1); y >= 0; y--) {
      for (int x = 0; x < Board.WIDTH/2; x++) {
        if (board.isOccupied(x, y) && board.getOccupyingPlayer(x, y) == playerNum) {
          quadrant3BotCount++;
        } else {
          quadrant3OpponentCount++;
        }
      }
    }
    int quadrant3Evaluation = (quadrant3BotCount - quadrant3OpponentCount);

    if (quadrant3Evaluation >= 0) {
      totalQuadrantEvaluation += 1;
      if (quadrant3OpponentCount > 0) {
        totalQuadrantEvaluation += 1;
      }
    }



    int quadrant4BotCount = 0;
    int quadrant4OpponentCount = 0;
    for (int y = (Board.HEIGHT/2-1); y >= 0; y--) {
      for (int x = Board.WIDTH/2; x < Board.WIDTH; x++) {
        if (board.isOccupied(x, y) && board.getOccupyingPlayer(x, y) == playerNum) {
          quadrant4BotCount++;
        } else {
          quadrant4OpponentCount++;
        }
      }
    }
    int quadrant4Evaluation = (quadrant4BotCount - quadrant4OpponentCount);

    if (quadrant4Evaluation >= 0) {
      totalQuadrantEvaluation += 1;
      if (getPlayerNo() == PLAYER_ONE) {
        totalQuadrantEvaluation += 1;
      }
      if (quadrant4OpponentCount > 0) {
        totalQuadrantEvaluation += 1;
      }
    }


    return totalQuadrantEvaluation;
  }

  private int countBlocks(int playerNum, Board board) {
    int blockCount = 0;
    for (int i = 0; i < Board.WIDTH; i++) {
      for (int j = 0; j < Board.HEIGHT; j++) {
        if (board.isOccupied(i, j) && board.getOccupyingPlayer(i, j) == playerNum) {
          blockCount++;
        }
      }
    }
    return blockCount;
  }

}