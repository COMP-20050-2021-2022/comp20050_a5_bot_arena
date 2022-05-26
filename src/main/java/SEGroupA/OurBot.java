package SEGroupA;

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Gamepiece;
import model.Location;
import model.Move;

import java.util.ArrayList;

public class OurBot extends SimpleBotPlayer {

    public OurBot(int playerNo) {
        super(playerNo);
        isFirstMove = true;
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
        } else {
            // Play the first found valid move.
            // N.B.: The game control checks that we have at least one valid move before calling this method

            ArrayList<Move> moves = new ArrayList<>();
            moves = getPlayerMoves(this, board);

            Move bestMove = getBestMove(moves, board);
            return bestMove;
        }
    }

    private Move getBestMove(ArrayList<Move> moves, Board board) {
        int bestMoveScore = 0;
        int currentMoveScore = 0;
        Move bestMove = moves.get(0);

        for(int i = 0; i < moves.size(); i++) {
            currentMoveScore = scoreMove(moves, moves.get(i), board);

            if(currentMoveScore > bestMoveScore) {
                bestMoveScore = currentMoveScore;
                bestMove = moves.get(i);
            }
        }
        return bestMove;
    }

    private int scoreMove(ArrayList<Move> allMoves, Move move, Board board) {
        return pieceSize(move) * (numberOfMovesCreated(allMoves, move, board) + numberOfOpponentsMovesBlocked(move, board));
//        return pieceSize(move) * numberOfMovesCreated(allMoves, move, board) + numberOfOpponentsMovesBlocked(move, board);

    }

    private int pieceSize(Move move) {
        int x = 0;
        switch (move.getGamepieceName()){
            case "I1": x = 1; break;
            case "I2": x = 2; break;
            case "I3":
            case "V3": x = 3; break;
            case "I4":
            case "T4":
            case "Z4":
            case "O4":
            case "L4": x = 4; break;
            case "I5":
            case "P":
            case "N":
            case "Z5":
            case "L5":
            case "V5":
            case "T5":
            case "F":
            case "U":
            case "W":
            case "X":
            case "Y": x = 5; break;
            }
            return x;
        }


    private int numberOfMovesCreated(ArrayList<Move> allMoves, Move move, Board board) {
        int oldNumberOfAvailableMoves = allMoves.size();
        Board copyBoard = new Board(board);
        copyBoard.makeMove(move);
        int newNumberOfAvailableMoves = getPlayerMoves(this, copyBoard).size();

        return newNumberOfAvailableMoves - oldNumberOfAvailableMoves;
    }

    private int numberOfOpponentsMovesBlocked(Move move, Board board) {
        int oldNumberOfOppenentsAvailableMoves = getPlayerMoves(this.opponent, board).size();
        Board copyBoard = new Board(board);
        copyBoard.makeMove(move);
        int newNumberOfOpponentsAvailableMoves = getPlayerMoves(this.opponent, copyBoard).size();

        return oldNumberOfOppenentsAvailableMoves - newNumberOfOpponentsAvailableMoves;
    }
}
