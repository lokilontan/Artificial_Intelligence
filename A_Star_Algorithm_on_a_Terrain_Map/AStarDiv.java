import java.awt.Point;
import java.util.*;
import java.util.List;
import java.util.Map.*;

public class AStarDiv implements AIModule
{
    public double getHeuristic(final Point tile, final TerrainMap map) {

        //Compute Chebyshev`s distance
        double deltaD = Math.max(Math.abs(tile.getX() - map.getEndPoint().getX()),
                Math.abs(tile.getY() - map.getEndPoint().getY()));

        //Compute change in height
        double deltaH = Math.abs(map.getTile(map.getEndPoint()) - map.getTile(tile));

        double h = 0.0;

        if (map.getTile(tile) > map.getTile(map.getEndPoint()) && (deltaH>deltaD)) {
            int deltaDint = (int) deltaD;
            int counter = deltaDint;
            int deltaHint = (int) deltaH;
            int height2 = 0;
            int height1 =(int) map.getTile(tile);
            for (int i = 0; i < counter; i++) {
                height2 = deltaHint/deltaDint;
                h += (double)height2/(height1+1);
                deltaHint -= height2;
                deltaDint--;
                height1 = height2;
            }
            return h;
        }

        return deltaD/1.8;

    }

    /// Creates the path to the goal.
    public List<Point> createPath(final TerrainMap map)
    {

        PriorityQueue<Tile> fringe = new PriorityQueue<>(1024);
        HashSet<Point> visited = new HashSet<>(1024);
        HashMap<Point, Point> parent = new HashMap<>(1024);
        HashMap<Point, Double> distances = new HashMap<>(1024);
        Point[] neighbors;

        Point CurrentPoint = map.getStartPoint();
        distances.put(CurrentPoint, 0.0);
        fringe.add(new Tile(CurrentPoint, (0.0 + getHeuristic(CurrentPoint, map))));

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
                if (!visited.contains(neighbor) &&
                        ( distances.get(neighbor) > ( distances.get(CurrentPoint)
                                + map.getCost(CurrentPoint, neighbor)
                        ))) {

                    fringe.remove(new Tile(neighbor, (distances.get(neighbor) + getHeuristic(neighbor, map))));

                    distances.put(neighbor, distances.get(CurrentPoint)
                            + map.getCost(CurrentPoint, neighbor));
                    //+ getHeuristic(neighbor, map ));

                    parent.put(neighbor, CurrentPoint);

                    fringe.add(new Tile(neighbor, (distances.get(neighbor) + getHeuristic(neighbor, map))));

                }
            }

            visited.add(CurrentPoint);

            fringe.remove(new Tile(CurrentPoint, (distances.get(CurrentPoint) + getHeuristic(CurrentPoint, map))));

            CurrentPoint = fringe.poll().getPoint();

        }

        List<Point> path = new ArrayList<>();

        CurrentPoint = map.getEndPoint();
        while (!CurrentPoint.equals(map.getStartPoint())) {
            path.add(CurrentPoint);
            CurrentPoint = parent.get(CurrentPoint);

        }

        path.add(map.getStartPoint());
        Collections.reverse(path);
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