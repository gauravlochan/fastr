package in.fastr.tripsim;

import in.fastr.apps.traffic.SimpleGeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TripGenerator {

    // spp
    public Trip constantSpeedTrip(List<SimpleGeoPoint> points, float speedKmph, Date startTime) {
        Trip trip = new Trip(points.size());
        TripPoint lastTripPoint = null;
        Calendar cal;

        for (SimpleGeoPoint sgPoint: points) {
            TripPoint tripPoint = new TripPoint();

            tripPoint.speed = speedKmph;
            tripPoint.simpleGeoPoint = sgPoint;

            // calculate timestamp based on last tripPoint
            if (lastTripPoint == null) {
                tripPoint.timestamp = startTime;
            } else {
                
                double distanceKm = getDistance(sgPoint, lastTripPoint.simpleGeoPoint);
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
    
    
    // returns distance in km
    private static double getDistance(SimpleGeoPoint point1, SimpleGeoPoint point2) {
        return getDistance(point1.getLatitude(), point1.getLongitude(), 
                point2.getLatitude(), point2.getLongitude() );
    }
    
    // from http://stackoverflow.com/questions/8494283/gps-distance-calculation
    // haversines formula
    public static double getDistance(double lat1, double lng1, double lat2, double lng2){
        double R = 6371; // earth’s radius (mean radius = 6,371km)
        double dLat =  Math.toRadians(lat2-lat1);

        double dLon =  Math.toRadians(lng2-lng1); 
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
                   Math.sin(dLon/2) * Math.sin(dLon/2); 
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        double dr1 = R * c; //in radians      

        return dr1;
    }
        
}
