Deep Reinforcememt(Q) Learning algorithm: 
Teach the Atari pong game to play agains an opponent. Took me more than 10 hours to train the model.pth on the Google Cloud virtual machine with Tesla K80 Graphic card. Implemented Q learning algorithm. Used PyTorch. Check out the graphs to observe the progress.
 
Usage: 
To train the new model: python run_dqn_pong.py
To test the trained model: python test_dqn_pong.py model.pth
	To see the game board: python test_dqn_pong.py model.pth -g 