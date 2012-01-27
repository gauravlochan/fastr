package in.fastr.tripsim;

import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.google.directions.GoogleDirectionsService;
import in.fastr.apps.traffic.services.DirectionsService;
import in.fastr.library.ServiceProviders;
import in.fastr.library.SimpleGeoPoint;

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
