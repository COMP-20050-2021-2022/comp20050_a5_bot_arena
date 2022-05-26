package control;

import SimpleBot.SimpleBotPlayer;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import model.Board;
import model.Move;
import model.Player;
import ui.UI;

import com.google.common.util.concurrent.SimpleTimeLimiter;

import java.time.Duration;
import java.util.concurrent.*;

public class BlokusDuoPlay implements Runnable {

    final long TIMEOUT = 30;  // timeout (in seconds) for making a move

    private Board board;
    private Player[] players;
    private UI ui;
    private int activePlayer;

    public BlokusDuoPlay(Board board, Player[] players, UI ui, int activePlayer) {
        this.board = board;
        this.players = players;
        this.ui = ui;
        this.activePlayer = activePlayer;
    }

    @Override
    public void run() {

        // Get player names

        for (Player player : players) {
            if (SimpleBotPlayer.class.isAssignableFrom(players[0].getClass())) {
                player.setName(player.getClass().getName().split("\\.")[0]);
            } else {
                player.setName(ui.getPlayerName(player));
            }
        }

        // Gameplay

        int gameTurn = 0;
        boolean otherPlayerSkipped = false;

        SimpleTimeLimiter limiter = SimpleTimeLimiter.create(Executors.newSingleThreadExecutor());

        Player winner = null;

        ui.announcePlayerMakingFirstMove(players[activePlayer]);

        for (; ; ) {
            Move move = null;

            ui.updateDisplay();

            /* check if the player still has some possible moves,
             * terminate game if the other player did not have moves on the previous move either
             */
            if ((gameTurn >= 2) && (!board.playerHasMoves(players[activePlayer]))) {
                if (otherPlayerSkipped) {
                    break;
                } else {
                    otherPlayerSkipped = true;
                }
            } else {

                // Active player has moves let's play!
                otherPlayerSkipped = false;

                if (gameTurn < 2) {
                    try {
                        move = limiter.callWithTimeout(new Callable<Move>() {
                            public Move call() {
                                boolean isValidMove;
                                Move m;
                                do {
                                    m = players[activePlayer].makeMove(board);
                                    isValidMove = board.isValidFirstMove(m);

                                    if (isValidMove) {
                                        ui.displayMove(m);
                                        board.makeMove(m);
                                    } else {
                                        ui.noifyBadMove(m);
                                    }
                                } while (!isValidMove);
                                return m;
                            }
                        }, TIMEOUT, TimeUnit.SECONDS);
                    } catch (TimeoutException | InterruptedException | ExecutionException e) {
                        // Current player exceeded running time therefore its opponent has won
                        winner = players[(activePlayer + 1) % players.length];
                        break;
                    }
                } else {
                    try {
                        move = limiter.callWithTimeout(new Callable<Move>() {
                            public Move call() {
                                boolean isValidMove;
                                Move m;

                                do {
                                    m = players[activePlayer].makeMove(board);
                                    isValidMove = board.isValidSubsequentMove(m);

                                    if (isValidMove) {
                                        ui.displayMove(m);
                                        board.makeMove(m);
                                    } else {
                                        ui.noifyBadMove(m);
                                    }
                                } while (!isValidMove);
                                return m;
                            }
                        }, TIMEOUT, TimeUnit.SECONDS);
                    } catch (TimeoutException | InterruptedException | ExecutionException e) {
                        // Current player exceeded running time therefore its opponent has won
                        ui.notifyTimeOut(players[activePlayer]);
                        winner = players[(activePlayer + 1) % players.length];
                        break;
                    }
                }
                players[activePlayer].getGamepieceSet().remove(move.getGamepieceName());
            }
            gameTurn = gameTurn + 1;
            activePlayer = (activePlayer + 1) % players.length;
        }

        // Game is finished
        if (winner==null) {
            winner = players[0].playerScore() > players[1].playerScore() ? players[0] : players[1];
        }

        ui.displayGameOverMessage(winner);
    }

    public UI getUI() {
        return ui;
    }

    public Player[] getPlayers() {
        return players;
    }
}
