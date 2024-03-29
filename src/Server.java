import server.GameServer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static final int PORT = 8000;
    public static void main(String[] args){
        try {
            //Create and export a remote object
            GameServer stub = new GameServer();

            Registry registry = LocateRegistry.createRegistry(PORT);

            registry.bind("SudokuServer", stub);

            System.out.println("Server is ready");

        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
