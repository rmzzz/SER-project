package bfst.citiesAndStreets;

import bfst.OSMReader.Node;
import bfst.canvas.Drawable;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

public class City implements Serializable, Comparable<City>, Drawable {
    public String getName() {
        return name;
    }

    private final String name;
    private final Node node;
    private final int fontSize;

    private final int minMxx;
    private final int maxMxx;

    public Node getNode() {
        return node;
    }

    private City(
            String _name,
            Node _node,
            String _cityType
    ) {
        name = _name;
        node = _node;
        switch (_cityType) {
            case "city":
                fontSize = 20;
                minMxx = 400;
                maxMxx = 4800;
                break;
            default:
                fontSize = 10;
                minMxx = 4800;
                maxMxx = 20000;
                break;

        }
    }

    @Override
    public int compareTo(City that) {
        return this.name.compareTo(that.name);
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        gc.fillText(this.name, node.getLon(), node.getLat());
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getMinMxx() {
        return minMxx;
    }

    public int getMaxMxx() {
        return maxMxx;
    }

    public static class Builder {
        private String name;
        private Node node;
        private String cityType;

        public Builder name(String _name) {
            name = _name;
            return this;
        }

        public Builder node(Node _node) {
            node = _node;
            return this;
        }

        public Builder cityType(String _cityType) {
            cityType = _cityType;
            return this;
        }

        public City build() {
            return new City(name, node, cityType);
        }
    }
}
