module com.onebytellc.imageviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;


    opens com.onebytellc.imageviewer to javafx.fxml;
    exports com.onebytellc.imageviewer;
    exports com.onebytellc.imageviewer.ui;
    opens com.onebytellc.imageviewer.ui to javafx.fxml;
    exports com.onebytellc.imageviewer.ui.imagegrid;
    opens com.onebytellc.imageviewer.ui.imagegrid to javafx.fxml;
}