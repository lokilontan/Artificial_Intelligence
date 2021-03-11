import java.awt.Point;
import java.util.*;
import java.util.Map.*;

public class AStarExp implements AIModule
{
    public double getHeuristic(final Point tile, final TerrainMap map) {

        //Compute Chebyshev`s distance
        double deltaD = Math.max(Math.abs(tile.getX() - map.getEndPoint().getX()),
                Math.abs(tile.getY() - map.getEndPoint().getY()));

        //Compute chang in height
        double deltaH = Math.abs(map.getTile(map.getEndPoint()) - map.getTile(tile));

        double h = 0.0;

        //CASE 1: Neighbor and Goal at the same height
        if (map.getTile(tile) == map.getTile(map.getEndPoint())) {
            h = deltaD;

        //CASE 2: Goal is above the Neighbor
        } else if (map.getTile(tile) < map.getTile(map.getEndPoint())) {

            //CASE 2.1: № of remaining steps is 1
            if (deltaD == 1) {
                h = Math.exp(deltaH);

            //CASE 2.2: № of steps is higher or equal than change in height
            } else if (deltaD >= deltaH) {
                h = deltaD - deltaH + (deltaH * Math.exp(1));

            //CASE 2.3: № of steps is less than change in height
            } else {
                int deltaDint = (int) deltaD;
                int counter = deltaDint;
                int deltaHint = (int) deltaH;
                int expPower = 0;

                for (int i = 0; i < counter; i++) {
                    expPower = deltaHint / deltaDint;
                    h += Math.exp(expPower);
                    deltaDint--;
                    deltaHint -= expPower;
                }
            }

        //CASE 3: Goal is below the Neighbor
        } else {

            //CASE 3.1: № of remaining steps is 1
            if (deltaD == 1) {
                h = 1 / Math.exp(deltaH);

            //CASE 3.2: № of remaining steps is greater or equal than change in height
            } else if (deltaD >= deltaH) {
                h = (deltaD - deltaH) + (deltaH / Math.exp(1));

            //CASE 3.3: № of remaining steps is less than change in height
            } else {
                int deltaDint = (int) deltaD;
                int counter = deltaDint;
                int deltaHint = (int) deltaH;
                int expPower = 0;

                for (int i = 0; i < counter; i++) {
                    expPower = deltaHint / deltaDint;
                    h += 1 / Math.exp(expPower);
                    deltaDint--;
                    deltaHint -= expPower;
                }
            }

        }

        return h;

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
        fringe.add(new Tile(CurrentPoint, (0.0 + getHeuristic(CurrentPoint, map))));

        int counterPos = 0;
        int counterNeg = 0;

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
                    //System.out.println(fringe.peek().getPoint() + ": " + getHeuristic(fringe.peek().getPoint(), map));

                }
            }

            visited.add(CurrentPoint);

            fringe.remove(new Tile(CurrentPoint, (distances.get(CurrentPoint) + getHeuristic(CurrentPoint, map))));

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