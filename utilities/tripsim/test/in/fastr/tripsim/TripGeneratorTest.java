package in.fastr.tripsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.SimpleGeoPoint;

import java.util.Date;

import org.junit.Test;

public class TripGeneratorTest {

    @Test
    public void testConstantSpeedTrip() {
        // SJW bus stop
        SimpleGeoPoint source = new SimpleGeoPoint(12.929613, 77.615546);
        // fanoos
        SimpleGeoPoint dest = new SimpleGeoPoint(12.964491, 77.606702);
        
        RouteGenerator br = new RouteGenerator();
        Route route = br.getRoute(source, dest);

        TripGenerator tr = new TripGenerator();
        Trip trip = tr.constantSpeedTrip(route.getPoints(), 40, new Date());
        
        assertNotNull(trip);
        assertEquals(route.getPoints().size(), trip.getPoints().size());
    }

}
