package bfst.canvas;

import bfst.OSMReader.Bound;
import bfst.OSMReader.Model;
import bfst.citiesAndStreets.City;
import bfst.citiesAndStreets.CityType;
import bfst.citiesAndStreets.Street;
import bfst.citiesAndStreets.StreetType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapCanvas extends Canvas {
    private GraphicsContext gc;
    private Affine trans;
    private Model model;
    private boolean smartTrace = true;
    private boolean useRegularColors = true;
    private boolean showCities = true;
    private boolean useDependentDraw = true;

    private List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());

    public MapCanvas(){
        this.gc = getGraphicsContext2D();
        this.trans = new Affine();
        repaint();
    }

    public void repaint(){
        long time = -System.nanoTime();

        gc.setTransform(new Affine());
        if (useRegularColors) {
            gc.setFill(Color.valueOf("#ade1ff"));
        } else {
            gc.setFill(Color.AQUA);
        }
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        double pixelwidth = 1 / Math.sqrt(Math.abs(trans.determinant()));
        gc.setFillRule(FillRule.EVEN_ODD);
        if(model != null) {
            for (Type type : typesToBeDrawn){
                if(type != Type.UNKNOWN ) {
                    if(useDependentDraw) {
                        if (type.getMinMxx() < trans.getMxx()) {
                            paintDrawablesOfType(type, pixelwidth, useRegularColors);
                        }
                    } else {
                        paintDrawablesOfType(type, pixelwidth, useRegularColors);
                    }
                }
            }


            for (Street street : model.getStreets()) {
                StreetType type = street.getType();
                if (useDependentDraw) {
                    if (trans.getMxx() > type.getMinMxx()) {
                        setValuesAndDrawStreet(pixelwidth, street, type);
                    }
                } else {
                    setValuesAndDrawStreet(pixelwidth, street, type);
                }
            }


            gc.setStroke(Color.BLACK);
            model.getBound().draw(gc, pixelwidth, false);

            if (showCities) {
                gc.setFill(Color.DARKGREY);
                for (City city : model.getCities()) {
                    CityType type = city.getType();
                    gc.setFont(new Font(pixelwidth * type.getFontSize()));
                    if (trans.getMxx() < type.getMaxMxx() && trans.getMxx() > type.getMinMxx()) {
                        city.draw(gc, pixelwidth, false);
                    }
                }
            }
        }
        time += System.nanoTime();
        System.out.println("repaint: " + time/1000000f + "ms");
        System.out.println("mxx: " + trans.getMxx());
    }

    private void setValuesAndDrawStreet(double pixelwidth, Street street, StreetType type) {
        if (useRegularColors) {
            gc.setStroke(type.getColor());
        } else {
            gc.setStroke(type.getAlternateColor());
        }
        gc.setLineWidth(pixelwidth * type.getWidth());
        street.draw(gc, pixelwidth, false);
    }

    public void setTypesToBeDrawn(List<Type> typesToBeDrawn){
        this.typesToBeDrawn = typesToBeDrawn;
        repaint();
    }

    public void setUseRegularColors(boolean shouldUseRegularColors) {
        useRegularColors = shouldUseRegularColors;
        repaint();
    }

    public void setTraceType(boolean shouldSmartTrace) {
        smartTrace = shouldSmartTrace;
        repaint();
    }

    public void setShowCities(boolean shouldShowCities) {
        showCities = shouldShowCities;
        repaint();
    }

    public void setUseDependentDraw(boolean shouldUseDependentDraw) {
        useDependentDraw = shouldUseDependentDraw;
        repaint();
    }

    public void resetView() {
        trans.setToIdentity();
        Bound b = model.getBound();
        pan(-(b.getMaxLon() + b.getMinLon())/2, -(b.getMaxLat() + b.getMinLat())/2);
        pan(getWidth()/2,getHeight()/2);

        float boundHeight = b.getMaxLat() - b.getMinLat();
        float boundWidth = b.getMaxLon() - b.getMinLon();
        float bound;
        float canvasScale;
        if(boundHeight > boundWidth){
            bound = boundHeight;
            canvasScale = (float) getHeight();
        } else {
            bound = boundWidth;
            canvasScale = (float) getWidth();
        }
        float factor = canvasScale/bound;
        zoom(factor,getWidth()/2,getHeight()/2);
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, double x, double y) {
        trans.prependScale(factor, factor, x, y);
        repaint();
    }

    private void paintDrawablesOfType(Type type, double pixelwidth, boolean useRegularColors) {
        ArrayList<Drawable> drawables = model.getDrawablesOfType(type);
        gc.setStroke(Color.TRANSPARENT);
        gc.setFill(Color.TRANSPARENT);
        if (drawables != null) {
            gc.setLineWidth(type.getWidth() * pixelwidth);
            if (useRegularColors) {
                if (type.shouldHaveFill()) gc.setFill(type.getColor());
                if (type.shouldHaveStroke()) gc.setStroke(type.getColor());
            } else {
                if (type.shouldHaveFill()) gc.setFill(type.getAlternateColor());
                if (type.shouldHaveStroke()) gc.setStroke(type.getAlternateColor());
            }
            for (Drawable drawable : drawables) {
                drawable.draw(gc, 1/pixelwidth, smartTrace);
                if (type.shouldHaveFill()) gc.fill();
            }
        }
    }

    public void setModel(Model model) {
        this.model = model;
        resetView();
    }
}