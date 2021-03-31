import matplotlib.pyplot as plt
import numpy as np
import math
import csv
import pandas as pd

r_csv = pd.read_csv('/Users/lokilontan/Documents/College/ECS - 170/Projects/AI_Course/DeepQL_for_Pong_game/rewards.csv')
frameR_idx_x_axis = r_csv.iloc[ : , 0 ]
rewards_y_axis = r_csv.iloc[ : , 1 ]
plt.plot(frameR_idx_x_axis,rewards_y_axis, 'r.')
plt.xlabel("Number of Frames")
plt.ylabel("Reward")
plt.title('Reward vs. Frames')
plt.plot(np.unique(frameR_idx_x_axis), np.poly1d(np.polyfit(frameR_idx_x_axis, rewards_y_axis, 1))(np.unique(frameR_idx_x_axis)))

plt.show()

l_csv = pd.read_csv('/Users/lokilontan/Documents/College/ECS - 170/Projects/AI_Course/DeepQL_for_Pong_game/losses.csv')
frameL_idx_x_axis = l_csv.iloc[ : , 0 ]
losses_y_axis = l_csv.iloc[ : , 1 ]
plt.plot(frameL_idx_x_axis,losses_y_axis, 'y.')
plt.xlabel("Number of Frames")
plt.ylabel("Losses")
plt.title('Losses vs. Frames')
plt.plot(np.unique(frameL_idx_x_axis), np.poly1d(np.polyfit(frameL_idx_x_axis, losses_y_axis, 1))(np.unique(frameL_idx_x_axis)))

plt.show()