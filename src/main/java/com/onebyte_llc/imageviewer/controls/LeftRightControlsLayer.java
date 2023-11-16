package com.onebyte_llc.imageviewer.controls;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class LeftRightControlsLayer extends CanvasLayer {

    private Image leftImage;
    private Image rightImage;
    private int barWidth = 120;
    private int imageDrawSize = 36;

    private Runnable onLeft;
    private Runnable onRight;
    private boolean enabled = true;
    private boolean showLeft = false;
    private boolean showRight = false;

    private Bounds leftBounds = new BoundingBox(0, 0, 0, 0);
    private Bounds rightBounds = new BoundingBox(0, 0, 0, 0);
    private Bounds leftButtonBounds = new BoundingBox(0, 0, 0, 0);
    private Bounds rightButtonBounds = new BoundingBox(0, 0, 0, 0);


    //////////////////
    // Properties
    public void setOnLeft(Runnable runnable) {
        this.onLeft = runnable;
    }

    public void setOnRight(Runnable runnable) {
        this.onRight = runnable;
    }

    public void setLeftImage(Image image) {
        this.leftImage = image;
    }

    public void setRightImage(Image image) {
        this.rightImage = image;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    ////////////////////
    // Mouse
    @Override
    protected void onMouseMoved(MouseEvent event) {
        if (!enabled) {
            showLeft = showRight = false;
            return;
        }

        Point2D point2D = new Point2D(event.getX(), event.getY());
        if (leftBounds.contains(point2D)) {
            showLeft = true;
            invalidate();
        } else if (rightBounds.contains(point2D)) {
            showRight = true;
            invalidate();
        } else if (showRight || showLeft) {
            showLeft = showRight = false;
            invalidate();
        }
    }

    @Override
    protected void onMouseExited(MouseEvent event) {
        if (!enabled) {
            return;
        }

        showLeft = showRight = false;
        invalidate();
    }

    @Override
    protected void onMouseClicked(MouseEvent event) {
        if (!enabled) {
            return;
        }

        Point2D point2D = new Point2D(event.getX(), event.getY());
        if (leftButtonBounds.contains(point2D) && onLeft != null) {
            onLeft.run();
        } else if (rightButtonBounds.contains(point2D) && onRight != null) {
            onRight.run();
        }

        event.consume();
    }

    @Override
    public void draw() {
        double offsetX = (barWidth - imageDrawSize) / 2.0;
        double offsetY = (getH() - imageDrawSize) / 2.0;

        leftBounds = new BoundingBox(0, 0, barWidth, getH());
        rightBounds = new BoundingBox(getW() - barWidth, 0, barWidth, getH());

        leftButtonBounds = new BoundingBox(offsetX, offsetY, imageDrawSize, imageDrawSize);
        rightButtonBounds = new BoundingBox(getW() - offsetX - imageDrawSize, offsetY, imageDrawSize, imageDrawSize);

        if (!enabled) {
            return;
        }

        getGraphics2D().setFill(Color.rgb(255, 255, 255, 0.75));

        double padding = 2;
        double cornerRadius = 14;
        if (showLeft) {
            getGraphics2D().fillRoundRect(leftButtonBounds.getMinX(),
                    leftButtonBounds.getMinY(),
                    leftButtonBounds.getWidth(),
                    leftButtonBounds.getHeight(), cornerRadius, cornerRadius);

            getGraphics2D().drawImage(leftImage,
                    leftButtonBounds.getMinX() + padding,
                    leftButtonBounds.getMinY() + padding,
                    leftButtonBounds.getWidth() - (padding * 2),
                    leftButtonBounds.getHeight() - (padding * 2)
            );
        }

        if (showRight) {
            getGraphics2D().fillRoundRect(rightButtonBounds.getMinX(),
                    rightButtonBounds.getMinY(),
                    rightButtonBounds.getWidth(),
                    rightButtonBounds.getHeight(), cornerRadius, cornerRadius);

            getGraphics2D().drawImage(rightImage,
                    rightButtonBounds.getMinX() + padding,
                    rightButtonBounds.getMinY() + padding,
                    rightButtonBounds.getWidth() - (padding * 2),
                    rightButtonBounds.getHeight() - (padding * 2)
            );
        }
    }

}
