package in.fastr.tripsim;

import in.fastr.apps.traffic.SimpleGeoPoint;

import java.util.Date;
import java.util.List;

public class TripGenerator {

    public Trip constantSpeedTrip(List<SimpleGeoPoint> points, float speedKmph, Date startTime) {
        Trip trip = new Trip(points.size());
        TripPoint lastTripPoint = null;

        for (SimpleGeoPoint sgPoint: points) {
            TripPoint tripPoint = new TripPoint();

            tripPoint.speed = speedKmph;
            tripPoint.simpleGeoPoint = sgPoint;

            // calculate timestamp based on last tripPoint
            if (lastTripPoint == null) {
                tripPoint.timestamp = startTime;
            } else {
                
                double distanceKm = sgPoint.getDistanceFrom(lastTripPoint.simpleGeoPoint);
                long secondsLapsed = (long) ((distanceKm * 60 * 60) / speedKmph);
                long lastTime = lastTripPoint.timestamp.getTime();
                tripPoint.timestamp = new Date();
                tripPoint.timestamp.setTime(lastTime + (secondsLapsed*1000) );
                String time = tripPoint.timestamp.toString();
                String test = time;
            }
            
            trip.addPoint(tripPoint);
            lastTripPoint = tripPoint;
        }

        return trip;
    }
    
    
        
}
