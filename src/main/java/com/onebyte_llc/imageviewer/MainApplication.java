package com.onebyte_llc.imageviewer;

import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.ContextParameters;
import com.onebyte_llc.imageviewer.logger.LogAppender;
import com.onebyte_llc.imageviewer.logger.LogLevel;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.ui.MainController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ViewNode<Parent, MainController> viewNode = MainController.create();
        Scene scene = new Scene(viewNode.getNode(), 1100, 740);

        stage.titleProperty().bind(I18N.get("app.title"));
        stage.setScene(scene);
        Image appIcon = new Image(getClass().getResource("/image/branding/app-icon.png").toString());
        stage.getIcons().add(appIcon);
        stage.show();

        // TODO - should be bound to light/dark mode
        scene.getStylesheets().clear();
        scene.getStylesheets().add(MainApplication.class
                .getResource("/theme/dark.css").toString());
        Theme.setStylesheet("/theme/dark.css");
    }

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        String appDataPath;
        if (os.contains("mac")) {
            appDataPath = System.getProperty("user.home") + "/Library/Application Support/";
        } else if (os.contains("win")) {
            appDataPath = System.getenv("AppData") + "/";
        } else {
            appDataPath = System.getProperty("user.home") + "/."; // dot for hidden on linux
        }
        appDataPath += "com.onebyte_llc.imageviewer";

        ContextParameters parameters = new ContextParameters.Builder()
                .setAppDataPath(Path.of(appDataPath))
                .build();

        // set some startup items
        Logger.setLevel(LogLevel.TRACE);
        Logger.addLogAppender(LogAppender.stdout());
        I18N.setLocale(Locale.getDefault());
        Context.initialize(parameters);

        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Context.destroy();
        Logger.stopAndWait();
    }

}