package edu.project2

import edu.project2.Maze.Maze
import edu.project2.generators.MazeGenerator
import edu.project2.generators.chaoticMaze.ChaoticMazeGenerator
import edu.project2.generators.idealMaze.IdealMazeGenerator
import edu.project2.solvers.BFSsolver
import edu.project2.solvers.DFSsolver
import edu.project2.solvers.MazeSolver
import org.apache.logging.log4j.LogManager
import java.util.*

val LOGGER = LogManager.getLogger()
lateinit var sc: Scanner


var height: Int = -1
var width: Int = -1
var printBound: Boolean = false
var wallChance: Int = 50


var generator: MazeGenerator = null!! //???
var maze: Maze = null!!
var solvedMaze: Maze = null!!
var solver: MazeSolver = null!!
fun main() {

    sc = Scanner(System.`in`)

    LOGGER.info(
        """
        WELCOME TO MAZE GENERATOR!
        Select maze generating algorithm:
            1 for chaotic maze
            2 for ideal maze
        """
    )

    var mazeType = -1

    while (mazeType !in 1..2) {
        LOGGER.info("type either '1' or '2'")
        mazeType = sc.nextInt()
    }

    if (mazeType == 1) {
        LOGGER.info("You've chosen chaotic maze")
        setUpChaoticMaze()
    } else {
        LOGGER.info("You've chosen ideal maze")
        setUpChaoticMaze()
    }


    LOGGER.info(
        """
        Select maze solving algorithm:
            1 for bfs
            2 for dfs
        """
    )
    var solverType = -1
    while (solverType !in 1..2) {
        LOGGER.info("type either '1' or '2'")
        solverType = sc.nextInt()
    }

    solver =
        if (mazeType == 1) {
            LOGGER.info("You've chosen bfs")
            BFSsolver()
        } else {
            LOGGER.info("You've chosen dfs")
            DFSsolver()
        }
}

private fun setUpChaoticMaze() {
    generator = ChaoticMazeGenerator()

    getDefaultMazeInfo()

    LOGGER.info(
        "Input integer from [0;100] that is chance of getting wall " +
            "(the less number - the less walls if the maze)"
    )
    wallChance = sc.nextInt()

    maze = (generator as ChaoticMazeGenerator).getMaze(height, width, wallChance)

    maze.printMaze()

    LOGGER.info("Do you want to regenerate it? (y/n)")
    var choice = sc.nextLine()[0]
    while (choice !in "yn")
        choice = sc.nextLine()[0]

    if(choice == 'y')
        setUpIdealMaze()
}


private fun setUpIdealMaze() {
    generator = IdealMazeGenerator()

    getDefaultMazeInfo()

    maze = (generator as IdealMazeGenerator).getMaze(height, width)

    maze.printMaze()

    LOGGER.info("Do you want to regenerate it? (y/n)")
    var choice = sc.nextLine()[0]
    while (choice !in "yn")
        choice = sc.nextLine()[0]

    if(choice == 'y')
        setUpIdealMaze()

}

private fun getDefaultMazeInfo() {
    LOGGER.info("Input height of the maze")
    height = sc.nextInt()

    LOGGER.info("Input width of the maze")
    width = sc.nextInt()

    LOGGER.info("Do you want to print bound of your maze? (y/n)")
    var choice = sc.nextLine()[0]
    while (choice !in "yn")
        choice = sc.nextLine()[0]

    printBound =
        if (choice == 'y')
            true
        else
            false
}
