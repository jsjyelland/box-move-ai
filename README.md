# Box Mover AI

This program organises boxes in a virtual environment using pathfinding algorithms to move boxes from a start to a goal position in an environment cluttered with moveable and immoveable obstacles.

## Input Format
The input of the program is a text file containing information on the robot and problem scenario. In particular, the file follows the following format:
* The first line consists of 4 numbers, where the first number is the value of w, while the subsequent two numbers represent the initial (x, y) position and the last number represents the orientation 𝛼 ∈ [0, 2𝜋]rad.
* The second line consists of 3 numbers separated by a single white space. The numbers represent the number of moving boxes (m), the number of movable obstacles (n), and the number of static obstacles (o), respectively.
* Each line-i, where i ∈ [3, m+2] consists of 4 numbers separated by a single white space, representing the initial and goal positions of the (i-2)th moving box. The first two number in line-i is the initial (x, y) position of the (i-2)th moving box, while the last two numbers is the goal (x, y) position.
* Each line-i, where i ∈ [m+3, m+n+2] consists of 3 numbers separated by a single white space, representing the position and size of movable obstacles. In particular, the first two numbers represent the initial (x, y) position of the (i-m-2)th movable obstacle, while the last number represent the width of the obstacle.
* Each line-i, where i ∈ [m+n+3, m+n+o+2] consists of 4 numbers separated by a single white space, representing the position and geometry of the static obstacles. The first two numbers represent the (x, y) position of the lower left vertex of the obstacle, while the last two represents the (x, y) position of the upper right vertex of the obstacle.

## Running

The solution finder program is under ```src/solution/Main.java```. It takes as arguments the input text file and output text file.
The visualiser can be used to present the solution. It is under ```src/visualiser/Visualiser.java```. It takes no arguments; the files are loaded within the GUI.
