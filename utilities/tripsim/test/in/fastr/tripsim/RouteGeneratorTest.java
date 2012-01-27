package in.fastr.tripsim;

import static org.junit.Assert.assertNotNull;
import in.fastr.apps.traffic.MapPoint;
import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.onze.OnzePointOfInterestService;
import in.fastr.apps.traffic.services.PointOfInterestService;
import in.fastr.library.SimpleGeoPoint;

import java.util.List;

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
        PointOfInterestService poiService = new OnzePointOfInterestService();
        
        List<MapPoint> mapPoints = poiService.getPoints("fanoos");
        SimpleGeoPoint source = mapPoints.get(0).getSimpleGeoPoint();

        List<MapPoint> mapPoints2 = poiService.getPoints("apollo hospitals");
        SimpleGeoPoint dest = mapPoints2.get(0).getSimpleGeoPoint();
        
        RouteGenerator br = new RouteGenerator();
        Route route = br.getRoute(source, dest);

        assertNotNull(route);
    }

    
    
}
