package in.fastr.tripsim;

import in.beetroute.apps.commonlib.ServiceProviders;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.Route;
import in.beetroute.apps.traffic.google.directions.GoogleDirectionsService;
import in.beetroute.apps.traffic.services.DirectionsService;

import java.util.List;

public class RouteGenerator {
    
    public Route getEmptyRoute() {
        return new Route(ServiceProviders.SIMULATOR);
    }
    
    public Route getRoute(SimpleGeoPoint source, SimpleGeoPoint dest) {
        DirectionsService dir = new GoogleDirectionsService();
        List<Route> routes = dir.getRoutes(source, dest);
        return routes.get(0);
    }

    
}
