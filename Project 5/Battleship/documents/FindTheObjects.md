1. GameController: Manages the overall game state, including transitioning between different stages of the game (start menu, ship placement, 
game loop, game over screen), handling user inputs, and coordinating interactions between the player and the computer AI.

2. Player: Represents a player in the game.  The Player object could manage the player's fleet, track the status of ships, 
and handle actions such as placing ships and taking shots.

3. Board: Represents the game board. The Board needs to handle operations like adding ships, marking hits and misses, 
and checking for ship destruction.

4. Ship: Represents a ship in the game. This object would store information such as the type of ship (Battleship, Cruiser, etc.), 
its size, orientation (horizontal or vertical), and coordinates on the board. It would also track damage and determine if the ship 
has been sunk.

5. AI: Manages the computer player's strategy and decision-making. Need to implement an easy AI and a hard AI

6. UIManager: Manages the user interface, including displaying the start menu, boards, animations for hits and misses, notifications 
for sunk ships, and the game over screen. 
