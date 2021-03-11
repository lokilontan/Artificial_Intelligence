//Vladimir Plagov
//Minimax implementation

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class AlphaBeta extends AIModule {
    public void getNextMove(final GameStateModule game) {

        int initialActivePlayer = game.getActivePlayer();
        chosenMove = maxValue(game, 8, initialActivePlayer, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).move;
        //System.out.println("Evaluation: " + eval(game, initialActivePlayer));

    }

    private Pair maxValue(GameStateModule game, int depth, int initialActivePlayer, double alpha, double beta) {
        if (this.terminate) {
            //System.out.println("Terminate from MAX");
            return new Pair(eval(game, initialActivePlayer), -1);
        } else if (depth == 0) {
            //System.out.println("Reached the depth at MAX");
            return new Pair(eval(game, initialActivePlayer), -1);
        } else if (game.isGameOver()) {
            //System.out.println("Game is over at MAX; Winner: " + game.getWinner());
            return new Pair(eval(game, initialActivePlayer), -1);
        }
        double value = Double.NEGATIVE_INFINITY;
        double a = alpha;
        double b = beta;
        Pair finalPair = new Pair();
        finalPair.value = value;

        int[] priority  = new int[] {3,2,4,1,5,0,6};
        //int[] priority2 = successor(game, initialActivePlayer);
        for (int i = 0; i < priority.length; i++) {
            if (game.canMakeMove(priority[i])) {
                game.makeMove(priority[i]);
                Pair pair = new Pair();
                pair = minValue(game, depth - 1, initialActivePlayer, a, b);
                game.unMakeMove();
                if (pair.value > value) {
                    value = pair.value;
                    finalPair.value = value;
                    finalPair.move = priority[i];
                    a = Math.max(a, value);
                }
                if (value >= b) {
                    return finalPair;
                }
            }
        }
        return finalPair;
    }

    private Pair minValue(GameStateModule game, int depth, int initialActivePlayer, double alpha, double beta) {
        if (this.terminate) {
            //System.out.println("Terminate from MIN");
            return new Pair(eval(game, initialActivePlayer), -1);
        } else if (depth == 0) {
            //System.out.println("Reached the depth MIN");
            return new Pair(eval(game, initialActivePlayer), -1);
        } else if (game.isGameOver()) {
            //System.out.println("Game is over at MIN; Winner: " + game.getWinner());
            return new Pair(eval(game, initialActivePlayer), -1);
        }
        double value = Double.POSITIVE_INFINITY;
        double a = alpha;
        double b = beta;
        Pair finalPair = new Pair();
        finalPair.value = value;


        int[] priority  = new int[] {3,2,4,1,5,0,6};
        //int[] priority2 = successor(game, initialActivePlayer);
        for (int i = 0; i < priority.length; i++) {
            if (game.canMakeMove(priority[i])) {
                game.makeMove(priority[i]);
                Pair pair = new Pair();
                pair = maxValue(game, depth - 1, initialActivePlayer, a, b);
                game.unMakeMove();
                if (pair.value < value) {
                    value = pair.value;
                    finalPair.value = value;
                    finalPair.move = priority[i];
                    b = Math.min(b, value);
                }
                if (value <= a) {
                    return finalPair;
                }
            }
        }
        return finalPair;
    }

    private int[] successor(GameStateModule game, int initialActivePlayer) {
        double evaluations[] = new double[7];
        double temp[] = new double[7];
        int positions[] = new int[7];

        //Find first possible move for future comparisons
        for (int i = 0; i <= 6; i++) {
            if (game.canMakeMove(i)) {
                game.makeMove(i);
                evaluations[i] = eval(game, initialActivePlayer);
                temp[i] = evaluations[i];
                positions[i] = i;
                game.unMakeMove();
            }
        }
        Arrays.sort(evaluations);
        for (int i = 6; i >= 0; i--) {
            positions[6-i] = Arrays.asList(temp).indexOf(evaluations[i]);
        }

        return positions;
    }

    private double eval(GameStateModule game, int initP) {
        double boardEval = 0.0;
        int playerAt ;

        //Initial "stupid" evaluation function. Evaluates higher coins closer to the center.
        /*
        for (int i = 0; i < game.getWidth(); i++) {
            for (int j = 0; j < game.getHeight(); j++) {
                playerAt = game.getAt(i, j);
                if (playerAt != 0) {
                    if (playerAt == initP) {
                        boardEval += (4 - Math.abs(4 - (i + 1)));
                    } else {
                        boardEval -= (4 - Math.abs(4 - (i + 1)));
                    }
                }
            }
        }
         */

        //Another "stupid" evaluation function. Evaluates only winning states
        /*
        if (game.isGameOver() && game.getWinner() == initP) {
            boardEval = Integer.MAX_VALUE;
        } else if (game.isGameOver() && game.getWinner() != 0) {
            boardEval = Integer.MIN_VALUE;
        }
        */

        if (game.isGameOver() && game.getWinner() == initP) {
            return 1500;
        } else if (game.isGameOver() && game.getWinner() == nonInitPlayer(initP) ) {
            return -1500;
        }


        //First trial of some good evaluation function.

        //For each coin on the board
        for (int i = 0; i < game.getWidth(); i++) {
            for (int j = 0; j < game.getHeight(); j++) {
                playerAt = game.getAt(i, j);
                if (playerAt != 0) {
                    if (playerAt == initP) {
                        //boardEval += (16*(4 - Math.abs(4 - (i + 1))));
                        //Find out how many potential wins the coin has(point for each) and how many coins are already placed for this win (point for each)
                        boardEval += potentialWinsOf(game, initP, i, j, 2);
                        //Find out how many potential blocks the coin makes (point for each) and how many enemy's coins are already placed for that win (double reward for each)
                        boardEval += potentialWinsOf(game, nonInitPlayer(initP), i, j, 4);
                    } else {
                        //Same for the opponent
                        //boardEval -= (16*(4 - Math.abs(4 - (i + 1))));
                        boardEval -= potentialWinsOf(game, nonInitPlayer(initP), i, j, 2);
                        boardEval -= potentialWinsOf(game, initP, i, j, 4);
                    }
                }
            }
        }

        return boardEval;
    }

    //(v1.0) This function returns the sum of the potential wins and the player's coins placed for this win
    //(v2.0) Update this function so now it counts empty spots(gets min reward), counts player's coins (gets n*reward); the main difference from the v1.0 - does not stop if finds enemy's coin. AND IT WORKS (play with the reward distribution)
    //(v3.0) The evaluation process from v2.0 does not place first coin exactly in the center. In v3.0 I will also decrease the reward if I will find enemy's coin. v3.0 does not work as good as v2.0. Will stick to v2.0 and hope in A-B pruning it will choose the middle column
    private double potentialWinsOf(GameStateModule game, int player, int i, int j, int reward) {
        double coins = 0.0;
        int rewardPower = 0;
        double tempCoins;
        double wins = 0.0;


        //Check all possible horizontal combinations of 4 tiles. (Work with index i)
        //Start with the most left combination
        for (int k = 3; k >= 0 ; k--) {
            tempCoins = 0;
            label:
            if (((i-k) >= 0) && (i + (3-k)) < game.getWidth()) {
                for (int ind = i-k; ind < i; ind++) {
                    if (game.getAt(ind, j) == 0) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(ind, j) == player ) {
                        tempCoins += (10*reward);
                    }
                }
                for (int ind = i+1; ind <= (i + (3-k)); ind++) {
                    if (game.getAt(ind, j) == 0) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(ind, j) == player ) {
                        tempCoins += (10*reward);
                    }
                }
                //wins += 1;
                coins += tempCoins;
            }
        }

        //Check all possible vertical combinations of 4 tiles. (Work with index j)
        //Start with the lowest combination
        for (int k = 3; k >= 0 ; k--) {
            tempCoins = 0;
            label:
            if (((j-k) >= 0) && (j + (3-k)) < game.getWidth()) {
                for (int ind = j-k; ind < j; ind++) {
                    if (game.getAt(i, ind) == 0) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(i, ind) == player ) {
                        tempCoins += (10*reward);
                    }
                }
                for (int ind = j+1; ind <= (j + (3-k)); ind++) {
                    if (game.getAt(i, ind) == 0) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(i, ind) == player ) {
                        tempCoins += (10*reward);
                    }
                }
                //wins += 1;
                coins += tempCoins;
            }
        }

        //CHECK DIAGONALS

        //Check all possible (/) diagonals of 4 tiles.
        //Start with the lowest left combination
        for (int k = 3; k >= 0 ; k--) {
            tempCoins = 0;
            int indJ;
            label:
            if ( ( (i-k) >= 0 ) && ( (j-k) >=0 )  && ( (i + (3-k)) < game.getWidth() ) && ( (j + (3-k)) < game.getHeight() ) ) {
                indJ = j-k;
                for (int indI = i-k; indI < i; indI++) {
                    if (game.getAt(indI, indJ) == 0) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(indI, indJ) == player ) {
                        tempCoins += (10*reward);
                    }
                    indJ++;
                }
                indJ = j+1;
                for (int indI = i+1; indI <= (i + (3-k)); indI++) {
                    if (game.getAt(indI, indJ) == 0) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(indI, indJ) == player ) {
                        tempCoins += (10*reward);
                    }
                    indJ++;
                }
                //wins += 1;
                coins += tempCoins;
            }
        }


        //Check all possible (\) diagonals of 4 tiles.
        //Start with the lowest right combination
        for (int k = 3; k >= 0 ; k--) {
            tempCoins = 0;
            int indJ;
            label:
            if ( ( (i+k) < game.getWidth() ) && ( (j-k) >=0 )  && ( (i - (3-k)) >= 0 ) && ( (j + (3-k)) < game.getHeight() ) ) {
                indJ = j-k;
                for (int indI = i+k; indI > i; indI--) {
                    if (game.getAt(indI, indJ) == 0) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(indI, indJ) == player ) {
                        tempCoins += (10*reward);
                    }
                    indJ++;
                }
                indJ = j+1;
                for (int indI = i-1; indI >= (i - (3-k)); indI--) {
                    if (game.getAt(indI, indJ) == nonInitPlayer(player)) {
                        tempCoins += 5*reward;
                    } else if (game.getAt(indI, indJ) == player ) {
                        tempCoins += (10*reward);
                    }
                    indJ++;
                }
                //wins += 1;
                coins += tempCoins;
            }
        }

        return (coins); //+ 0.5 * (6-j)
    }


    int nonInitPlayer(int initPlayer) {
        return initPlayer == 1 ? 2 : 1;
    }

    static class Pair {
        double value;
        int move;

        Pair() {
            this.value = 0.0;
            this.move = 4;
        }

        Pair(double value, int move) {
            this.value = value;
            this.move = move;
        }

    }
}

