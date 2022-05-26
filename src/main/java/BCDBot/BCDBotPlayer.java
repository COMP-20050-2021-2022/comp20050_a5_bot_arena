package BCDBot;

import SimpleBot.SimpleBotPlayer;
import model.*;
import ui.UI;

import java.util.ArrayList;
import java.util.Collection;

public class BCDBotPlayer extends SimpleBotPlayer {

    protected Player opponent;
    protected Board board;
    protected boolean isFirstMove;

    public BCDBotPlayer(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    @Override
    public void setUI(UI ui) {
    }

    @Override
    public Move makeMove(Board board) {
        if (isFirstMove) {
            isFirstMove = false;
            return new Move(
                    this,
                    new Gamepiece(getGamepieceSet().get("F")),
                    "F",
                    new Location(board.startLocations[getPlayerNo()]));
        }

        return getBestMove(this, opponent,board);
    }

    //Find the best available move via the moveRank algorithm - J
    public Move getBestMove(Player player,Player opponent, Board board) {
        ArrayList<Move> availableMoves = getPlayerMoves(player, board);
        Move bestMove = availableMoves.get(0);
        int bestMoveRank = rankMove(bestMove, player, opponent, board);

        for (Move move : availableMoves) {
            int currMoveRank = rankMove(move, player, opponent, board);
            if (currMoveRank > bestMoveRank) {
                bestMove = move;
                bestMoveRank = currMoveRank;
            }
        }

        return bestMove;
    }

    public int rankMove(Move move, Player player,Player opponent ,Board board) {
        int rank = 0;

        int size = sizeFactor(move);
        int playerCornersAdded = newCorners(move,player,board);
        int opponentCornerRemoved = opponentCornersCovered(move,player,board);


        rank = size + (playerCornersAdded) + (3*opponentCornerRemoved);
        return rank;
    }

    public int sizeFactor(Move move) {
        int size = move.getGamepiece().getLocations().length;
        if (size < 3) return 0;
        else if (size < 5) return 4;
        else return 7;
    }

    public int opponentCornersCovered(Move move, Player opponent, Board board) {

        Location moveLoc = move.getLocation();
        Location[] loc = move.getGamepiece().getLocations();
        Location temp;
        int res = 0;

        for (Location l : loc) {

            temp = new Location(moveLoc.getX() + l.getX() + 1, moveLoc.getY() + l.getY() + 1);
            if (temp.getX() >= 0 && temp.getX() < 14 && temp.getY() >= 0 && temp.getY() < 14) {
                if (board.isOccupied(temp.getX(), temp.getY()) && board.getOccupyingPlayer(temp.getX(), temp.getY()) == this.opponent.getPlayerNo()
                        && !board.isOccupied(temp.getX() - 1, temp.getY()) && !board.isOccupied(temp.getX(), temp.getY() - 1))
                {
                    res++;
                }
            }

            temp = new Location(moveLoc.getX() + l.getX() + 1, moveLoc.getY() + l.getY() - 1);
            if (temp.getX() >= 0 && temp.getX() < 14 && temp.getY() >= 0 && temp.getY() < 14) {
                if (board.isOccupied(temp.getX(), temp.getY()) && board.getOccupyingPlayer(temp.getX(), temp.getY()) == this.opponent.getPlayerNo()
                        && !board.isOccupied(temp.getX() - 1, temp.getY()) && !board.isOccupied(temp.getX(), temp.getY() + 1))
                {
                    res++;
                }
            }

            temp = new Location(moveLoc.getX() + l.getX() - 1, moveLoc.getY() + l.getY() - 1);
            if (temp.getX() >= 0 && temp.getX() < 14 && temp.getY() >= 0 && temp.getY() < 14) {
                if (board.isOccupied(temp.getX(), temp.getY()) && board.getOccupyingPlayer(temp.getX(), temp.getY()) == this.opponent.getPlayerNo()
                        && !board.isOccupied(temp.getX() + 1, temp.getY()) && !board.isOccupied(temp.getX(), temp.getY() + 1))
                {
                    res++;
                }
            }

            temp = new Location(moveLoc.getX() + l.getX() - 1, moveLoc.getY() + l.getY() + 1);
            if (temp.getX() >= 0 && temp.getX() < 14 && temp.getY() >= 0 && temp.getY() < 14) {
                if (board.isOccupied(temp.getX(), temp.getY()) && board.getOccupyingPlayer(temp.getX(), temp.getY()) == this.opponent.getPlayerNo()
                        && !board.isOccupied(temp.getX() + 1, temp.getY()) && !board.isOccupied(temp.getX(), temp.getY() - 1))
                {
                    res++;
                }
            }
        }

        return res;
        //For each corner covered: res++

    }

    public int newCorners(Move move, Player player, Board board) {

        Location[] loc = getCornerLocations(move);
        int res = 0;
        Location moveLoc = move.getLocation();

        for (Location l : loc) {
            Location p = new Location(moveLoc.getX() + l.getX(), moveLoc.getY() + l.getY());

            if ( !(p.getX() < 0 || p.getX() >= 14 || p.getY() < 0 || p.getY() >= 14) )
                if (!board.isOccupied(p.getX(), p.getY()))
                    res++;
        }
        return res;
    }

    public boolean isCorner(Location location, Player player, Board board) {

        Move m = new Move(player,new Gamepiece(new int[]{0, 0},player.getPlayerNo()),"I1",location);
        //If any diagonal squares belong to player: corner = true
        return board.isValidSubsequentMove(m);
    }

    public ArrayList<Move> getPlayerMoves(Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        Collection<String> gamepieceNames = player.getGamepieceSet().getPieces().keySet();
        for (String gamepieceName : gamepieceNames) {
            moves.addAll(
                    getMovesWithGivenGamepiece(
                            player.getGamepieceSet().getPieces().get(gamepieceName),
                            gamepieceName,
                            player,
                            board)
            );
        }
        return moves;
    }

    public ArrayList<Move> getMovesWithGivenGamepiece(Gamepiece gamepiece, String gamepieceName, Player player, Board board){
        ArrayList<Move> moves = new ArrayList<>();
        Gamepiece clonedPiece = new Gamepiece(gamepiece);
        for (int i = 0; i < 4 ; i++) {
            moves.addAll(getMovesWithGivenOrientation(clonedPiece,gamepieceName,player,board));
            clonedPiece.rotateRight();
        }
        clonedPiece.flipAlongY();
        for (int i = 0; i < 4 ; i++) {
            moves.addAll(getMovesWithGivenOrientation(clonedPiece,gamepieceName,player,board));
            clonedPiece.rotateRight();
        }
        return moves;
    }

    public ArrayList<Move> getMovesWithGivenOrientation(Gamepiece piece, String gamepieceName, Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        Move move = new Move(player,piece, gamepieceName,new Location(0,0));
        for (int x = 0; x < Board.WIDTH; x++) {
            for (int y = 0; y < Board.HEIGHT; y++) {
                move.getLocation().setX(x);
                move.getLocation().setY(y);
                if (board.isValidSubsequentMove(move)) {
                    moves.add(new Move(move));
                }
            }
        }
        return moves;
    }

    public Location[] getCornerLocations(Move move) {

        final int North = 2;
        final int South = 3;
        final int East = 5;
        final int West = 7;

        Gamepiece piece = move.getGamepiece();
        ArrayList<Location> arr = new ArrayList<>();

        for (Location loc : piece.getLocations()) {

            int edges = 1;

            //check north
            if (!containsLoc(piece, new Location(loc.getX(), loc.getY() + 1) )) {
                edges *= North;
            }

            //check east
            if (!containsLoc(piece, new Location(loc.getX() + 1, loc.getY()))) {
                edges *= East;
            }

            //south
            if (!containsLoc(piece, new Location(loc.getX(), loc.getY() - 1))) {
                edges *= South;
            }

            //west
            if (!containsLoc(piece, new Location(loc.getX() - 1, loc.getY()))) {
                edges *= West;
            }

            if (edges % (North * East) == 0)
                arr.add(new Location(loc.getX() + 1, loc.getY() + 1));

            if (edges % (East * South) == 0)
                arr.add(new Location(loc.getX() + 1, loc.getY() - 1));

            if (edges % (South * West) == 0)
                arr.add(new Location(loc.getX() - 1, loc.getY() - 1));

            if (edges % (West * North) == 0)
                arr.add(new Location(loc.getX() - 1, loc.getY() + 1));
        }

        Location[] loc = new Location[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            loc[i] = arr.get(i);
        }
        return loc;
    }
    public boolean containsLoc(Gamepiece piece, Location loc) {

        for (Location l : piece.getLocations()) {
            if (l.getX() == loc.getX() && l.getY() == loc.getY())
                return true;
        }
        return false;
    }

    public boolean isOpponentCorner(Location loc, Player opp, Board board) {

        final int North = 2;
        final int South = 3;
        final int East = 5;
        final int West = 7;

        int edges = 1;

        //check north
        if (loc.getY() + 1 < 14) {
            if (!board.isOccupied(loc.getX(), loc.getY() + 1))
                edges *= North;
        }

        //check south
        if (loc.getY() - 1 >= 0) {
            if (!board.isOccupied(loc.getX(), loc.getY() - 1))
                edges *= South;
        }

        // check east
        if (loc.getX() + 1 < 14) {
            if (!board.isOccupied(loc.getX() + 1, loc.getY()))
                edges *= East;
        }

        //check west
        if (loc.getX() - 1 >= 0) {
            if (!board.isOccupied(loc.getX() - 1, loc.getY()))
                edges *= West;
        }

        if (edges % (North*East) == 0 || edges % (East*South) == 0 ||
                edges % (South*West) == 0 || edges % (West*North) == 0) {
            return true;
        }
        else return false;

    }

    public Location rotateLocation(Location loc) {

        return new Location(loc.getY(), loc.getX() * (-1));
    }
}
