package JFK;

import SimpleBot.SimpleBotPlayer;
import com.badlogic.gdx.Game;
import model.*;
import ui.UI;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.rotateRight;

public class JFKBotPlayer extends SimpleBotPlayer {
    boolean importedMoves1 = false;
    ArrayList<String> movesList1 = new ArrayList<>();
    String[][] movesArray1;
    protected Player opponent;
    protected Board board;
    protected boolean isFirstMove;
    int moveCount1 = 0;
    int moveCount2 = 0;

    public JFKBotPlayer(int playerNo) {
        super(playerNo);
        isFirstMove = true;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    ;

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    @Override
    public void setUI(UI ui) {
    }

    // This bot is VERY simple-minded it returns the first move it finds.
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
        } else {
            // Play the first found valid move.
            // N.B.: The game control checks that we have at least one valid move before calling this method
            ArrayList<Move> moves = new ArrayList<Move>();
            moves = getPlayerMoves(this, board);
            int[] values = moveValues(moves, moveCount1);
            if (Objects.equals(board.startLocations[getPlayerNo()], new Location(4, 9))) {
                BufferedReader reader = null;
                if (!importedMoves1) {
                    try {
                        reader = new BufferedReader((
                                new FileReader("src/main/java/SimpleBot/output1.txt")));
                        String line = reader.readLine();
                        while (line != null) {
                            movesList1.add(line);
                            line = reader.readLine();
                        }
                        importedMoves1 = true;
                        movesArray1 = new String[movesList1.size()][23];
                        for (int i = 0; i < movesList1.size(); i++) {
                            movesArray1[i] = movesList1.get(i).split("\t");
                            //System.out.println(movesArray1[i][22]);
                        }

                    } catch (FileNotFoundException e) {

                    } catch (IOException e) {

                    }
                }
                int[] possibleEndScores = new int[moves.size()];
                String[][] possibleMoves = new String[moves.size()][2];
                for (int i = 0; i < moves.size(); i++) {
                    String movePieceName = moves.get(i).getGamepieceName();
                    String movePieceLocation = moves.get(i).getLocation().toString();
                    //System.out.println("NAME " + movePieceName + " LOCATION " + movePieceLocation);
                    StringBuilder moveLocation = new StringBuilder();
                    for (int z = movePieceLocation.length() - 1; z > 0; z--) {
                        if (movePieceLocation.charAt(z) == '(') {
                            for (int x = z; x < movePieceLocation.length(); x++) {
                                moveLocation.append(movePieceLocation.charAt(x));
                                if (movePieceLocation.charAt(x) == ')') {
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    possibleMoves[i][0] = movePieceName;
                    possibleMoves[i][1] = moveLocation.toString();
                }
                for (int z = 0; z < moves.size(); z++) {
                    for (int i = 0; i < movesArray1.length; i++) {
                        if (moveCount1 < 22) {
                            if (movesArray1[i][moveCount1].contains(possibleMoves[z][0]) && movesArray1[i][moveCount1].contains(possibleMoves[z][1])) {
                                possibleEndScores[z] = parseInt(movesArray1[i][22]);
                                break;
                            }
                        }
                    }
                    moveCount1++;
                }
                //System.out.println("POSSIBLE MOVES: " + moves.size() + " END SCORES " + possibleEndScores.length);
                int highestPossibleScore = 0;
                for (int i = 0; i < possibleEndScores.length; i++) {
                    if (highestPossibleScore < possibleEndScores[i]) {
                        highestPossibleScore = possibleEndScores[i];
                    }
                }
                for (int i = 0; i < possibleEndScores.length; i++) {
                    if (highestPossibleScore == possibleEndScores[i]) {
                        return moves.get(i);
                    }
                }
            }
            if (Objects.equals(board.startLocations[getPlayerNo()], new Location(9, 4))) {
                System.out.println("I'M PLAYER 2");
            }
            int highestValue = 0;
            for (int value : values) {
                if (highestValue < value) {
                    highestValue = value;
                }
            }
            ArrayList<Integer> usedNumbers = new ArrayList<Integer>();
            if (moves.size() > 2) {
                for (int i = 0; i < values.length; i++) {
                    int randomNumber = -1;
                    do {
                        randomNumber = ThreadLocalRandom.current().nextInt(0, values.length);
                    } while (usedNumbers.contains(randomNumber));
                    usedNumbers.add(randomNumber);
                    if (values[randomNumber] == highestValue) {
                        System.out.println("MOVE: " + moves.get(randomNumber).getGamepieceName() + " VALUE: " + values[randomNumber]);
                        return moves.get(randomNumber);
                    }
                }
            }
            if (moves.size() <= 2) {
                for (Move move : moves) {
                    if (move.getGamepieceName().equals("I1")) {
                        return move;
                    }
                }
            }
            moveCount1++;
            return moves.get(0);
        }
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
        //System.out.println("Moves:" + moves.get(0).getGamepiece());
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

    public static int[] moveValues(ArrayList<Move> moves , int moveCount1) {//weight of the piece plus number of corners == value
        int[] values = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).getGamepieceName().equals("I1")) {
                values[i] = 1;
            }
            if (moves.get(i).getGamepieceName().equals("I2")) {
                values[i] = 2 * 3 + 4 + locationPriority(moves.get(i) , moveCount1)/2;
            }
            if (moves.get(i).getGamepieceName().equals("I3")) {
                values[i] = 3 * 3 + 4 + locationPriority(moves.get(i) , moveCount1)/2;
            }
            if (moves.get(i).getGamepieceName().equals("V3")) {
                values[i] = 3 * 3 + 5 + locationPriority(moves.get(i), moveCount1)/2;
            }
            if (moves.get(i).getGamepieceName().equals("I4") || moves.get(i).getGamepieceName().equals("O4")) {
                values[i] = 4 * 3 + 4 + locationPriority(moves.get(i) , moveCount1);
            }
            if (moves.get(i).getGamepieceName().equals("L4")) {
                values[i] = 4 * 3 + 5 + locationPriority(moves.get(i), moveCount1);
            }
            if (moves.get(i).getGamepieceName().equals("Z4") || moves.get(i).getGamepieceName().equals("T4")) {
                values[i] = 4 * 3 + 6 + locationPriority(moves.get(i) ,moveCount1);
            }
            if (moves.get(i).getGamepieceName().equals("I5")) {
                values[i] = 5 * 3 + 4 + locationPriority(moves.get(i), moveCount1);
            }
            if (moves.get(i).getGamepieceName().equals("L5") || moves.get(i).getGamepieceName().equals("V5") || moves.get(i).getGamepieceName().equals("P")) {
                values[i] = 5 * 3 + 5 + locationPriority(moves.get(i) ,moveCount1);
            }
            if (moves.get(i).getGamepieceName().equals("T5") || moves.get(i).getGamepieceName().equals("N") || moves.get(i).getGamepieceName().equals("Z5")
                    || moves.get(i).getGamepieceName().equals("U") || moves.get(i).getGamepieceName().equals("Y")) {
                values[i] = 5 * 3 + 6 + locationPriority(moves.get(i) , moveCount1);
            }
            if (moves.get(i).getGamepieceName().equals("W") || moves.get(i).getGamepieceName().equals("F")) {
                values[i] = 5 * 3 + 7 + locationPriority(moves.get(i) , moveCount1);
            }
            if (moves.get(i).getGamepieceName().equals("X")) {
                values[i] = 5 * 3 + 8 + locationPriority(moves.get(i) ,moveCount1);
            }
        }
        return values;
    }

    public static int locationPriority(Move moves , int moveCount1) {
        int centre = 0;
        int x, y;
        x = moves.getLocation().getX();
        y = moves.getLocation().getY();
        if (x > 3 && x < 10 && y > 3 && y < 10) {
            centre = 2;
        } else if ((x > 3 && x < 10) || (y > 3 && y < 10)) {
            centre = 1;
        } else {
            centre = 0;
        }
        if(moveCount1 > 6){
            if (x < 3 || x > 10 && y < 3 || y > 10) {
                centre = 2;
            } else if ((x < 3 || x > 10) || (y < 3 || y > 10)) {
                centre = 1;
            } else {
                centre = 0;
            }
        }
        return centre;
    }
}
