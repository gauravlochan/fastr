package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.MapPoint;

import java.util.List;

/**
 * Interface for getting information for points of interest.
 * 
 * This may be implemented by a service such as:
 * - Google Places, http://code.google.com/apis/maps/documentation/places/
 * - Onze/Latlong, https://github.com/mayanks/latlong/wiki/Latlong-API---PI-Hackathon
 * - Our own implementation, perhaps using OpenStreetMap
 * 
 * @author gauravlochan
 */
public interface PointOfInterestService {
	List<MapPoint> getPoints(String nameOfPlace);

}
