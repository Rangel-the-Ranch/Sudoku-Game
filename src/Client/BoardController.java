package Client;

import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;



public class BoardController implements Initializable {

    @FXML private Label selectLabel;
    @FXML private Pane root;
    @FXML private GridPane board;
    @FXML private Label timeLabel;
    @FXML private GridPane undoRedoButtons;
    private int selectedNumber = 0;
    private Timeline timeline;
    private int seconds = 0;
    private final Sudoku sudoku = new Sudoku();
    private static final int SIZE = 9;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectLabel.setFocusTraversable(true);
        root.setOnKeyPressed(this::numberSelect);
        sudoku.startup("medium");
        timerStart();
        //disable undo and redo (No moves to undo or redo)
        for(Node node : undoRedoButtons.getChildren()){
            if(node instanceof Button){
                node.setDisable(true);
            }
        }
        //set initial board and disable starting buttons
        for(int x=0; x<SIZE; x++){
            for(int y=0; y<SIZE; y++){
                int cellValue = sudoku.getBoard()[x][y];
                if (cellValue != 0) {
                    Button button = findButtonAtPosition(board, x, y);
                    if (button != null) {
                        button.setText(String.valueOf(cellValue));
                        button.setDisable(true);
                    }
                }
            }
        }

    }
    //Starts the timer
    private void timerStart(){
        if(timeline != null){
            timeline.stop();
        }
        seconds = 0;
        updateTimeLabel();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            updateTimeLabel();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    private void updateTimeLabel(){
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, remainingSeconds));
    }
    //Finds the button at a given position in the grid
    private Button findButtonAtPosition(GridPane gridPane, int row, int col) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Button) {
                int nodeRow = GridPane.getRowIndex(node);
                int nodeCol = GridPane.getColumnIndex(node);
                if (nodeRow == row && nodeCol == col) {
                    return (Button) node;
                }
            }
        }
        return null;
    }
    @FXML
    private void undoRedoAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if(GridPane.getColumnIndex(clickedButton) == 0){
            SudokuMove move = sudoku.undoMove();
            if(move != null){
                Button button = findButtonAtPosition(board, move.getX(), move.getY());
                if (button != null) {
                    button.setText("");
                }
            }
            //enable redo (if there are moves to redo)
            for(Node node : undoRedoButtons.getChildren()){
                if(node instanceof Button){
                    if(GridPane.getColumnIndex(node) == 1){
                        node.setDisable(false);
                    }
                }
            }
            //disable undo if no moves
            if(sudoku.moveCount() == 0){
                for(Node node : undoRedoButtons.getChildren()){
                    if(node instanceof Button){
                        if(GridPane.getColumnIndex(node) == 0){
                            node.setDisable(true);
                        }
                    }
                }
            }
        } else{
            SudokuMove move = sudoku.redoMove();
            if(move != null){
                Button button = findButtonAtPosition(board, move.getX(), move.getY());
                if (button != null) {
                    button.setText(String.valueOf(move.getValue()));
                }
            }
            //disable redo if no moves
            if(sudoku.undoneMoveCount() == 0){
                for(Node node : undoRedoButtons.getChildren()){
                    if(node instanceof Button){
                        if(GridPane.getColumnIndex(node) == 1){
                            node.setDisable(true);
                        }
                    }
                }
            }
            //enable undo (if there are moves to undo)
            for(Node node : undoRedoButtons.getChildren()){
                if(node instanceof Button){
                    if(GridPane.getColumnIndex(node) == 0){
                        node.setDisable(false);
                    }
                }
            }
        }
    }
    @FXML
    private void boardMove(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        if(sudoku.addMove(new SudokuMove(GridPane.getRowIndex(clickedButton), GridPane.getColumnIndex(clickedButton), selectedNumber))){
            clickedButton.setText( String.valueOf(selectedNumber));
            //disable redo (if there are no moves to redo)
            for(Node node : undoRedoButtons.getChildren()){
                if(node instanceof Button){
                    if(GridPane.getColumnIndex(node) == 1){
                        node.setDisable(true);
                    }
                }
            }
            //enable undo (if there are moves to undo)
            for(Node node : undoRedoButtons.getChildren()){
                if(node instanceof Button){
                    if(GridPane.getColumnIndex(node) == 0){
                        node.setDisable(false);
                    }
                }
            }
            if(sudoku.isSolved()){
                //If the game is solved, stop the timer ,display the score and send the score to the server
                timeline.stop();
                selectLabel.setText("Solved!");
                timeLabel.setText("Score: "+sudoku.getScore(seconds));

                //Get the player's name
                String name = openInputDialog((Stage) root.getScene().getWindow());
                name = covertName(name);
                sudoku.sendStats(seconds, name);

                //disable undo and redo at the end of the game
                for(Node node : undoRedoButtons.getChildren()){
                    if(node instanceof Button){
                        node.setDisable(true);
                    }
                }



            }
        }
    }
    private String covertName(String name){
        // Regular expression to match characters other than alphanumeric and underscore
        String regex = "[^a-zA-Z0-9_]";

        String newStr = name.replaceAll(regex, "");

        if(newStr.length() > 20){
            return newStr.substring(0, 20);
        }
        if(newStr.isEmpty()){
            return "Player";
        }
        return newStr;
    }
    //Open a dialog to get the player's name
    //https://stackoverflow.com/questions/8309981/how-to-create-and-show-common-dialog-error-warning-confirmation-in-javafx-2
    private String openInputDialog(Stage primaryStage) {
        TextField inputField = new TextField();
        Label inputLabel = new Label("Enter your name:");
        inputLabel.setStyle("-fx-font-weight: bold;");
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #5DADE2; -fx-text-fill: white;");

        //Create a dialog layout including the input field and submit button
        VBox dialogLayout = new VBox(10, inputLabel, inputField, submitButton);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setStyle("-fx-background-color: #EADDCA; -fx-border-color: #ccc; -fx-border-width: 1px;");
        dialogLayout.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogLayout, 250, 100);

        //Create a new stage and set the dialog layout as the scene
        Stage inputDialog = new Stage();
        //Set the dialog to be application modal(Block input events to other windows)
        inputDialog.initModality(Modality.APPLICATION_MODAL);
        //Set the owner of the dialog to the main stage
        inputDialog.initOwner(primaryStage);
        inputDialog.setScene(dialogScene);
        inputDialog.setTitle("Puzzle Solved!");
        //Set the button action to close the dialog
        submitButton.setOnAction(e -> inputDialog.close());

        inputDialog.showAndWait();

        return inputField.getText();
    }

    @FXML
    private void difficultyChange(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        sudoku.reset();
        timerStart();
        selectedNumber = 0;
        selectLabel.setText("Selected: " + 0);
        //request a new board based on the difficulty
        if(GridPane.getColumnIndex(clickedButton) == 0){
            sudoku.startup("easy");
        } else if(GridPane.getColumnIndex(clickedButton) == 1){
            sudoku.startup("medium");
        } else{
            sudoku.startup("hard");
        }
        //disable undo and redo (No moves to undo or redo)
        for(Node node : undoRedoButtons.getChildren()){
            if(node instanceof Button){
                node.setDisable(true);
            }
        }
        //set initial board and disable starting buttons
        for(int x=0; x<SIZE; x++){
            for(int y=0; y<SIZE; y++){
                int cellValue = sudoku.getBoard()[x][y];
                Button button = findButtonAtPosition(board, x, y);
                if (button != null) {
                    button.setText(cellValue == 0 ? "" : String.valueOf(cellValue));
                    button.setDisable(cellValue != 0);
                }
            }
        }
    }
    @FXML
    private void numberSelect(KeyEvent event) {
        //Select a number to place on the board. If the number is valid, it will be placed on the board
        String keyText = event.getText();
        if (keyText.matches("[1-9]")) {
            selectedNumber = Integer.parseInt(keyText);
            selectLabel.setText("Selected: " + keyText);
        }
    }
    @FXML
    private void solution(ActionEvent event) {
        //Solve the board and display the solution
        sudoku.solve();
        for(int x=0; x<SIZE; x++){
            for(int y=0; y<SIZE; y++){
                int cellValue = sudoku.getBoard()[x][y];
                Button button = findButtonAtPosition(board, x, y);
                if (button != null) {
                    button.setText(cellValue == 0 ? "" : String.valueOf(cellValue));
                    button.setDisable(cellValue != 0);
                }
            }
        }
        timeline.stop();
        //disable undo and redo
        for(Node node : undoRedoButtons.getChildren()){
            if(node instanceof Button){
                node.setDisable(true);
            }
        }
    }
}
