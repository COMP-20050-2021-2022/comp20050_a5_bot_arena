package ApplePlus;

/**
 * Team ApplePlus
 * Members: Ao Peng     20202688
 *          Oscar Yeoh  20403662
 *          KarYen Yap  20202149
 *
 * Player class that holds the values of weights for each bot instance.
 */

public class WeightPlayer {
    private final double blockerWeight;
    private final double builderWeight;
    private final double bigPieceWeight;
    private final int ID;

    public WeightPlayer(int ID, double blockerWeight, double builderWeight, double bigPieceWeight) {
        this.builderWeight = builderWeight;
        this.blockerWeight = blockerWeight;
        this.bigPieceWeight = bigPieceWeight;
        this.ID = ID;
    }

    public double getBigPieceWeight() {
        return bigPieceWeight;
    }

    public double getBlockerWeight() {
        return blockerWeight;
    }

    public double getBuilderWeight() {
        return builderWeight;
    }

    public int getID() {
        return ID;
    }
}
