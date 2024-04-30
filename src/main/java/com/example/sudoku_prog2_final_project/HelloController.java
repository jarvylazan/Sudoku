package com.example.sudoku_prog2_final_project;

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
    private GridPane mainGrid; // Make sure this matches the fx:id in the FXML file

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
                        block.add(tf, col, row);
                        tf.setAlignment(Pos.CENTER);
                    }
                }
                mainGrid.add(block, blockCol, blockRow);
            }
        }
    }

    @FXML
    protected void onLoadButtonClick() {
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
                    TextField tf = getTextFieldAt(row, col);
                    if (tf != null) tf.setText(cells[col].trim());
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextField getTextFieldAt(int row, int col) {
        int blockRow = row / 3;
        int blockCol = col / 3;
        int inBlockRow = row % 3;
        int inBlockCol = col % 3;
        GridPane block = (GridPane) mainGrid.getChildren().get(blockRow * 3 + blockCol);
        return (TextField) block.getChildren().get(inBlockRow * 3 + inBlockCol);
    }
}
