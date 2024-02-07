import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.net.URL;

//In order to fully utilise the JAR file we must remove resources , types and client packages from the src folder
//For the purpose of demonstration, we will not remove any of the packages
public class Client extends Application{
    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }
    @Override
    public void start(Stage stage) throws Exception {
        //Load the board
        URL boardFile = getClass().getResource("resources/board.fxml");
        if (boardFile != null) {
            Parent root = FXMLLoader.load(boardFile);
            Scene scene = new Scene(root, 610, 750);
            stage.setTitle("Sudoku Game");
            stage.setScene(scene);
            stage.show();
        } else {
            System.out.println("FXML file not found");
        }
    }
}