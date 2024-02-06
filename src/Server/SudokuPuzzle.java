package Server;

import java.io.Serializable;
//class to store the sudoku puzzle and its solution. It is serializable so that it can be sent over the network
@SuppressWarnings("unused")
public class SudokuPuzzle implements Serializable {
    int[][] puzzle;
    int[][] solution;
    private static final int SIZE = 9;
    public SudokuPuzzle(){
        puzzle = new int[SIZE][SIZE];
        solution = new int[SIZE][SIZE];
    }
    public SudokuPuzzle(int[][] puzzle, int[][] solution){
        this.puzzle = puzzle;
        this.solution = solution;
    }
    public int[][] getPuzzle(){
        return puzzle;
    }
    public int[][] getSolution(){
        return solution;
    }
    public void setPuzzle(int[][] puzzle){
        this.puzzle = puzzle;
    }
    public void setSolution(int[][] solution){
        this.solution = solution;
    }
    public void printPuzzle(){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                System.out.print(puzzle[i][j] + " ");
            }
            System.out.println();
        }
    }
    public void printSolution(){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                System.out.print(solution[i][j] + " ");
            }
            System.out.println();
        }
    }

}
