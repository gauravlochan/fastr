package in.fastr.apps.traffic.json.google.directions;

import in.fastr.apps.traffic.json.google.GoogleLatlong;

public class Step {
	public TextValueObject distance;
	public TextValueObject duration;

	public GoogleLatlong end_location;
	
    public String html_instructions;
    
    public Polyline polyline;
    
    public GoogleLatlong start_location;

    public String travel_mode;
}
