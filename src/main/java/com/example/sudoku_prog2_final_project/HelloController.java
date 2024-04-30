package com.example.sudoku_prog2_final_project;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HelloController {
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
//                        tf.setOnAction(onTextFieldChange());
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
    }
}
