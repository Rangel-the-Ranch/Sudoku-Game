package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

//interface for the Game Server.
public interface GameServerInterface extends Remote{
    SudokuPuzzle getPuzzle(int K) throws RemoteException;
    void cleanUp() throws RemoteException;

    void receiveStat(Stat stat) throws RemoteException;
}
