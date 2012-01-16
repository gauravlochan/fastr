package in.fastr.tripsim;

import java.util.ArrayList;
import java.util.List;

/**
 * A trip is when someone takes a route, so in addition to static information like
 * geopoints, it contains timestamps and speed
 * 
 * @author gauravlochan
 */
public class Trip {
    private List<TripPoint> tripPoints;

    public Trip(int points) {
        tripPoints = new ArrayList<TripPoint>(points);
    }
    
    public void addPoint(TripPoint tripPoint) {
        tripPoints.add(tripPoint);
    }
    
    public List<TripPoint> getPoints() {
        return tripPoints;
    }

}
