module com.onebytellc.imageviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.xerial.sqlitejdbc;
    requires org.jooq;
    requires org.apache.commons.imaging;
    requires java.desktop;

    opens com.onebytellc.imageviewer to javafx.fxml;
    exports com.onebytellc.imageviewer;
    exports com.onebytellc.imageviewer.ui;
    opens com.onebytellc.imageviewer.ui to javafx.fxml;

    exports com.onebytellc.imageviewer.ui.display;
    opens com.onebytellc.imageviewer.ui.display to javafx.fxml;

    exports com.onebytellc.imageviewer.ui.display.grid;
    opens com.onebytellc.imageviewer.ui.display.grid to javafx.fxml;

    exports com.onebytellc.imageviewer.ui.display.header;
    opens com.onebytellc.imageviewer.ui.display.header to javafx.fxml;
    exports com.onebytellc.imageviewer.controls;
    opens com.onebytellc.imageviewer.controls to javafx.fxml;

    exports com.onebytellc.imageviewer.backend.db.jooq.tables.records;
    exports com.onebytellc.imageviewer.controls.gridview;
    opens com.onebytellc.imageviewer.controls.gridview to javafx.fxml;
    exports com.onebytellc.imageviewer.controls.imageview;
    opens com.onebytellc.imageviewer.controls.imageview to javafx.fxml;
    exports com.onebytellc.imageviewer.controls.scrollbar;
    opens com.onebytellc.imageviewer.controls.scrollbar to javafx.fxml;
}