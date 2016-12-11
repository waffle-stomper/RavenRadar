package wafflestomper.ravenradar;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebData {
	
	private static URL url;
	private boolean disabled = false;
	private boolean gotData = false;
	private ArrayList<WebWaypoint> waypoints = new ArrayList<WebWaypoint>();
	private static final double y = RavenRadar.instance.getConfig().getDefaultWebWaypointY();
	private static final Logger logger = LogManager.getLogger("RavenRadar:WebData");

	public WebData(){
		if (RavenRadar.instance.getConfig().getWebWaypointsEnabled() == false){
			this.disabled = true;
			return;
		}
		try {
			url = new URL("https://spreadsheets.google.com/feeds/list/<SHEET_ID_GOES_HERE>/od6/public/values?alt=json");
		} catch (MalformedURLException e) {
			this.disabled = true;
			e.printStackTrace();
		}
	}
	

	private double extractDouble(JsonObject o, String identifier, int lineNum, boolean suppressError){
		if (o.has(identifier)){
			JsonElement ele = o.get(identifier);
			if(!ele.isJsonNull() && ele.isJsonObject()){
				JsonObject obj = ele.getAsJsonObject();
				if (obj.has("$t") && !obj.get("$t").isJsonNull()){
					try{
						return(obj.get("$t").getAsDouble());
					}
					catch (NumberFormatException n){
						if (!suppressError){
							logger.error("Error parsing " + identifier + " cell on line " + lineNum);
						}
					}
				}
			}
		}
		return(31337.31337d);
	}
	
	
	private float extractFloat(JsonObject o, String identifier, int lineNum, boolean suppressError){
		return((float)extractDouble(o, identifier, lineNum, suppressError));
	}
	
	
	private String extractString(JsonObject o, String identifier){
		if (o.has(identifier)){
			JsonElement ele = o.get(identifier);
			if(!ele.isJsonNull() && ele.isJsonObject()){
				JsonObject obj = ele.getAsJsonObject();
				if (obj.has("$t") && !obj.get("$t").isJsonNull()){
					return(obj.get("$t").getAsString());
				}
			}
		}
		return("NO_STRING");
	}
	
	
	public void fetchData(){
		if (this.disabled){
			return;
		}
		
		this.waypoints.clear();
		
		// Download the data
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    String jsonString = "";
	    try {
	        is = url.openStream();
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null) {
	            jsonString += line;
	        }
	    } 
	    catch (MalformedURLException mue) {
	         mue.printStackTrace();
	         this.disabled = true;
	    } 
	    catch (IOException ioe) {
	         ioe.printStackTrace();
	         this.disabled = true;
	    } 
	    finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
		         this.disabled = true;
	        }
	    }
	    
	    // Parse JSON into waypoints
	    JsonArray entries = null;
		JsonParser parser = new JsonParser();
		JsonElement tree = parser.parse(jsonString);
		if (!tree.isJsonNull() && tree.isJsonObject()){
			JsonObject base = tree.getAsJsonObject();
			if (base.has("feed")){
				JsonElement fe = base.get("feed");
				if (!fe.isJsonNull() && fe.isJsonObject()){
					JsonObject feed = fe.getAsJsonObject();
					if (feed.has("entry")){
						JsonElement en = feed.get("entry");
						if (!en.isJsonNull() && en.isJsonArray()){
							entries = en.getAsJsonArray();
						}
					}
				}
			}
		}
		
		if (entries == null){
			return;
		}
		
		int lineNum = 1;
		for (JsonElement e : entries){
			lineNum++;
			if (!e.isJsonNull() && e.isJsonObject()){
				JsonObject currLine = e.getAsJsonObject();
				double x = extractDouble(currLine, "gsx$x", lineNum, false);
				double y = extractDouble(currLine, "gsx$y", lineNum, true);
				double z = extractDouble(currLine, "gsx$z", lineNum, false);
				String name = extractString(currLine, "gsx$name");
				String contents = extractString(currLine, "gsx$contents");
				float r = extractFloat(currLine, "gsx$r", lineNum, true);
				float g = extractFloat(currLine, "gsx$g", lineNum, true);
				float b = extractFloat(currLine, "gsx$b", lineNum, true);
				if (x >= 31337 || z >= 31337 || contents.equals("NO_STRING") || name.equals("NO_STRING")){
					logger.error("Skipping line " + lineNum);
					continue;
				}
				if (y > 31337){
					y = RavenRadar.instance.getConfig().getDefaultWebWaypointY();
				}
				Random random = new Random();
				if (r > 1f || r < 0f){
					r = random.nextFloat();
				}
				if (g > 1f || g < 0f){
					g = random.nextFloat();
				}
				if (b > 1f || b < 0f){
					b = random.nextFloat();
				}
				this.waypoints.add(new WebWaypoint(x, y, z, name, contents, r, g, b));
			}
		}
	    
	    gotData = true;
	    logger.warn("Fetched " + this.waypoints.size() + " waypoints from the spreadsheet");
	}
	
	
	public boolean hasData(){
		return(this.gotData);
	}
	
	
	public List<WebWaypoint> getWaypoints(){
		return(this.waypoints);
	}
}
