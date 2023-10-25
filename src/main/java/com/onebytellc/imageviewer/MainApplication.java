package com.onebytellc.imageviewer;

import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.ContextParameters;
import com.onebytellc.imageviewer.logger.LogAppender;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.ui.MainController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ViewNode<Parent, MainController> viewNode = MainController.create();
        Scene scene = new Scene(viewNode.getNode(), 1024, 1024);
        stage.titleProperty().bind(I18N.get("app.title"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // TODO - this path should be in %APP_DATA% dir
        ContextParameters parameters = new ContextParameters.Builder()
                .setAppDataPath(Path.of("/Users/shorton/imageviewdata/"))
                .build();

        // set some startup items
        Logger.addLogAppender(LogAppender.stdout());
        I18N.setLocale(Locale.getDefault());
        Context.initialize(parameters);
        // Application.setUserAgentStylesheet();

        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Context.destroy();
        Logger.stopAndWait();
    }
}