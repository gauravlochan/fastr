var myLocation, map, myOptions;
function initMap(){
    function fail(){
      console.log("GPS fail.")
      myLocation = new google.maps.LatLng(12.933263920639556,77.54980516764984);
      myOptions = {
                center: myLocation,
                zoom: 15,
                disableDefaultUI: true,
                mapTypeId: google.maps.MapTypeId.ROADMAP
              };
        map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
        // var marker = new google.maps.Marker({
        //     position: myLocation,
        //     map: map,
        //     title:"You are here."
        //  })
    }
    function success(pos){
        myLocation = new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
        myOptions = {
                center: myLocation,
                zoom: 15,
                disableDefaultUI: true,
                mapTypeId: google.maps.MapTypeId.ROADMAP
              };
        map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
        
        var marker = new google.maps.Marker({
            position: myLocation,
            map: map,
            title:"You are here."
         })
    }
    navigator.geolocation.getCurrentPosition(success,fail);
}

function startPositionUpload(){
    geoOptions = {
            frequency: 30*5000,
            maximumAge: 30*5000,
            timeout: 30*5000,
            enableHighAccuracy: true
    };
    counter = 0;
    function success(pos){
        geoObject = {
                lat:pos.coords.latitude,
                long:pos.coords.longitude,
                speed:pos.coords.speed,
                accuracy:pos.coords.accuracy,
                timestamp: new Date(pos.timestamp)
        }
        $.parse.post('geoPosition',geoObject);
//        document.getElementById("pos").innerHTML="lat:"+pos.coords.latitude+" long:"+pos.coords.longitude + " "+ counter;
//        counter = counter+1;
    }
    function failure(){
        console.log("Can't upload position.")
    }
    
    navigator.geolocation.watchPosition(success,failure,geoOptions);   
}

$(function() {
   
   initMap();
   startPositionUpload();
   
   $.parse.init({
       app_id:"w9Rg9jzLRm7UXn38afbDDXbE3YIh2txHg9PwmYxZ",
       rest_key:"le1fv2I5JYPPyuSevG4d9R23gWHeaoNnZsRVBG0v" 
   })   
    
});

function findPlace(){
  destinationName = document.getElementById("search_basic").value;

  service = new google.maps.places.PlacesService(map);
  var request = {
       location:myLocation,
       radius: 50000,
       name: destinationName
}

function callback(results, status){
   if(status == google.maps.places.PlacesServiceStatus.OK){
    plist = document.getElementById("places_list");
        $('ul').empty();
       for(var i = 0; i<results.length;i++){
           var res = results[i];
           elem = document.createElement("li");
           elem.innerHTML = results[i].name+' - '+results[i].vicinity;
          (function (result) {
             $(elem).bind("tap",function(){
              $.mobile.changePage($('#basic_map'));
              destLatLong = new google.maps.LatLng(result.geometry.location.Sa,result.geometry.location.Ta);
                calcRoute(destLatLong);
                google.maps.event.trigger(map, 'resize')
            });
          })(res);  
          $('ul').append(elem);         
       
       }
       
       $('ul').listview('refresh');
   }
}
service.search(request,callback);
}

function plotPlace(destLatLong){
    var marker = new google.maps.Marker({
    position: destLatLong,
    map: map,
    title:"Destination"
  })
}

function calcRoute(destLatLong) {
plotPlace(destLatLong);
var directionsService = new google.maps.DirectionsService();
  directionsDisplay = new google.maps.DirectionsRenderer();
  directionsDisplay.setMap(map);

  var request = {
      origin: myLocation,
      destination: destLatLong,      
      travelMode: google.maps.TravelMode["DRIVING"],
      provideRouteAlternatives: true
  };
  // directionsService.route(request, function(response, status) {
  //   console.log(response);
  //   if (status == google.maps.DirectionsStatus.OK) {
  //     directionsDisplay.setDirections(response);
  //   }
  // });
 directionsService.route(request, function(result, status) {
    var color = ['red','green','blue','yellow'];
    var colorIndex = 0;
    console.log(result);
    if (status == google.maps.DirectionsStatus.OK) {
      var routes = result.routes; // An array of DirectionsRoute objects.
      for(var i=0;i<routes.length;++i) { // Loop through the array to get the individual directionsRoute object.
        var overviewPath = routes[i].overview_path; //An overviewPath is the array containing all the latLongs of the individual route.
        console.log(overviewPath);
        polyLine = new google.maps.Polyline({ //Displaying the path using the polyLine object.
          path:overviewPath, 
           strokeColor: color[colorIndex], 
           strokeOpacity:1.0,
           strokeWeight:2
        });
        colorIndex++;
        polyLine.setMap(map);
      }
    }
  });
}