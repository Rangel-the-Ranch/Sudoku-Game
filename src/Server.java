
import Server.SudokuGenerator;
import java.rmi.registry.*;

public class Server {
    private static final int PORT = 8000;
    public static void main(String[] args){
        try {
            //Create and export a remote object
            SudokuGenerator stub = new SudokuGenerator();

            Registry registry = LocateRegistry.createRegistry(PORT);

            registry.bind("SudokuServer", stub);

            System.out.println("Server is ready");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
