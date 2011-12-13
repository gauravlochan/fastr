package in.fastr.apps.traffic.json.google.directions;

import in.fastr.apps.traffic.json.google.GoogleLatlong;

public class Step {
	TextValueObject distance;
	TextValueObject duration;

	GoogleLatlong end_location;
	
    public String html_instructions;
    
    public Polyline polyline;
    
	GoogleLatlong start_location;

    public String travel_mode;
}
