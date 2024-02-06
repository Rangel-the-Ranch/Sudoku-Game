package Server;

import java.io.Serializable;

public class SudokuPuzzle implements Serializable {
    int[][] puzzle;
    int[][] solution;
    public SudokuPuzzle(){
        puzzle = new int[9][9];
        solution = new int[9][9];
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
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                System.out.print(puzzle[i][j] + " ");
            }
            System.out.println();
        }
    }
    public void printSolution(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                System.out.print(solution[i][j] + " ");
            }
            System.out.println();
        }
    }

}
