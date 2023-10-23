package edu.project2.solvers

import edu.project2.Maze.Cell
import edu.project2.Maze.CellType
import edu.project2.Maze.Maze
import java.util.*

class BFSSolver : MazeSolver() {

    public override fun solve(mazeParam: Maze, start: Cell, end: Cell): Maze {

        val maze = mazeParam.clone()

        if (maze.getCellType(start) != CellType.PASSAGE)
            throw IllegalArgumentException("Start cell is not a passage")

        if (maze.getCellType(end) != CellType.PASSAGE)
            throw IllegalArgumentException("End cell is not a passage")


        val queue: Queue<Cell> = LinkedList()

        queue.add(start)
        val ancestors = List(maze.height) { _ ->
            List(maze.width) { Cell(-1, -1) }
        }


        val visited = hashSetOf<Cell>()

        while (queue.isNotEmpty()) {
            val currentCell = queue.poll()
            visited.add(currentCell)
            val nextCells = maze.getAdjacentCells(currentCell).filter {
                it !in visited && maze.getCellType(it) == CellType.PASSAGE
            }

            for (cell in nextCells) {

                val mutableAncestors = ancestors.toMutableList()                        //?????
                mutableAncestors[cell.row] = mutableAncestors[cell.row].toMutableList()    //??????
                (mutableAncestors[cell.row] as MutableList<Cell>)[cell.column] = cell         //?????
                mutableAncestors[cell.row] = mutableAncestors[cell.row].toList()

                queue.add(cell)
                if (cell.row == end.row && cell.column == end.column)
                    break
            }
        }
        return buildSolvedMaze(maze, start, end, ancestors)
    }
}
