module DotsAndBoxes {
    requires javafx.fxml;
    requires javafx.controls;
    opens wrapper to javafx.fxml;
    exports wrapper to javafx.graphics;
}