package in.beetroute.apps.traffic.google.directions;

import in.beetroute.apps.traffic.google.GoogleLatlong;

class Step {
	public TextValueObject distance;
	public TextValueObject duration;

	public GoogleLatlong end_location;
	
    public String html_instructions;
    
    public Polyline polyline;
    
    public GoogleLatlong start_location;

    public String travel_mode;
}
