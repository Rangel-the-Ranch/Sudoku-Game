package server;

import types.SudokuPuzzle;
import types.Stat;
import client.GameServerInterface;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


public class GameServer extends UnicastRemoteObject implements GameServerInterface{
    public static void main(String[] args) throws RemoteException {
        //Test the SudokuGenerator
        GameServer sudoku = new GameServer();
        SudokuPuzzle temp = sudoku.getPuzzle(32);

        System.out.println("Generated Sudoku Puzzle:");
        temp.printPuzzle();

        System.out.println("\nSolution:");
        temp.printSolution();
    }

    @Override
    public void receiveStat(Stat stat) throws RemoteException {
        System.out.println("Received Stat: " + stat.toString());
        //Append the stat to the records file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RecordsFile, true))) {
            writer.write(stat.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending content to the file: " + e.getMessage());
        }
        generateStatsFile();
    }
    //Generate the stats file.
    private void generateStatsFile() {
        //Maps to store the total time, total score, and occurrences for each player
        Map<String, Integer> totalTimeMap = new HashMap<>();
        Map<String, Integer> totalScoreMap = new HashMap<>();
        Map<String, Integer> occurrencesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RecordsFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(StatisticsFile))) {
            //Read each line and update the maps
            String line;
            while ((line = reader.readLine()) != null) {
                String[] components = line.split(" ");

                String name = components[0];
                int time = Integer.parseInt(components[1]);
                int score = Integer.parseInt(components[2]);
                totalTimeMap.put(name, totalTimeMap.getOrDefault(name, 0) + time);
                totalScoreMap.put(name, totalScoreMap.getOrDefault(name, 0) + score);
                occurrencesMap.put(name, occurrencesMap.getOrDefault(name, 0) + 1);
            }
            //Write the stats to the file
            for (String name : totalTimeMap.keySet()) {
                int totalTime = totalTimeMap.get(name);
                int totalScore = totalScoreMap.get(name);
                int occurrences = occurrencesMap.get(name);

                String statsLine = String.format("%s Time:%d Score:%d Games:%d%n", name, totalTime, totalScore, occurrences);
                writer.write(statsLine);
            }

        } catch (IOException e) {
            System.err.println("Error generating stats: " + e.getMessage());
        }
    }
    private static final String RecordsFile = "records.txt";
    private static final String StatisticsFile = "stats.txt";
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private int[][] grid;
    private int[][] solution;
    private int cellsToKeep = 32;

    public SudokuPuzzle getPuzzle(int K) throws RemoteException{
        cellsToKeep = K;
        generate();
        return new SudokuPuzzle(grid, solution);
    }
    //clean up the grid and solution used after the puzzle is generated
    public void cleanUp() throws RemoteException{
        grid = new int[SIZE][SIZE];
        solution = new int[SIZE][SIZE];
    }


    public GameServer() throws RemoteException{
        grid = new int[SIZE][SIZE];
        solution = new int[SIZE][SIZE];
    }
    //https://geeksforgeeks.org/program-sudoku-generator/
    //https://medium.com/analytics-vidhya/sudoku-backtracking-algorithm-and-visualization-75adec8e860c
    private void generate() {
        fillGrid();
        fillSolution();
        removeCells();
    }

    private void fillGrid() {
        //Random random = new Random();
        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) {
            fillSubgrid(i, i);
        }
        solve(0, 0);
    }

    private void fillSolution() {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, solution[i], 0, SIZE);
        }
    }

    private void fillSubgrid(int startRow, int startCol) {
        //Fill 3x3 subgrid with random numbers
        //Random random = new Random();
        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        shuffleArray(values);

        int index = 0;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                grid[startRow + i][startCol + j] = values[index++];
            }
        }
    }
    //Solve the puzzle using backtracking
    private boolean solve(int row, int col) {
        if (row == SIZE - 1 && col == SIZE)
            return true;

        if (col == SIZE) {
            row++;
            col = 0;
        }

        if (grid[row][col] != 0)
            return solve(row, col + 1);

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(row, col, num)) {
                grid[row][col] = num;

                if (solve(row, col + 1))
                    return true;

                grid[row][col] = 0;
            }
        }
        return false;
    }
    //Check if the number is valid in the given cell
    private boolean isValid(int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (grid[row][x] == num || grid[x][col] == num || grid[row - row % SUBGRID_SIZE + x / SUBGRID_SIZE][col - col % 3 + x % SUBGRID_SIZE] == num)
                return false;
        }
        return true;
    }
    //Remove cells from the grid to create the puzzle
    private void removeCells() {
        Random random = new Random();

        while (cellsToKeep > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            if (grid[row][col] != 0) {
                grid[row][col] = 0;
                cellsToKeep--;
            }
        }
    }
    //Fisher-Yates shuffle algorithm
    //https://www.geeksforgeeks.org/shuffle-a-given-array-using-fisher-yates-shuffle-algorithm/
    private void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
