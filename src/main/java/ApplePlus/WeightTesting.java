package ApplePlus;

/**
 * Team ApplePlus
 * Members: Ao Peng     20202688
 *          Oscar Yeoh  20403662
 *          KarYen Yap  20202149
 *
 * Modified version of Sprint5
 *  - allows for getting the fitness of each bot in order to find optimal weights
 */

import SimpleBot.SimpleBotPlayer;
import model.Board;
import model.Player;
import ui.UI;
import ui.text.TextUI;

import java.util.Random;

public class WeightTesting {
    private static final int NUM_PLAYERS = 5;
    private static final int NUM_GAMES = 5;
    private static final int[][] fitness = new int[NUM_PLAYERS][NUM_PLAYERS];

    /**
     * Generates random weights for bots and plays them against each other
     */
    public static void calculateWeights() {
        Random rand = new Random();
        WeightPlayer[] weightPlayers = new WeightPlayer[NUM_PLAYERS];

        //Initialise bots with random weights
        for (int i = 0; i < NUM_PLAYERS; i++) {
            double weight1 = rand.nextDouble();
            double weight2 = rand.nextDouble()*(1-weight1);
            double weight3 = 1 - weight1 - weight2;
            weightPlayers[i] = new WeightPlayer(i, weight1, weight2, weight3);
        }

        //Play each bot against every other bot
        for (int i = 0; i < NUM_PLAYERS - 1; i++) {
            for (int j = i + 1; j < NUM_PLAYERS; j++) {
                for (int k = 0; k < NUM_GAMES; k++) {
                    test(weightPlayers[i], weightPlayers[j]);
                }
            }
        }

        //Sum results to get fitness
        for (int i = 0; i < NUM_PLAYERS; i++) {
            int sum = 0;
            for (int j = 0; j < NUM_PLAYERS; j++) {
                if (i == j) {
                    continue;
                }
                sum += fitness[i][j];
            }
            System.out.println("Fitness for bot " + i + ": " + sum + " Weights: " + weightPlayers[i].getBlockerWeight() + " " + weightPlayers[i].getBuilderWeight() + " " + weightPlayers[i].getBigPieceWeight());
        }
    }

    /**
     * Modified version of main in Sprint5
     * Takes in two WeightPlayers and initialises two ApplePLusBots and plays them in the game
     *
     * @param p1 first bot player
     * @param p2 second bot player
     */
    public static void test(WeightPlayer p1, WeightPlayer p2) {
        Board board = new Board();
        Player[] players = new Player[Board.startLocations.length];
        UI ui;
        BlokusDuoWeight blokusDuoWeight;
        int activePlayer = new Random().nextInt(2);

        SimpleBotPlayer botPlayer1 = new ApplePlusBot(0, p1.getBuilderWeight(), p1.getBlockerWeight(), p1.getBigPieceWeight());
        botPlayer1.setBoard(board);
        players[0] = botPlayer1;

        SimpleBotPlayer botPlayer2 = new ApplePlusBot(1, p2.getBuilderWeight(), p2.getBlockerWeight(), p2.getBigPieceWeight());
        botPlayer2.setBoard(board);
        players[1] = botPlayer2;

        ui = new TextUI(board, players);
        for (Player player : players) {
            player.setUI(ui);
        }

        if (SimpleBotPlayer.class.isAssignableFrom(players[0].getClass())) {
            ((SimpleBotPlayer)players[0]).setOpponent(players[1]);
        }

        if (SimpleBotPlayer.class.isAssignableFrom(players[1].getClass())) {
            ((SimpleBotPlayer)players[1]).setOpponent(players[0]);
        }

        blokusDuoWeight = new BlokusDuoWeight(board,players,ui,activePlayer);

        int result = blokusDuoWeight.run();
        if (result > 0) {
            fitness[p1.getID()][p2.getID()] += 2;
        } else if (result == 0) {
            fitness[p1.getID()][p2.getID()] += 1;
            fitness[p2.getID()][p1.getID()] += 1;
        } else {
            fitness[p2.getID()][p1.getID()] += 2;
        }
    }
}
