package Client;

import java.util.ArrayList;
import Server.SudokuGeneratorInterface;
import Server.SudokuPuzzle;
import Server.Stat;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Sudoku {
    private final ArrayList<SudokuMove> moves = new ArrayList<>();
    private final ArrayList<SudokuMove> undoneMoves = new ArrayList<>();
    private int[][] board = new int[9][9];
    private int[][] solution = new int[9][9];




    public Sudoku() {
        initialBoard();
    }
    public int moveCount(){
        return moves.size();
    }
    public int undoneMoveCount(){
        return undoneMoves.size();
    }

    public void startup(String difficulty) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);

            SudokuGeneratorInterface remoteObject = (SudokuGeneratorInterface) registry.lookup("SudokuServer");
            SudokuPuzzle temp = null;

            switch (difficulty) {
                case "easy" -> temp = remoteObject.getPuzzle(41);
                case "medium" -> temp = remoteObject.getPuzzle(51);
                case "hard" -> temp = remoteObject.getPuzzle(1);//TODO: change back to 60
                default -> temp = remoteObject.getPuzzle(50);
            }
            this.board = temp.getPuzzle();
            this.solution = temp.getSolution();
            remoteObject.cleanUp();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void sendStats(int time){
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            SudokuGeneratorInterface remoteObject = (SudokuGeneratorInterface) registry.lookup("SudokuServer");
            remoteObject.receiveStat(new Stat("Test",time,69));//TODO
            remoteObject.cleanUp();
        } catch (Exception e) {
            System.out.println(e);
        }


    }
    public void solve() {
        board = solution;
    }
    public boolean addMove(SudokuMove move) {
        if (isValidMove(move)) {
            moves.add(move);
            board[move.getX()][move.getY()] = move.getValue();
            undoneMoves.clear();
            return true;
        }
        return false;
    }
    public SudokuMove undoMove() {
        if (!moves.isEmpty()) {
            SudokuMove move = moves.remove(moves.size() - 1);
            board[move.getX()][move.getY()] = 0;
            undoneMoves.add(move);
            return move;
        }
        return null;
    }
    public SudokuMove redoMove() {
        if (!undoneMoves.isEmpty()) {
            SudokuMove move = undoneMoves.remove(undoneMoves.size() - 1);
            moves.add(move);
            board[move.getX()][move.getY()] = move.getValue();
            return move;
        }
        return null;
    }

    public boolean isSolved() {
        for( int x = 0; x < 9; x++ ) {
            for( int y = 0; y < 9; y++ ) {
                if( board[x][y] == 0 ) {
                    return false;
                }
            }
        }
        return true;
    }
    public int[][] getBoard() {
        return board;
    }
    public void reset() { moves.clear(); initialBoard();}
    private void initialBoard() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                board[x][y] = 0;
            }
        }
    }
    private boolean isValidMove(SudokuMove move) {
        int x = move.getX();
        int y = move.getY();
        int value = move.getValue();
        if (board[x][y] != 0) {
            return false;
        }
        for (int i = 0; i < 9; i++) {
            if (board[x][i] == value) {
                return false;
            }
            if (board[i][y] == value) {
                return false;
            }
        }
        int x0 = (x / 3) * 3;
        int y0 = (y / 3) * 3;
        for (int i = x0; i < x0 + 3; i++) {
            for (int j = y0; j < y0 + 3; j++) {
                if (board[i][j] == value) {
                    return false;
                }
            }
        }
        return true;
    }
}
