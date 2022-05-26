//Team name: Alpha
package AlphaBot;

import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class PossibleBoard extends Board {

    Player player;
    public Board b;
    ArrayList<Move> moves = new ArrayList<>();


    public PossibleBoard(Board bd, Player play) {
        b = bd;
        player = play;
    }

    public Move getMove() {
        System.out.println(player.getName());
        moves = getPlayerMoves(player, b);
        int i = 0, x = 0, c, s = moves.size(), best = 0;

        while (i < s) {
            c = moves.get(i).getGamepiece().getLocations().length;
            if (c > best) {
                best = c;
                x = i;
                i++;
            } else if (c < best) {
                moves.remove(i);
                s--;
            } else {
                i++;
            }
        }
        int n = x + (int) (Math.random() * (s - x));
        return moves.get(n);
    }

    public ArrayList<Move> getPlayerMoves(Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
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

    public ArrayList<Move> getMovesWithGivenGamepiece(Gamepiece gamepiece, String gamepieceName, Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
        Gamepiece clonedPiece = new Gamepiece(gamepiece);
        for (int i = 0; i < 4; i++) {
            moves.addAll(getMovesWithGivenOrientation(clonedPiece, gamepieceName, player, board));
            clonedPiece.rotateRight();
        }
        clonedPiece.flipAlongY();
        for (int i = 0; i < 4; i++) {
            moves.addAll(getMovesWithGivenOrientation(clonedPiece, gamepieceName, player, board));
            clonedPiece.rotateRight();
        }
        return moves;
    }

    public ArrayList<Move> getMovesWithGivenOrientation(Gamepiece piece, String gamepieceName, Player player, Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
        if (player.getGamepieceSet().getPieces().size() == 21) {
            Move move = new Move(player, piece, gamepieceName, new Location(0, 0));
            for (int x = 0; x < board.WIDTH; x++) {
                for (int y = 0; y < board.HEIGHT; y++) {
                    move.getLocation().setX(x);
                    move.getLocation().setY(y);
                    if (board.isValidFirstMove(move)) {
                        moves.add(new Move(move));
                    }
                }
            }
            return moves;
        } else {
            Move move = new Move(player, piece, gamepieceName, new Location(0, 0));
            for (int x = 0; x < board.WIDTH; x++) {
                for (int y = 0; y < board.HEIGHT; y++) {
                    move.getLocation().setX(x);
                    move.getLocation().setY(y);
                    if (board.isValidSubsequentMove(move)) {
                        moves.add(new Move(move));
                    }
                }
            }
            return moves;
        }
    }
}