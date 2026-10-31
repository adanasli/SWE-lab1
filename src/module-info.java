module SWE_lab1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens swe_lab1 to javafx.graphics, javafx.fxml;
    exports swe_lab1;
}