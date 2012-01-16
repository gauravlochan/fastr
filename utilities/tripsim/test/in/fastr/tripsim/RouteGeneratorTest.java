package in.fastr.tripsim;

import static org.junit.Assert.assertNotNull;
import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.SimpleGeoPoint;

import org.junit.Test;

public class RouteGeneratorTest {

    @Test
    public void testGetNotNullRoute() {
        RouteGenerator br = new RouteGenerator();
        Route route = br.getEmptyRoute();
        assertNotNull(route);
    }

    
    @Test
    public void testGetRealRoute() {
        // SJW bus stop
        SimpleGeoPoint source = new SimpleGeoPoint(12.929613, 77.615546);
        // fanoos
        SimpleGeoPoint dest = new SimpleGeoPoint(12.964491, 77.606702);
        
        RouteGenerator br = new RouteGenerator();
        Route route = br.getRoute(source, dest);

        assertNotNull(route);
    }

    
    
}
