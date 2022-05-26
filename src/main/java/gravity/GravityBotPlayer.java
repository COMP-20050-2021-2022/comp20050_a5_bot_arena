package gravity;

import SimpleBot.SimpleBotPlayer;
import model.*;
import java.util.ArrayList;
import java.util.HashSet;

/** Team GRAVITY bot player */

class MyLocation extends Location {

    public MyLocation(Location location) {
        super(location);
    }

    public MyLocation (int x, int y) {
        super(x, y);
    }

    @Override
    public int hashCode() {
        return this.getX() * Board.HEIGHT + this.getY();
    }
}


public class GravityBotPlayer extends SimpleBotPlayer {
  private boolean wasContact;

  private boolean isInsideBoard(int x, int y) {
    return x >= 0 && y >= 0 && x < Board.WIDTH && y < Board.HEIGHT;
  }

  private int[][] eightNeighbours = {
    {-1, 0}, {-1, -1}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, -1}, {1, 1}
  };

  private int[][] cornerNeighbours = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

  private int[][] sideNeighbours = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

  private int countGrowthPointsLocation(Player p, Board b) {
    ArrayList<Move> playerMoves = getPlayerMoves(p, b);
    HashSet<MyLocation> squaresHit = new HashSet<>();
    for (Move m : playerMoves) {
      for (Location l : m.getGamepiece().getLocations())
      squaresHit.add(new MyLocation((l)));
    }

    HashSet<MyLocation> squaresOfColor = new HashSet<>();
    for (int i = 0; i < Board.HEIGHT; i++) {
      for (int j = 0; j < Board.WIDTH; j++) {
        if (!b.isOccupied(i, j)) continue;
        if (b.getOccupyingPlayer(i, j) != p.getPlayerNo()) continue;

        squaresOfColor.add(new MyLocation(i, j));
      }
    }

    HashSet<MyLocation> growthPoints = new HashSet<>();
    for (MyLocation soc : squaresOfColor) {
      for (int moveNo = 0; moveNo < cornerNeighbours.length; moveNo++) {
        int di = cornerNeighbours[moveNo][0];
        int dj = cornerNeighbours[moveNo][1];

        int i2 = soc.getX() + di;
        int j2 = soc.getY() + dj;

        if (isInsideBoard(i2, j2)) growthPoints.add(new MyLocation(i2, j2));
      }
    }

    HashSet<MyLocation> sidePoints = new HashSet<>();
    for (MyLocation soc : squaresOfColor) {
      for (int moveNo = 0; moveNo < sideNeighbours.length; moveNo++) {
        int di = sideNeighbours[moveNo][0];
        int dj = sideNeighbours[moveNo][1];

        int i2 = soc.getX() + di;
        int j2 = soc.getY() + dj;

        if (isInsideBoard(i2, j2)) sidePoints.add(soc);
      }
    }

    // System.out.println(growthPoints);
    growthPoints.retainAll(squaresHit);
    // System.out.println(squaresHit);
    // System.out.println(growthPoints);

    return growthPoints.size();
  }

  private boolean isContact() {
    if (wasContact) return true;

    // check whether a contact occurred since the last check
    boolean result = false;
    for (int i = 0; !result && i < Board.HEIGHT; i++) {
      for (int j = 0; !result && j < Board.WIDTH; j++) {
        if (!board.isOccupied(i, j)) continue;
        if (board.getOccupyingPlayer(i, j) != this.getPlayerNo()) continue;

        for (int moveNo = 0; !result && moveNo < eightNeighbours.length; moveNo++) {
          int di = eightNeighbours[moveNo][0];
          int dj = eightNeighbours[moveNo][1];

          int i2 = i + di;
          int j2 = j + dj;

          if (isInsideBoard(i2, j2)
              && board.isOccupied(i2, j2)
              && board.getOccupyingPlayer(i2, j2) == opponent.getPlayerNo()) result = true;
        }
      }
    }

    if (result) {
      wasContact = true;
    }
    return result;
  }

  public GravityBotPlayer(int playerNo) {
    super(playerNo);
    wasContact = false;
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
              new Location(board.startLocations[getPlayerNo()]));
    }

    else {
      ArrayList<Move> moves = getPlayerMoves(this, board);
      boolean contact = isContact();

      Move res;

      if (!contact) {
        // builder mode
        int gpMax = -1;
        Move maxMove = null;
        for (Move move : moves) {
          Board possibleBoard = new Board(board);
          possibleBoard.makeMove(move);

          int gp = countGrowthPointsLocation(this, possibleBoard);
          if (gp > gpMax
                  || gp == gpMax
                  && move.getGamepiece().getLocations().length > maxMove.getGamepiece().getLocations().length) {
            gpMax = gp;
            maxMove = move;
          }

        }
        res = maxMove;
      }
      else {
        // blocker mode
        // utility = our growth - opponent growth

        int gpMax = -1000;
        Move maxMove = null;
        for (Move move : moves) {
          Board possibleBoard = new Board(board);
          possibleBoard.makeMove(move);

          int gp = countGrowthPointsLocation(this, possibleBoard);
          int ogp = countGrowthPointsLocation(this.opponent, possibleBoard);
          gp -= ogp;
          if (gp > gpMax
                  || gp == gpMax
                  && move.getGamepiece().getLocations().length > maxMove.getGamepiece().getLocations().length) {
            gpMax = gp;
            maxMove = move;
          }

        }
        res = maxMove;

      }
      return res;
    }
  }
}
