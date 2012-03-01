package in.fastr.tripsim;

import static org.junit.Assert.assertNotNull;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.Route;
import in.beetroute.apps.traffic.onze.OnzePointOfInterestService;
import in.beetroute.apps.traffic.services.PointOfInterestService;

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
