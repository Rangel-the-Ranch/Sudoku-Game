package Client;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.Node;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectLabel.setFocusTraversable(true);
        root.setOnKeyPressed(this::numberSelect);
        sudoku.startup("medium");
        timerStart();
        //disable undo and redo
        for(Node node : undoRedoButtons.getChildren()){
            if(node instanceof Button){
                node.setDisable(true);
            }
        }

        for(int x=0; x<9; x++){
            for(int y=0; y<9; y++){
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
            //enable redo
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
            //enable undo
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
    private void boardMove(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if(sudoku.addMove(new SudokuMove(GridPane.getRowIndex(clickedButton), GridPane.getColumnIndex(clickedButton), selectedNumber))){
            clickedButton.setText( String.valueOf(selectedNumber));
            //disable redo
            for(Node node : undoRedoButtons.getChildren()){
                if(node instanceof Button){
                    if(GridPane.getColumnIndex(node) == 1){
                        node.setDisable(true);
                    }
                }
            }
            //enable undo
            for(Node node : undoRedoButtons.getChildren()){
                if(node instanceof Button){
                    if(GridPane.getColumnIndex(node) == 0){
                        node.setDisable(false);
                    }
                }
            }
            if(sudoku.isSolved()){
                timeline.stop();
                selectLabel.setText("Solved!");
                timeLabel.setText("Score: "+sudoku.getScore(seconds));
                sudoku.sendStats(seconds);

                for(Node node : undoRedoButtons.getChildren()){
                    if(node instanceof Button){
                        node.setDisable(true);
                    }
                }



            }
        }
    }
    @FXML
    private void difficultyChange(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        sudoku.reset();
        timerStart();
        selectedNumber = 0;
        selectLabel.setText("Selected: " + 0);

        if(GridPane.getColumnIndex(clickedButton) == 0){
            sudoku.startup("easy");
        } else if(GridPane.getColumnIndex(clickedButton) == 1){
            sudoku.startup("medium");
        } else{
            sudoku.startup("hard");
        }
        //disable undo and redo
        for(Node node : undoRedoButtons.getChildren()){
            if(node instanceof Button){
                node.setDisable(true);
            }
        }

        for(int x=0; x<9; x++){
            for(int y=0; y<9; y++){
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
        String keyText = event.getText();
        if (keyText.matches("[1-9]")) {
            selectedNumber = Integer.parseInt(keyText);
            selectLabel.setText("Selected: " + keyText);
        }
    }
    @FXML
    private void solution(ActionEvent event) {
        sudoku.solve();
        for(int x=0; x<9; x++){
            for(int y=0; y<9; y++){
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
