package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Server.SudokuPuzzle;
//interface for the sudoku generator.
public interface GameServerInterface extends Remote{
    public SudokuPuzzle getPuzzle(int K) throws RemoteException;
    public void cleanUp() throws RemoteException;

    public void receiveStat(Stat stat) throws RemoteException;
}
