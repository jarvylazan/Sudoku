package com.example.sudoku_prog2_final_project;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SudokuHelper {

    @FXML
    private Button finishedSetupButton;
    @FXML
    private GridPane mainGrid; // Ensure this GridPane is properly referenced inFXML

    private TextField[][] sudokuFields = new TextField[9][9]; // Array to store text fields for Sudoku grid

    public void initialize() {
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                GridPane block = new GridPane();
                block.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        TextField tf = new TextField();
                        tf.setPrefWidth(50);
                        tf.setPrefHeight(50);
                        tf.setAlignment(Pos.CENTER);
                        block.add(tf, col, row);

                        attachListeners(tf);
                        // Calculate the index in the sudokuFields array
                        int indexRow = blockRow * 3 + row;
                        int indexCol = blockCol * 3 + col;
                        sudokuFields[indexRow][indexCol] = tf; // Store the text field reference in the array
                    }
                }
                mainGrid.add(block, blockCol, blockRow);
            }
        }
    }

    private void attachListeners(TextField textField) {
        // Adding a change listener to each TextField
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) { // Allow only digits
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (!newValue.isEmpty() && !isValidInput(newValue)) {
                    textField.setStyle("-fx-text-fill: red;"); // Set text color to red if input is invalid
                } else {
                    textField.setStyle("-fx-text-fill: black;"); // Reset text color to black if input is valid
                }
            }
        });
    }

    private boolean isValidInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= 1 && value <= 9;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @FXML
    protected void onImportButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Sudoku File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            loadSudokuFromFile(file);
        }
    }

    @FXML
    protected void onFinishedSetupButtonClick() {
//        int row = 0;
//        for (int col = 0; col < 9; col++) {
//            TextField tf = sudokuFields[row][col];
//            if (tf != null) {
//                String value = cells[col].trim();
//                tf.setText(value);
//                if (!value.isEmpty()) {
//                    tf.setEditable(false);
//                }
//            }
//        }
//        row++;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = sudokuFields[row][col];
                if (!tf.getText().trim().isEmpty()) {
                    tf.setEditable(false);
                }

                if (sudokuFields[row][col] == sudokuFields[8][8]) {
                    finishedSetupButton.setVisible(false);
                }
            }
        }
    }

    private void loadSudokuFromFile(java.io.File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < 9) {
                String[] cells = line.split(",");
                for (int col = 0; col < cells.length; col++) {
                    TextField tf = sudokuFields[row][col];
                    if (tf != null) {
                        String value = cells[col].trim();
                        tf.setText(value);
                        if (!value.isEmpty()) {
                            tf.setEditable(false); // Make the field non-editable only if it's filled
                        } else {
                            tf.setEditable(true); // Ensure empty fields are editable
                            tf.setStyle("-fx-opacity: 1.0;"); // Reset the style for empty cells
                        }
                    }
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void onClearButtonClick() {
        for (TextField[] row : sudokuFields) {
            for (TextField tf : row) {
                tf.setText(""); // Clear the text
                tf.setEditable(true); // Make the field editable again
                tf.setStyle("-fx-opacity: 1.0;"); // Reset any style if needed
            }
        }
        finishedSetupButton.setVisible(true);
    }

    @FXML
    protected void  onGetHelpButtonClick() {
        solveSudoku();
    }

    private boolean solveSudoku() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = sudokuFields[row][col];
                if (tf.getText().isEmpty()) {  // Find an empty cell
                    for (int num = 1; num <= 9; num++) {  // Try possible numbers
                        if (isValid(row, col, num)) {
                            tf.setText(String.valueOf(num));  // Place the number
                            tf.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");  // Color it green
                            if (solveSudoku()) {  // Recursively continue to solve
                                return true;
                            } else {
                                tf.setText("");  // Backtrack
                            }
                        }
                    }
                    return false;  // Trigger backtrack
                }
            }
        }
        return true;  // Puzzle solved
    }

    private void HelpCell() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = sudokuFields[row][col];
                if (tf.getText().isEmpty()) {  // Find an empty cell
                    for (int num = 1; num <= 9; num++) {  // Try possible numbers
                        if (isValid(row, col, num)) {
                            tf.setText(String.valueOf(num));  // Place the number
                            tf.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");  // Color it green
                        }
                    }
                }
            }
        }
    }

    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            // Row check
            if (!sudokuFields[row][i].getText().isEmpty() &&
                    Integer.parseInt(sudokuFields[row][i].getText()) == num) {
                return false;
            }
            // Column check
            if (!sudokuFields[i][col].getText().isEmpty() &&
                    Integer.parseInt(sudokuFields[i][col].getText()) == num) {
                return false;
            }
        }
        // Subgrid check
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (!sudokuFields[i][j].getText().isEmpty() &&
                        Integer.parseInt(sudokuFields[i][j].getText()) == num) {
                    return false;
                }
            }
        }
        return true;
    }

    //TODO Create button that validates current input. Display an alert showing if it's right or not
    //TODO Validate the imported csv file. (Too many commas, too little, etc etc).
    //TODO Make sure all invalid input is unusable when solving the sudoku.
    //TODO Invalid board setups crash the program.
}
