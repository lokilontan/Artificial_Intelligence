// (c) Ian Davidson, Leo Shamis U.C. Davis 2019

import java.util.*;

/// Sample AI module that picks random moves at each point.
/**
 * This AI performs random actions.  It is meant to illustrate the basics of how to
 * use the AIModule and GameStateModule classes.
 *
 * Since this terminates in under a millisecond, there is no reason to check for
 * the terminate flag.  However, your AI needs to check for the terminate flag.
 *
 * @author Leonid Shamis
 */
public class RandomAI extends AIModule
{
	public void getNextMove(final GameStateModule game)
	{
		final Random r = new Random();
		// set chosenMove to a random column
		int move = r.nextInt(game.getWidth());
		while(!game.canMakeMove(move))
			move = r.nextInt(game.getWidth());
		chosenMove = move;

		System.out.println("Evaluation test(diagonal \\): " + eval(game, game.getActivePlayer()));
	}

	private int eval(GameStateModule game, int initP) {
		int boardEval = 0;
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

		//First trial of some good evaluation function.

		//For each coin on the board
		for (int i = 0; i < game.getWidth(); i++) {
			for (int j = 0; j < game.getHeight(); j++) {
				playerAt = game.getAt(i, j);
				if (playerAt != 0) {
					if (playerAt == initP) {
						//Find out how many potential wins the coin has(point for each) and how many coins are already placed for this win (point for each)
						boardEval += potentialWinsOf(game, initP, i, j, 1);
						//Find out how many potential blocks the coin makes (point for each) and how many enemy's coins are already placed for that win (point for each)
						//boardEval += potentialWinsOf(game, nonInitPlayer(initP), i, j);
					} else {
						//Same for the opponent
						//boardEval -= potentialWinsOf(game, nonInitPlayer(initP), i, j);
						//boardEval -= potentialWinsOf(game, initP, i, j);
					}
				}
			}
		}

		return boardEval;
	}

	//This function returns the sum of the potential wins and the player's coins placed for this win (v1.0)
	//Update this function so now it counts empty spots(gets min reward), counts player's coins (gets n*reward); the main difference from the v1.0 - does not stop if finds enemy's coin. AND IT WORKS (play with the reward distribution) (v2.0)
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
						tempCoins += 2*reward;
					} else if (game.getAt(ind, j) == player ) {
						tempCoins += (10*reward);
					}
				}
				for (int ind = i+1; ind <= (i + (3-k)); ind++) {
					if (game.getAt(ind, j) == 0) {
						tempCoins += 2*reward;
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
						tempCoins += 2*reward;
					} else if (game.getAt(i, ind) == player ) {
						tempCoins += (10*reward);
					}
				}
				for (int ind = j+1; ind <= (j + (3-k)); ind++) {
					if (game.getAt(i, ind) == 0) {
						tempCoins += 2*reward;
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
						tempCoins += 2*reward;
					} else if (game.getAt(indI, indJ) == player ) {
						tempCoins += (10*reward);
					}
					indJ++;
				}
				indJ = j+1;
				for (int indI = i+1; indI <= (i + (3-k)); indI++) {
					if (game.getAt(indI, indJ) == 0) {
						tempCoins += 2*reward;
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
						tempCoins += 2*reward;
					} else if (game.getAt(indI, indJ) == player ) {
						tempCoins += (10*reward);
					}
					indJ++;
				}
				indJ = j+1;
				for (int indI = i-1; indI >= (i - (3-k)); indI--) {
					if (game.getAt(indI, indJ) == nonInitPlayer(player)) {
						tempCoins += 2*reward;
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
}


