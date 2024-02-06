package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Server.SudokuPuzzle;
public interface SudokuGeneratorInterface extends Remote{
    public SudokuPuzzle getPuzzle(int K) throws RemoteException;
    public void cleanUp() throws RemoteException;

    public void receiveStat(Stat stat) throws RemoteException;
}
