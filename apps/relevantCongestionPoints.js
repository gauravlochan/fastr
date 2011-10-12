/*Input  data: 
 We get a set of congestion points from a source - Currently BTIS. 
 This is a set of JSON objects. 
 Each object contains a lat and long along with additional parameters.

 Input Query: 
 Source point, Destination point.
 
 Output result: Direction from source to destination with color coded congestion markers. 

 Logic:
 From a vast set of congestion points, choose the relevant congestion points that lie along the route from source to destination.
 So load all congestion point objects to an array.
 Source point object TO Destination point object - get the route.
 Pick a few points along the route.
 For each of these points 
	Check if this point is in the array of congestion point objects.
	If this check returns true - mark this point as a relevant congestion point.
 */
var relevantCongestionPoints = function() {

  var congestionPoint { // The main congestion point object.
	var latitude,
	var longitude,
	var isRelevant,
  function compareTo (var anotherCongestionPoint) {
    if ((anotherCongestionPoint.latitude === this.latitude) && (anotherCongestionPoint.longitude === this.longitude)){
      isRelevant = true;
      return isRelevant;
    } else if ((anotherCongestionPoint.latitudei+10 === this.latitude)&& (anotherCongestionPoint.longitude+10 === this.longitude
          )) {
      isRelevant = true;
      return isRelevant;
    }
  }
  }

// This is an array of congestionPoint Objects
var congestionPointArray = new Array();

// Using this method populate the congestionPointArray by fetching data from the global pool of congestionPoints
function fetchCongestionPointsFromStorage() {
	
}
// Get the route between the source and the destination points
function getRoute(congestionPoint sourcePoint, congestionPoint destinationPoint) {
	var routeArray = new Array();
	return routeArray;
}

// This function will be called by the client and will display the directions with the routeArray 
// points

function getRelevantCongestionPoints(congestionPoint sourcePoint, congestionPoint destinationPoint) {

var routeArray = getRoute(sourcePoint, destinationPoint);
var routeArrayLength = routeArray.length;

for (i=0;i< routeArrayLength;++i) {
	if ( routeArray[i].compareTo(congestionPointArray[i]) === true ) {
		routeArray[i].isRelevant = true;
}
return routeArray;
}	

}

