package Client;

import java.util.ArrayList;

import Server.GameServerInterface;
import Server.SudokuPuzzle;
import Server.Stat;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Sudoku {
    private final ArrayList<SudokuMove> moves = new ArrayList<>(); //moves made by the player
    private final ArrayList<SudokuMove> undoneMoves = new ArrayList<>(); // moves that were undone
    private static final int SIZE = 9;
    private static final int SERVER_PORT = 8000;
    private int[][] board = new int[SIZE][SIZE];
    private int[][] solution = new int[SIZE][SIZE];
    private int wrongMoves = 0;



    public Sudoku() {
        initialBoard();
    }
    public int moveCount(){
        return moves.size();
    }
    public int undoneMoveCount(){
        return undoneMoves.size();
    }
    public int[][] getBoard() {
        return board;
    }
    public int getScore(int time){
        //score is calculated by the number of moves made, the number of wrong moves, and the time taken
        return (int) (moves.size()*2 - wrongMoves*3 - 0.01*time);
    }

    public void startup(String difficulty) {
        reset();
        //Connect to the server and get a puzzle
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", SERVER_PORT);

            GameServerInterface remoteObject = (GameServerInterface) registry.lookup("SudokuServer");
            SudokuPuzzle temp;

            switch (difficulty) {
                case "easy" -> temp = remoteObject.getPuzzle(41);
                case "medium" -> temp = remoteObject.getPuzzle(51);
                case "hard" -> temp = remoteObject.getPuzzle(5);//TODO: change back to 60
                default -> temp = remoteObject.getPuzzle(50);
            }
            this.board = temp.getPuzzle();
            this.solution = temp.getSolution();
            //After getting the puzzle, clear the server's records and close the connection
            remoteObject.cleanUp();

        } catch (Exception e) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        wrongMoves = 0;
    }
    public void sendStats(int time, String name){
        //When the game is over, send the stats to the server (Supposedly this is called when the game is over)
        int score = getScore(time);
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", SERVER_PORT);
            GameServerInterface remoteObject = (GameServerInterface) registry.lookup("SudokuServer");
            remoteObject.receiveStat(new Stat(name,time,score));
            //After sending the stats, close the connection
            //Clean up the server's records (Although this is not necessary in this case)
            remoteObject.cleanUp();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    public void solve() {
        board = solution;
    }
    public boolean addMove(SudokuMove move) {
        //add a move to the board. If the move is invalid, return false and increment wrongMoves
        if (isValidMove(move)) {
            //This allows the player to overwrite a move
            if(board[move.getX()][move.getY()] != 0){
                removeMove(move);
            }
            moves.add(move);
            board[move.getX()][move.getY()] = move.getValue();
            //if the move is valid, clear the undoneMoves list(They will be overwritten by the new moves)
            undoneMoves.clear();
            return true;
        }
        wrongMoves++;
        return false;
    }
    public SudokuMove undoMove() {
        //undo the last move made and add it to the undoneMoves list
        if (!moves.isEmpty()) {
            SudokuMove move = moves.remove(moves.size() - 1);
            board[move.getX()][move.getY()] = 0;
            undoneMoves.add(move);
            return move;
        }
        return null;
    }
    public SudokuMove redoMove() {
        //redo the last move made and add it to the moves list
        if (!undoneMoves.isEmpty()) {
            SudokuMove move = undoneMoves.remove(undoneMoves.size() - 1);
            moves.add(move);
            board[move.getX()][move.getY()] = move.getValue();
            return move;
        }
        return null;
    }
    //remove a move from the board
    public void removeMove(SudokuMove move) {
        board[move.getX()][move.getY()] = 0;
        //remove the move from the moves list
        for (SudokuMove sudokuMove : moves) {
            int currX = sudokuMove.getX();
            int currY = sudokuMove.getY();
            if (currX == move.getX() && currY == move.getY()) {
                moves.remove(sudokuMove);
                break;
            }
        }
        //moves.clear();
        undoneMoves.clear();

    }
    public void reset() {
        moves.clear();
        wrongMoves = 0;
        initialBoard();
    }
    private void initialBoard() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                board[x][y] = 0;
            }
        }
    }
    public boolean isSolved() {
        //Considering that we made only valid moves, the board is solved if there are no empty cells
        for( int x = 0; x < SIZE; x++ ) {
            for( int y = 0; y < SIZE; y++ ) {
                if( board[x][y] == 0 ) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isValidMove(SudokuMove move) {
        //check if the move is valid by checking if the value is already in the row, column, or 3x3 square
        int x = move.getX();
        int y = move.getY();
        int value = move.getValue();

        //if (board[x][y] != 0) { return false; }

        for (int i = 0; i < SIZE; i++) {
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
