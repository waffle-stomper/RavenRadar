package wafflestomper.ravenradar;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class Waypoint {

	private double x, y, z;
	private String name;
	private int dimension;
	private boolean enabled;
	private float red, green, blue;
	private boolean userModified = false;
	private boolean fromWeb = true; //Set to true to purge old records that don't contain this value
	
	public Waypoint(double _x, double _y, double _z, String _name, Color _c, boolean _enabled){
		this.x = _x + 0.5;
		this.y = _y;
		this.z = _z + 0.5;
		this.name = _name;
		this.red = _c.getRed() / 255.0F;
		this.green = _c.getGreen() / 255.0F;
		this.blue = _c.getBlue() / 255.0F;
		if (Minecraft.getMinecraft().world != null)
			this.dimension = Minecraft.getMinecraft().world.provider.getDimension();
		this.enabled = _enabled;
	}
	
	public Waypoint(int x, int y, int z, String name, Color c, boolean enabled) {
		this((double)x, (double)y, (double)z, name, c, enabled);
	}
	
	public boolean isFromWeb(){
		return(this.fromWeb);
	}
	
	public boolean isUserModified(){
		return(this.userModified);
	}
	
	public void setUserModified(boolean newStatus){
		this.userModified = newStatus;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public Color getColor() {
		return new Color(red, green, blue);
	}
	
	public void setColor(Color c) {
		this.red = c.getRed() / 255.0F;
		this.green = c.getGreen() / 255.0F;
		this.blue = c.getBlue() / 255.0F;
	}
	
	public float getRed() {
		return red;
	}
	
	public float getGreen() {
		return green;
	}
	
	public float getBlue() {
		return blue;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public double getDistance(Minecraft mc) {
		double d3 = x - mc.player.posX;
        double d4 = y - mc.player.posY;
        double d5 = z - mc.player.posZ;
        return (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
	}
	
	public boolean equals(Waypoint p) {
		return x == p.getX() && y == p.getY() && z == p.getZ() && name.equals(p.getName()) && dimension == p.getDimension();
	}
	
	public void setDimension(int dimension){
		this.dimension = dimension;
	}
}
