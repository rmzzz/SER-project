package bong.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bong.canvas.CanvasElement;
import bong.canvas.Range;
import bong.routeFinding.Edge;
import bong.routeFinding.Street;
import bong.util.Geometry;
import javafx.geometry.Point2D;

public class KDTree implements Serializable {
  private static final long serialVersionUID = 1L;
  List<? extends CanvasElement> elements;
  public static int maxNumOfElements = 500; // max size of elements list in leafs
  Range bound;
  KDTree low;
  KDTree high;
  Type type;
  int depth;

  private enum Type {
    PARENT, LEAF
  }

  /** first instance is root and depth 0 */
  public KDTree(List<? extends CanvasElement> elements, Range bound){
    this(elements,bound,0,Type.LEAF);
  }

  /** Recursive constructor for intermediate nodes */
  public KDTree(List<? extends CanvasElement> elements, Range bound, int depth, Type type) {
    this.bound = bound;
    this.elements = elements;
    this.type = type;
    this.depth = depth;
    int size = this.elements.size();

    if (this.elements.size() >= maxNumOfElements) {
      sortElementsByDimension();

      List<CanvasElement> lower = getSubList(0, size / 2 + 1);
      List<CanvasElement> higher = getSubList(size / 2 + 1, size);

      this.low = new KDTree(lower, CanvasElement.boundingRangeOf(lower), this.depth + 1, Type.LEAF);

      this.high = new KDTree(higher, CanvasElement.boundingRangeOf(higher), this.depth + 1, Type.LEAF);

      this.elements = null;
      this.type = Type.PARENT;
    }
  }

  private void sortElementsByDimension() {
    if (isEvenDepth()) {
      this.elements.sort(Comparator.comparingDouble(o -> o.getCentroid().getX()));
    } else {
      this.elements.sort(Comparator.comparingDouble(o -> o.getCentroid().getY()));
    }
  }

  private List<CanvasElement> getSubList(int fromIndex, int toIndex) {
    return new ArrayList<>(this.elements.subList(fromIndex, toIndex));
  }

  // Only used for Address objects
  public CanvasElement nearestNeighbor(Point2D query) {
    return nearestNeighbor(query, Double.POSITIVE_INFINITY);
  }

  private CanvasElement nearestNeighbor(Point2D query, double bestDist) {
    if (!isLeaf()) {
      return findNearestInNonLeaf(query, bestDist);
    }

    return findNearestInLeaf(query, bestDist);
  }

  private CanvasElement findNearestInNonLeaf(Point2D query, double bestDist) {
    KDTree first = low.bound.distanceToPoint(query) < high.bound.distanceToPoint(query) ? low : high;
    KDTree last = (first == low) ? high : low;  // The other tree

    CanvasElement result = tryFindInTree(first, query, bestDist);
    if (result != null) {
      bestDist = Geometry.distance(query, result.getCentroid());
    }

    CanvasElement temp = tryFindInTree(last, query, bestDist);
    return temp != null ? temp : result;
  }

  private CanvasElement tryFindInTree(KDTree tree, Point2D query, double bestDist) {
    if (tree.bound.distanceToPoint(query) < bestDist) {
      return tree.nearestNeighbor(query, bestDist);
    }
    return null;
  }

  private CanvasElement findNearestInLeaf(Point2D query, double bestDist) {
    if (elements.isEmpty()) {
      return null;
    }

    CanvasElement c = closestElementInElements(query);
    if (c == null) {
      return null;
    }

    return Geometry.distance(query, c.getCentroid()) < bestDist ? c : null;
  }


  private boolean isEvenDepth() {
    return this.depth % 2 == 0;
  }

  private boolean isLeaf() {
    return this.type == Type.LEAF;
  }

  public CanvasElement closestElementInElements(Point2D query){
    CanvasElement closestElement = elements.getFirst();
    double bestDist = Geometry.distance(query, closestElement.getCentroid());
    for(CanvasElement element : elements){
      double newDist = Geometry.distance(query, element.getCentroid());
      if(newDist < bestDist){
        bestDist = newDist;
        closestElement = element;
      }
    }
    return closestElement;
  }

  public List<? extends CanvasElement> rangeSearch(Range range){
    if(!range.overlapsWith(bound)) return new ArrayList<CanvasElement>();
    if(this.isLeaf()){
      if(bound.isEnclosedBy(range)) return elements;

      List<CanvasElement> elementsInRange = new ArrayList<CanvasElement>();
      for(CanvasElement element : elements){
        Range boundingBox = element.getBoundingBox();
        if(range.overlapsWith(boundingBox)){
          elementsInRange.add(element);
        }
      }

      return elementsInRange;
    } else {
      List<? extends CanvasElement> elementsInLowRange = new ArrayList<CanvasElement>();
      List<? extends CanvasElement> elementsInHighRange = new ArrayList<CanvasElement>();
      if(low != null) elementsInLowRange = low.rangeSearch(range);
      if(high != null) elementsInHighRange = high.rangeSearch(range);
      return Stream.concat(elementsInLowRange.stream(), elementsInHighRange.stream()).collect(Collectors.toList());
    }
  }

  // Only used for road edges
  public Node nearestNeighborForEdges(Point2D query, String vehicle){
    return nearestNeighborForEdges(query, Double.POSITIVE_INFINITY, vehicle);
  }

  private Node nearestNeighborForEdges(Point2D query, double bestDist, String vehicle) {
    KDTree first, last;
    if(!isLeaf()){
      Node result = null;

      first = low.bound.distanceToPoint(query) < high.bound.distanceToPoint(query) ? low : high;
      last = low.bound.distanceToPoint(query) > high.bound.distanceToPoint(query) ? low : high;

      if(first.bound.distanceToPoint(query) < bestDist){
        result = first.nearestNeighborForEdges(query, bestDist, vehicle);
        if(result != null) bestDist = Geometry.distance(query, result.getAsPoint());
      }
      Node temp;
      if(last.bound.distanceToPoint(query) < bestDist){
        temp = last.nearestNeighborForEdges(query, bestDist, vehicle);
        if(temp != null){
          result = temp;
        }
      }
      return result;
    }

    if(!elements.isEmpty()){
      Node result = null;
      Node c = closestNodeInEdges(query, vehicle);
      if(c == null) return null;

      if(Geometry.distance(query, c.getAsPoint()) < bestDist) result = c;
      return result;
    }
    return null;
  }

  public Node closestNodeInEdges(Point2D query, String vehicle) {
    Node closestNode = null;
    double bestDist = Double.POSITIVE_INFINITY;
    List<Edge> edges = (List<Edge>)(List<?>) elements;
    for(Edge e : edges) {

      Street street = e.getStreet();
      if(vehicle.equals("Car") && !street.isCar()) continue;
      if(vehicle.equals("Bicycle") && !street.isBicycle()) continue;
      if(vehicle.equals("Walk") && !street.isWalking()) continue;
      Node newNode = e.closestNode(query);
      
      double newDist = Geometry.distance(query.getX(), query.getY(), newNode.getLon(), newNode.getLat());
      if(newDist < bestDist){
        bestDist = newDist;
        closestNode = newNode;
      }
    }
    return closestNode;
  }

}