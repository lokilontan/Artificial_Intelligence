from collections import deque
import numpy as np
import matplotlib.pyplot as plt
import torch
import torch.nn as nn
import torch.autograd as autograd
import math, random
USE_CUDA = torch.cuda.is_available()
Variable = lambda *args, **kwargs: autograd.Variable(*args, **kwargs).cuda() if USE_CUDA else autograd.Variable(*args, **kwargs)

class QLearner(nn.Module):
    def __init__(self, env, num_frames, batch_size, gamma, replay_buffer):
        super(QLearner, self).__init__()

        self.batch_size = batch_size
        self.gamma = gamma
        self.num_frames = num_frames
        self.replay_buffer = replay_buffer
        self.env = env
        self.input_shape = self.env.observation_space.shape
        self.num_actions = self.env.action_space.n

        self.features = nn.Sequential(
            nn.Conv2d(self.input_shape[0], 32, kernel_size=8, stride=4),
            nn.ReLU(),
            nn.Conv2d(32, 64, kernel_size=4, stride=2),
            nn.ReLU(),
            nn.Conv2d(64, 64, kernel_size=3, stride=1),
            nn.ReLU()
        )
        
        self.fc = nn.Sequential(
            nn.Linear(self.feature_size(), 512),
            nn.ReLU(),
            nn.Linear(512, self.num_actions)
        )
        
    def forward(self, x):
        x = self.features(x)
        x = x.view(x.size(0), -1)
        x = self.fc(x)
        return x
    
    def feature_size(self):
            return self.features(autograd.Variable(torch.zeros(1, *self.input_shape))).view(1, -1).size(1)
    
    def act(self, state, epsilon):
        #Epsilon gets greater with the number of learning frames.
        #Greater epsilon - less probability for if condition to be true
        #Random EXPLORATION at the start; EXPLOITATION at the end
        if random.random() > epsilon:
            state = Variable(torch.FloatTensor(np.float32(state)).unsqueeze(0), requires_grad=True)
            # TODO: Given state, you should write code to get the Q value and chosen action
            # Choose the best action to be performed. Hence, maximum reward for a particular action
            # Exploitation
            action = torch.argmax(self(state)).item()

        else:
            # Randomly select a valid action
            # Exploration
            action = random.randrange(self.env.action_space.n)
            
        return action

    def copy_from(self, target):
        self.load_state_dict(target.state_dict())

        
def compute_td_loss(model, target_model, batch_size, gamma, replay_buffer):
    state, action, reward, next_state, done = replay_buffer.sample(batch_size)

    state = Variable(torch.FloatTensor(np.float32(state))).squeeze(1)
    next_state = Variable(torch.FloatTensor(np.float32(next_state)).squeeze(1), requires_grad=True)
    action = Variable(torch.LongTensor(action))
    reward = Variable(torch.FloatTensor(reward))
    done = Variable(torch.FloatTensor(done))

    # implement the loss function here

    #Get the Q(s,a;Theta)
    actual_q = model(state).gather(1,action.unsqueeze(-1)).squeeze(-1)
    
    #Start computing y_i
    #Get max(Q(s',a';Theta))_i
    expected_y = target_model(next_state).detach().max(1)[0] #check what is this...
    #Multiply each max(Q(s',a';Theta))_i by gamma
    expected_y *= gamma #check if changed from previous
    #Just a tensor of zeros
    zero = torch.zeros(32)
    #Condition each max(Q(s',a';Theta))_i * gamma
        #If done is 1(terminated) then place a zero at that place (max(Q(s',a';Theta))_i * gamma won't contribute)
        #If done is 0(not the terminal state) then keep the old value
    expected_y = torch.where(done != 0., zero, expected_y)
    #Add reward_i to y_i
    expected_y = expected_y.add(reward)

    #Assign this important flag
    expected_y.requires_grad = True

    #Use MSELoss to sum app all the (expected_y_i - actual_q_i)^2
    loss_fn = nn.MSELoss(reduction='sum')
    loss = loss_fn(expected_y, actual_q)
    
    return loss

class ReplayBuffer(object):
    def __init__(self, capacity):
        self.buffer = deque(maxlen=capacity)

    def push(self, state, action, reward, next_state, done):
        state = np.expand_dims(state, 0)
        next_state = np.expand_dims(next_state, 0)

        self.buffer.append((state, action, reward, next_state, done))

    def sample(self, batch_size):
        # TODO: Randomly sampling data with specific batch size from the buffer
        temp = random.sample(self.buffer, 32)   #Get a random sample of size 32 from the buffer. Output is a nested list
        batch = list(zip(*temp))          #Nested list is kind of a matrix. This statement makes i_th column to be the i_th row
        state = batch[0]                  #Now retrieve each row as a list of needed valiues
        action = batch[1]                 #...
        reward = batch[2]
        next_state = batch[3]
        done = batch[4]
        return state, action, reward, next_state, done

    def __len__(self):
        return len(self.buffer)
