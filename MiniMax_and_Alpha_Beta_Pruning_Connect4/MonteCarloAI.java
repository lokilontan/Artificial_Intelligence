import java.util.Random;

public class MonteCarloAI extends AIModule {
    private final Random r = new Random(System.currentTimeMillis());
    private int[] moves;

    public MonteCarloAI() {
    }

    public void getNextMove(GameStateModule var1) {
        this.moves = new int[var1.getWidth()];
        this.chosenMove = 0;
        int var2 = var1.getActivePlayer();
        int[] var3 = new int[var1.getWidth()];

        int var4;
        for(var4 = 0; var4 < var3.length; ++var4) {
            if (!var1.canMakeMove(var4)) {
                var3[var4] = -2147483647;
            }
        }

        while(!this.terminate) {
            var4 = this.getMove(var1);
            var1.makeMove(var4);
            this.updateGuess(var2, this.playRandomGame(var1), var3, var4);
            var1.unMakeMove();
        }

    }

    private int getMove(GameStateModule var1) {
        int var2 = 0;

        int var3;
        for(var3 = 0; var3 < var1.getWidth(); ++var3) {
            if (var1.canMakeMove(var3)) {
                this.moves[var2++] = var3;
            }
        }

        var3 = this.r.nextInt(var2);
        return this.moves[var3];
    }

    private void updateGuess(int var1, int var2, int[] var3, int var4) {
        if (var2 != 0) {
            var3[var4] += var2 == var1 ? 1 : -1;

            for(int var5 = 0; var5 < var3.length; ++var5) {
                if (var3[var5] > var3[this.chosenMove]) {
                    this.chosenMove = var5;
                }
            }

        }
    }

    private int playRandomGame(GameStateModule var1) {
        GameStateModule var2 = var1.copy();

        while(!var2.isGameOver()) {
            var2.makeMove(this.getMove(var2));
        }

        return var2.getWinner();
    }
}
