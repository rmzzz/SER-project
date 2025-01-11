package bong.canvas;

import javafx.scene.paint.Color;

public class RouteInstructionIndicator extends Indicator {

    public RouteInstructionIndicator(float centerX, float centerY, float radius) {
        super(centerX,centerY,radius);
    }

    public void draw(Drawer gc) {
        draw(gc, 1);
    }

    public void draw(Drawer gc, double size) {
        double factor = size*0.6;
        drawCenterCircle(gc, Color.BLACK, factor*5);
        drawCenterCircle(gc, Color.WHITE, factor*4);
    }

}