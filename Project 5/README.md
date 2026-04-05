# BattleShip: A game made in Java and JavaFX

**Introduction**

This was a month-long coding project assigned to a team of four individuals. It was a result of numerous designated team meetings, both online and offline, to plan, execute and streamline our product. Even though each team member had their own designated tasks, either in frontend or backend development, this project was a good way for us to exercise our skills in both aspects of creating an application as we helped each other frequently.

**Features Used**

Java 15, JavaFX

**Summary**

Since it is a very complex project, I will attempt to summarize our overall thinking and direction that we decided to take with this project. 

Backend development:

At first, we began looking through the internet for similar examples of projects that we can draw inspiration from with respect to the structuring of the application. After much discussions, we decided with building the overall board where our game would be held, and work our way inwards, concluding that it would be easier to visualize the battleships themselves and any other features we add if we finish building the playing map first. The board was a 2d array of ints that served as coordinates.

We also needed to build the ship models; since our board was a 2d array of integers, it made designing the different size of the ships easy as well as their placements on the board. The ships could be placed vertically or horizontally on the board.

After our board and ship designs were finalized, we began creating algorithms that the computer can utilize to play against the user. There were three difficulties that the user could set the computer to: Easy, Intermediate and Difficult. 
Easy mode practically made the computer choose random coordinates on the board, regardless of whether a ship was hit or not.
Intermediate mode would initially begin with random coordinates, but if it managed to hit an enemy ship, it would limit it's choice of the next coordinate to hit within a 3x3 square of the hit mark to increase chances of hitting another part of the ship. But if no more ships were hit, the condition would essentially reset, meaning the computer would forget that it hit the ship at a certain location before and look there when it's next turn arrives.
Hard mode would also initially begin by randomly choosing coordinates, but once it hit a ship, it would choose from one of the four possible spots the ship's part can be in: top, bottom, left or right block. If it hits another part on the second attempt, it will keep going in that direction until it sinks the entire ship, and then choose another random location on the board. If it doens't manage to sink the entire ship, it remembers the location where it hit the ship first, and scans other directions during it's next attempt to try and sink the ship.

Furthermore, we decided to add bonus powerups that the user can utilize if they had numerous successful hits in a row. 
The powerups included selecting a certain square that you can hit all at the same time, or selecting a row/column that you can hit all at once. 
We also included a cheat that allows the user to see the locations of all of the computer's ships.

We also added a leaderboard feature that used serialization to ensure past high scores would not get removed from the local space if it existed. The leaderboard displayed statistics like accuracy and number of moves executed to win the game.

Frontend development:

Our aim with the user interface and the overall mood of the application was to create a simple yet humorous experience for the user. Since we had many features, we had to create multiple interfaces using GUI, such as a start menu, choosing the difficulty, and placing the ship on the board before the game would begin. We also used image overlaying for the ships, the background and the hit/miss animation to create an immersive experience for the user. We also added music, sound animations for when ships were hit/missed and also when the game was won or lost. 
