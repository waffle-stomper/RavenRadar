package wafflestomper.ravenradar;

import java.awt.Color;
import java.util.Random;

public class WebWaypoint {
	private double x;
	private double y;
	private double z;
	private String name;
	private String contents;
	private float r;
	private float g;
	private float b;
	
	public WebWaypoint(double _x, double _y, double _z, String _name, String _contents, float _r, float _g, float _b){
		this.x = _x;
		this.y = _y;
		this.z = _z;
		this.name = _name;
		this.contents = _contents;
		Random random = new Random();
		this.r = _r;
		this.g = _g;
		this.b = _b;
	}
	
	public WebWaypoint(double _x, double _y, double _z, String _name, String _contents){
		this.x = _x;
		this.y = _y;
		this.z = _z;
		this.name = _name;
		this.contents = _contents;
		Random random = new Random();
		this.r = random.nextFloat();
		this.g = random.nextFloat();
		this.b = random.nextFloat();
	}
	
	public double getX(){
		return(this.x);
	}
	
	public double getY(){
		return(this.y);
	}
	
	public double getZ(){
		return(this.z);
	}
	
	public String getName(){
		return(this.name);
	}
	
	public String getContents(){
		return(this.contents);
	}
	
	public Color getColor(){
		return(new Color(this.r, this.g, this.b));
	}
	
	public String toString(){
		return("[x:" + this.x + ", y:" + this.y + ", z:" + this.z + "] - " + this.name);
	}
	
	/**
	 * Returns true if the x & z co-ordinates and name of the waypoint match the data from this webwaypoint
	 */
	@Override
	public boolean equals(Object o){
		if (o instanceof WebWaypoint){
			WebWaypoint w = (WebWaypoint)o;
			if (Double.compare(this.x, w.getX()) == 0 && Double.compare(this.z, w.getZ()) == 0){
				if (Double.compare(this.y, w.getY()) == 0 && this.name.toLowerCase().equals(w.getName().toLowerCase())){
					if (this.contents.equals(w.getContents())){
						return(true);
					}
				}
			}
		}
		else if (o instanceof Waypoint){
			Waypoint w = (Waypoint)o;
			if (Double.compare(this.x, w.getX()) == 0 && Double.compare(this.z, w.getZ()) == 0){
				if (Double.compare(this.y, w.getY()) == 0 && this.name.toLowerCase().equals(w.getName().toLowerCase())){
					return(true);
				}
			}
		}
		return(false);
	}
}
