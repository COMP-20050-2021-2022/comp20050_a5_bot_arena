package ApplePlus;

/**
 * Team ApplePlus
 * Members: Ao Peng     20202688
 *          Oscar Yeoh  20403662
 *          KarYen Yap  20202149
 *
 * Modified version of BlokusDuoPlay
 *  - used for weight testing
 */

import model.Board;
import model.Move;
import model.Player;
import ui.UI;

public class BlokusDuoWeight {

    private final Board board;
    private final Player[] players;
    private final UI ui;
    private int activePlayer;

    public BlokusDuoWeight(Board board, Player[] players, UI ui, int activePlayer) {
        this.board = board;
        this.players = players;
        this.ui = ui;
        this.activePlayer = activePlayer;
    }

    /**
     * Controls the flow of the game
     * @return result of game
     */
    public int run() {

        // Get player names

        for (Player player : players) {
            player.setName("bot");
        }

        // Gameplay

        int gameTurn = 0;
        boolean otherPlayerSkipped = false;

        ui.announcePlayerMakingFirstMove(players[activePlayer]);

        for(;;) {
            Move move;
            boolean isValidMove;

            ui.updateDisplay();

            /* check if the player still has some possible moves,
             * terminate game if the other player did not have moves on the previous move either
             */
            if ( (gameTurn >=2) && (! board.playerHasMoves(players[activePlayer]))) {
                if (otherPlayerSkipped) {
                    break;
                } else {
                    otherPlayerSkipped = true;
                }
            } else {

                // Active player has moves let's play!
                otherPlayerSkipped = false;

                do {
                    move = players[activePlayer].makeMove(board);

                    if (gameTurn < 2) {
                        isValidMove = board.isValidFirstMove(move);
                    } else {
                        isValidMove = board.isValidSubsequentMove(move);
                    }

                    if (isValidMove) {
                        board.makeMove(move);
                    } else {
                        ui.noifyBadMove(move);
                    }
                } while (!isValidMove);

                players[activePlayer].getGamepieceSet().remove(move.getGamepieceName());
            }

            gameTurn = gameTurn + 1;
            activePlayer = (activePlayer + 1) % players.length;

        }

        // Game is finished

        //ui.displayGameOverMessage();
        return Integer.compare(players[0].playerScore(), players[1].getPlayerNo());
    }

    public UI getUI() {
        return ui;
    }
}


