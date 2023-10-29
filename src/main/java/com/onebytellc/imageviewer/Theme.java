package com.onebytellc.imageviewer;

import com.onebytellc.imageviewer.logger.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssParser;
import javafx.css.Declaration;
import javafx.css.Rule;
import javafx.css.Selector;
import javafx.css.StyleConverter;
import javafx.css.Stylesheet;
import javafx.css.converter.ColorConverter;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URISyntaxException;

public final class Theme {

    private static final Logger LOG = Logger.getInstance(Theme.class);

    private static final ObjectProperty<Stylesheet> stylesheetProperty = new SimpleObjectProperty<>();
    private static final ObjectProperty<Color> buttonBlue = new SimpleObjectProperty<>();
    private static final ObjectProperty<Color> imageBackground = new SimpleObjectProperty<>();

    private Theme() {

    }

    public static void setStylesheet(String sheet) {
        CssParser parser = new CssParser();
        try {
            stylesheetProperty.set(parser.parse(Theme.class.getResource(sheet).toURI().toURL()));
        } catch (URISyntaxException | IOException ex) {
            System.err.println("Could not parse stylesheet!");
            System.exit(1);
        }

        // NOTE: for some reason ColorConverter can't parse -fx-background-color
        buttonBlue.setValue(extract(".button-blue", "-fx-text-fill", ColorConverter.getInstance()));
        imageBackground.setValue(extract(".image-background", "-fx-text-fill", ColorConverter.getInstance()));
    }

    private static <T> T extract(String selector, String decaration, StyleConverter converter) {
        Stylesheet css = stylesheetProperty.get();

        for (Rule rule : css.getRules()) {
            for (Selector s : rule.getSelectors()) {
                if (s.toString().contains(selector)) {
                    for (Declaration declaration : rule.getDeclarations()) {
                        if (declaration.getProperty().equals(decaration)) {
                            Object o = converter.convert(declaration.getParsedValue(), null);
                            LOG.debug("Converted css style {} :: {} :: {}", selector, decaration, o);
                            return (T) o;
                        }
                    }
                }
            }
        }

        LOG.warn("Unable to find css {} :: {}", selector, decaration);
        return null;
    }

    public static ObjectProperty<Color> buttonBlue() {
        return buttonBlue;
    }

    public static ObjectProperty<Color> imageBackground() {
        return imageBackground;
    }

}
