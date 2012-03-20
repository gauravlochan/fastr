package in.beetroute.apps.traffic.trip;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import in.beetroute.apps.traffic.db.LocationDbHelper.LocationTable;
import in.beetroute.apps.traffic.db.TripDbHelper;
import in.beetroute.apps.traffic.google.geocoding.AndroidGeocodingService;
import in.beetroute.apps.traffic.location.LocationUpdate;
import in.beetroute.apps.traffic.services.GeocodingService;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;


public class Trip {
    private static final String TAG = Global.COMPANY;

    // The thresholds that are used to calculate the end of the trip
    public static final int TIME_CUTOFF = 10 * 60 * 1000; // 10 minutes
    public static final float DIST_THRESHOLD = 0.500f; // 500 meters
    public static final float SPEED_THRESHOLD = 2.0f; // 2 m/s = 7.2 km/hr 
    
    public static final float MIN_ACCURACY = 2000f; // 2kms
    
    // TODO: Don't leave it public
    public Integer startPointId;
    public Integer endPointId;

    public String startPointName;
    public String endPointName;

    // prevent default constructor
    private Trip() {}
    
    public Trip(Integer startPointId) {
        this.startPointId = startPointId;
    }

    public Trip(Integer startPointId, Integer endPointId, String startName, String endName) {
        this.startPointId = startPointId;
        this.startPointName = startName;
        this.endPointId = endPointId;
        this.endPointName = endName;
    }
    
    /**
     * Get the most recent trip.
     * 
     * @param context
     * @return
     */
    public static Trip getLastTrip(Context context) {
        TripDbHelper tripDbHelper = new TripDbHelper(context, null);
        return tripDbHelper.getLatestTrip();
    }
    
    /**
     * Tries to find a newer trip than the specified one
     * 
     * Algo: How to find a trip
     * 1. Go through location updates after the previous trip, drop any non-moving points
     *    TODO: Need to do this
     * 2. Treat the first real moving point as the trip starting point
     *    Initialize lastMovingPoint = starting point
     * 3. Go through location updates after the trip starting point.
     *    If user is still moving since lastMovingPoint,
     *      set lastMovingPoint = current point
     *    else 
     *      if (delay(currentPoint, lastMovingPoint) > 10 mins
     *         mark trip as ended
     *    
     * 4. If run out of location updates and still moving, then return null
     *    (since this current trip hasn't ended)
     * 
     * @param previous Previous Trip.  Null means look from the beginning
     * @return a valid trip, null if there is no newer trip
     */
    public static Trip getNextTrip(Activity context, Trip previous) {
        LocationDbHelper locationDbHelper = new LocationDbHelper(context, null);
        
        // Get the timestamp of the end of the last trip
        Long timestamp = 0L;
        LocationUpdate previousTripEndpoint = null;

        if (previous != null) {
            previousTripEndpoint = locationDbHelper.getLocationUpdate(previous.endPointId);
            timestamp = previousTripEndpoint.getEpochTime();
            Logger.info(TAG, "Start looking for trips after time " + timestamp);
        }

        // Get all the location updates after the last trip
        Cursor c = locationDbHelper.getNewerLocationUpdates(timestamp);
        context.startManagingCursor(c);
        
        int column_id = c.getColumnIndex(LocationTable._ID);

        if (c.moveToFirst()) {
            LocationUpdate tripStartPoint = null;
            
            if (previousTripEndpoint == null) {
                // Use the first location update as the trip start
                tripStartPoint = locationDbHelper.getCurrentLocationUpdate(c);
            } else {
                // If there was a previous trip, filter out all stationary points after that trip
                tripStartPoint = skipStationaryPoints(locationDbHelper, c, previousTripEndpoint);

                // if there were only stationary points after the previous trip, no new trip
                if (tripStartPoint == null) {
                    Logger.info(TAG, "No starting point for a new trip");
                    return null;
                }
            }
            
            // Start the trip with the first moving point
            Trip newTrip = new Trip(c.getInt(column_id));
            
            // Loop through all results until we run out or the trip ends
            LocationUpdate lastMovingPoint = tripStartPoint;
            while (c.moveToNext()) {
                LocationUpdate currentPoint = locationDbHelper.getCurrentLocationUpdate(c);
                
                if (isMoving(lastMovingPoint, currentPoint)) {
                    lastMovingPoint = currentPoint;
                    // Logger.debug(TAG, "Found a newer moving point at " + currentPoint.toString());
                } else {
                    Logger.debug(TAG, "Found a stationary point at " + currentPoint.toString());

                    // if the user has been stationary beyond the end of the trip then
                    // mark the trip ended.
                    if (hasTripEnded(lastMovingPoint, currentPoint)) {
                        Logger.info(TAG, "End of the trip detected at " + currentPoint.toString());
                        
                        newTrip.endPointId = c.getInt(column_id);
                        
                        // We do the geocoding here (No point doing it any earlier)
                        newTrip.startPointName = getLocationName(context, tripStartPoint);
                        newTrip.endPointName = getLocationName(context, currentPoint);

                        return newTrip;
                    }
                }
            }
            
            // We have gone through all the updates but the trip doesn't seem to have ended
            Logger.info(TAG, "Trip started but didn't end");
            return null;
        } else {
            Logger.info(TAG, "No location updates after time " + timestamp);
            return null;
        }
    }
    

    /**
     * Go over the cursor and see if there is any location update that is considered
     * to be a movement from the last location.
     * 
     * This *will* update the cursor
     * 
     * @param locationDbHelper
     * @param c
     * @param lastPoint
     * @return
     */
    private static LocationUpdate skipStationaryPoints(LocationDbHelper locationDbHelper, 
            Cursor c, LocationUpdate lastPoint) {
        
        Integer count = 0;
        do {
            LocationUpdate currentPoint = locationDbHelper.getCurrentLocationUpdate(c);
            if (isMoving(lastPoint, currentPoint)) {
                Logger.info(TAG, "Skipped over " + count.toString() + " stationary points.");
                return currentPoint;
            }
            Logger.debug(TAG, "Skipping over stationary point at " + currentPoint.toString());
            count++;
        } while (c.moveToNext());
        
        Logger.info(TAG, "User has been stationary since last trip ended.  No new trip");
        return null;
    }
    
    
    /**
     * Algo: How to decide whether a user was moving, by looking at 2 location updates
     *    (point2.distance(point1) > cutoff, point2.speed = 0)
     * 
     * @param lastMovingPoint
     * @param currentPoint
     * @return
     */
    private static boolean isMoving(LocationUpdate lastMovingPoint, LocationUpdate currentPoint) {
        double distance = SimpleGeoPoint.getDistance(
                lastMovingPoint.getLatitude(), lastMovingPoint.getLongitude(),
                currentPoint.getLatitude(), currentPoint.getLongitude() );

        Logger.debug(TAG, String.format("Distance = %f between %f,%f to %f,%f", 
                distance,
                lastMovingPoint.getLatitude(), lastMovingPoint.getLongitude(),
                currentPoint.getLatitude(), currentPoint.getLongitude()));

        // TODO: Need to come with better logic for updates with varying accuracy levels
        if (distance > DIST_THRESHOLD) {
            return true;
        }
        
        if (currentPoint.getSpeed() > SPEED_THRESHOLD) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Algo: How to decide whether a user was moving, by looking at 2 location updates
     *    (point2.distance(point1) > cutoff, point2.speed = 0)
     * 
     * @param lastMovingPoint
     * @param currentPoint
     * @return
     */
    public static boolean isMoving(Location lastMovingPoint, Location currentPoint) {
        double distance = SimpleGeoPoint.getDistance(
                lastMovingPoint.getLatitude(), lastMovingPoint.getLongitude(),
                currentPoint.getLatitude(), currentPoint.getLongitude() );

        Logger.debug(TAG, String.format("Distance = %f between %f,%f to %f,%f", 
                distance,
                lastMovingPoint.getLatitude(), lastMovingPoint.getLongitude(),
                currentPoint.getLatitude(), currentPoint.getLongitude()));

        if (distance > DIST_THRESHOLD) {
            return true;
        }
        
        Logger.debug(TAG, "Speed = " + currentPoint.getSpeed() );
        if (currentPoint.getSpeed() > SPEED_THRESHOLD) {
            return true;
        }
        
        return false;
    }
    

    
    private static boolean hasTripEnded(LocationUpdate lastMovingPoint, LocationUpdate currentPoint) {
        if (lastMovingPoint.getEpochTime() + TIME_CUTOFF > currentPoint.getEpochTime()) {
            return false;
        } else {
            return true;
        }
    }
    
    
    /**
     * Try to get a short but descriptive location name.
     * If nothing can be found, return "-unknown-"
     * 
     * @param context
     * @param location
     * @return
     */
    private static String getLocationName(Context context, LocationUpdate location) {
        GeocodingService geoService = new AndroidGeocodingService(context);
        SimpleGeoPoint sgPoint = new SimpleGeoPoint(location.getLatitude(), location.getLongitude());
        
        List<Address> addresses = geoService.resolveLocation(sgPoint, 1);
        if ((addresses == null) || (addresses.size()==0)) {
            return "-unknown-";
        }
        
        StringBuilder name = new StringBuilder();
        Address address = addresses.get(0);
        boolean addcomma = false;
        
        String area = address.getFeatureName();
        String sublocality = address.getSubLocality();
        String locality = address.getLocality();
        
        // The intent is to print upto 2 data points.
        int count = 0;
        
        if (area != null) {
            name.append(area);
            count++;
        }
        
        if (sublocality != null) {
            if (count>0) { name.append(", "); }
            name.append(sublocality);
            count++;
            if (count == 2) {
                return name.toString();
            }
        }
        
        if (locality != null) {
            if (count>0) { name.append(", "); }
            name.append(locality);
        }

        if (count == 0) {
            return "-unknown-";
        }

        return name.toString();
    }
    
}
