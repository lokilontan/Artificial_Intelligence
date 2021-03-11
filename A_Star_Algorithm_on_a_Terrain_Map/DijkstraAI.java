
import java.awt.Point;
import java.util.*;
import java.util.Map.*;

/// A sample AI that takes a very suboptimal path.
/**
 * This is a sample AI that moves as far horizontally as necessary to reach the target,
 * then as far vertically as necessary to reach the target.  It is intended primarily as
 * a demonstration of the various pieces of the program.
 *
 */



public class DijkstraAI implements AIModule
{
    public double getHeuristic(final Point start, final Point end) {
        return 0.0;
    }

    public Point getShortestDistance(final HashMap<Point, Double> distances, HashSet<Point> visited) {

        Entry<Point, Double> min = null;
        for (Entry<Point, Double> entry : distances.entrySet()) {
            if ((min == null || min.getValue() > entry.getValue()) && !visited.contains(entry.getKey())) {
                min = entry;
            }
        }
        return min.getKey();

    }


    /// Creates the path to the goal.
    public List<Point> createPath(final TerrainMap map)
    {

        PriorityQueue<Tile> fringe = new PriorityQueue<>();
        HashSet<Point> visited = new HashSet<>();
        HashMap<Point, Point> parent = new HashMap<>();
        HashMap<Point, Double> distances = new HashMap<>();
        Point[] neighbors;

        // Holds the resulting path
        final ArrayList<Point> path = new ArrayList<Point>();
        // Keep track of where we are and add the start point.
        Point CurrentPoint = map.getStartPoint();
        path.add(CurrentPoint);
        distances.put(CurrentPoint, 0.0);
        fringe.add(new Tile(CurrentPoint, 0.0));

        while (!fringe.isEmpty()) {

            if (CurrentPoint.equals(map.getEndPoint())) {
                break;
            }

            neighbors = map.getNeighbors(CurrentPoint);

            //Assign
            for (Point neighbor: neighbors) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, Double.POSITIVE_INFINITY);
                }
            }

            for (Point neighbor: neighbors) {
                if ( !visited.contains(neighbor) &&
                        ( distances.get(neighbor) > ( distances.get(CurrentPoint)
                                                    + map.getCost(CurrentPoint, neighbor)
                                                    + getHeuristic(neighbor, map.getEndPoint() )))) {

                    fringe.remove(new Tile(neighbor, distances.get(neighbor)));

                    distances.put(neighbor, distances.get(CurrentPoint)
                            + map.getCost(CurrentPoint, neighbor)
                            + getHeuristic(neighbor, map.getEndPoint() ));

                    parent.put(neighbor, CurrentPoint);

                    fringe.add(new Tile(neighbor, distances.get(neighbor)));

                }
            }

            visited.add(CurrentPoint);

            fringe.remove(new Tile(CurrentPoint, distances.get(CurrentPoint)));

            CurrentPoint = fringe.poll().getPoint();


        }


        Stack<Point> pathStack = new Stack<>();

        CurrentPoint = map.getEndPoint();
        while (!CurrentPoint.equals(map.getStartPoint())) {
            pathStack.push(CurrentPoint);
            CurrentPoint = parent.get(CurrentPoint);
        }

        while (!pathStack.empty()) {
            path.add(pathStack.pop());
        }

        // We're done!  Hand it back.
        return path;
    }

    static class Tile implements Comparable<Tile> {
        private Point point;
        private Double cost ;

        Tile(Point point, Double cost) {
            super();
            this.point = point;
            this.cost = cost;
        }

        Tile(Point point) {
            super();
            this.point = point;
            this.cost = Double.POSITIVE_INFINITY;
        }

        public Point getPoint() {
            return point;
        }
        public Double getCost() {
            return cost;
        }

        @Override
        public int compareTo(Tile other) {
            return this.getCost().compareTo(other.getCost());
        }
    }

}