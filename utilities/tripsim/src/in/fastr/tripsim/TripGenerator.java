package in.fastr.tripsim;

import in.beetroute.apps.commonlib.SimpleGeoPoint;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class TripGenerator {

    /**
     * Generate a trip of the specified GeoPoints, assuming a fixed speed
     * throughout the trip
     * 
     * @param points
     * @param startTime
     * @param speedKmph
     * @return
     */
    public Trip constantSpeedTrip(List<SimpleGeoPoint> points, Date startTime, float speedKmph) {
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
    
    /**
     * Generate a trip of the provided GeoPoints, assuming a random speed at each point
     * and that speed is maintained till the next point.
     * 
     * @param points
     * @param startTime
     * @param minSpeedKmph
     * @param maxSpeedKmph
     * @return
     */
    public Trip randomSpeedTrip(List<SimpleGeoPoint> points, Date startTime,
            float minSpeedKmph, float maxSpeedKmph) {

        Trip trip = new Trip(points.size());
        TripPoint lastTripPoint = null;

        for (SimpleGeoPoint sgPoint: points) {
            TripPoint tripPoint = new TripPoint();

            tripPoint.speed = getRandom(minSpeedKmph, maxSpeedKmph);
            tripPoint.simpleGeoPoint = sgPoint;

            // calculate timestamp based on last tripPoint
            if (lastTripPoint == null) {
                tripPoint.timestamp = startTime;
            } else {
                double distanceKm = sgPoint.getDistanceFrom(lastTripPoint.simpleGeoPoint);
                long secondsLapsed = (long) ((distanceKm * 60 * 60) / lastTripPoint.speed);
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

    private Random fRandom = new Random();
    public float getRandom(float min, float max) {
        return min + fRandom.nextFloat() * (max-min);
    }
}
