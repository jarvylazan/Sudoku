module com.example.sudoku_prog2_final_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sudoku_prog2_final_project to javafx.fxml;
    exports com.example.sudoku_prog2_final_project;
}