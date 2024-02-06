
import Server.SudokuGenerator;
import java.rmi.registry.*;

public class Server {
    public static void main(String[] args){
        try {
            SudokuGenerator stub = new SudokuGenerator();

            Registry registry = LocateRegistry.createRegistry(8000);

            registry.bind("SudokuServer", stub);

            System.out.println("Server is ready");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
