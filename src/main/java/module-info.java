module com.onebyte_llc.imageviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.jooq;
    requires org.apache.commons.imaging;
    requires java.desktop;

    opens com.onebyte_llc.imageviewer to javafx.fxml;
    exports com.onebyte_llc.imageviewer;
    exports com.onebyte_llc.imageviewer.ui;
    opens com.onebyte_llc.imageviewer.ui to javafx.fxml;

    exports com.onebyte_llc.imageviewer.ui.display;
    opens com.onebyte_llc.imageviewer.ui.display to javafx.fxml;

    exports com.onebyte_llc.imageviewer.ui.directory;
    opens com.onebyte_llc.imageviewer.ui.directory to javafx.fxml;

    exports com.onebyte_llc.imageviewer.ui.display.grid;
    opens com.onebyte_llc.imageviewer.ui.display.grid to javafx.fxml;

    exports com.onebyte_llc.imageviewer.ui.display.header;
    opens com.onebyte_llc.imageviewer.ui.display.header to javafx.fxml;
    exports com.onebyte_llc.imageviewer.controls;
    opens com.onebyte_llc.imageviewer.controls to javafx.fxml;

    exports com.onebyte_llc.imageviewer.backend.db.jooq.tables.records;
    exports com.onebyte_llc.imageviewer.controls.gridview;
    opens com.onebyte_llc.imageviewer.controls.gridview to javafx.fxml;
    exports com.onebyte_llc.imageviewer.controls.imageview;
    opens com.onebyte_llc.imageviewer.controls.imageview to javafx.fxml;
    exports com.onebyte_llc.imageviewer.controls.scrollbar;
    opens com.onebyte_llc.imageviewer.controls.scrollbar to javafx.fxml;
}