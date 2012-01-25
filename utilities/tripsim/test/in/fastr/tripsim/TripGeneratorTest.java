package in.fastr.tripsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.SimpleGeoPoint;

import java.util.Date;

import org.junit.Test;

public class TripGeneratorTest {

    /**
     * A simple test to invoke the constantSpeedTrip code 
     * 
     * This doesnt check the correctness of the trip apart from the number of points
     */
    @Test
    public void testConstantSpeedTrip() {
        // SJW bus stop
        SimpleGeoPoint source = new SimpleGeoPoint(12.929613, 77.615546);
        // fanoos
        SimpleGeoPoint dest = new SimpleGeoPoint(12.964491, 77.606702);
        
        RouteGenerator br = new RouteGenerator();
        Route route = br.getRoute(source, dest);

        TripGenerator tr = new TripGenerator();
        Trip trip = tr.constantSpeedTrip(route.getPoints(), new Date(), 40);
        
        printTrip(trip);
        
        assertNotNull(trip);
        assertEquals(route.getPoints().size(), trip.getPoints().size());
    }
 

    /**
     * A simple test to invoke the randomSpeedTrip code 
     * 
     * This doesnt check the correctness of the trip apart from the number of points
     */
    @Test
    public void testRandomSpeedTrip() {
        // SJW bus stop
        SimpleGeoPoint source = new SimpleGeoPoint(12.929613, 77.615546);
        // fanoos
        SimpleGeoPoint dest = new SimpleGeoPoint(12.964491, 77.606702);
        
        RouteGenerator br = new RouteGenerator();
        Route route = br.getRoute(source, dest);

        TripGenerator tr = new TripGenerator();
        Trip trip = tr.randomSpeedTrip(route.getPoints(), new Date(), 20, 40);
        
        printTrip(trip);
        
        assertNotNull(trip);
        assertEquals(route.getPoints().size(), trip.getPoints().size());
    }

    
    private void printTrip(Trip trip) {
        String print = "";
        int count = 0;
        for (TripPoint point: trip.getPoints()) {
            print += count + ") "+ point.simpleGeoPoint + " at " + point.timestamp + 
                    " at speed " + point.speed + "\n";
            count++;
        }
        System.out.print(print);
        
    }

}
