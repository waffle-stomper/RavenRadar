package wafflestomper.ravenradar;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class Config {
	
	private boolean enabled = true;
	private ArrayList<Entity> mobs;
	private boolean renderCoordinates = true;
	private boolean extraPlayerInfo = true;
	private boolean playerNames = true;
	private int radarXoffset = 0;
	private int radarYoffset = 0;
	private boolean radarAlignLeft = true;
	private boolean radarAlignTop = true;
	private int maxWaypointDistance = 500;
	private float radarOpacity = 0.25F;
	private float iconOpacity = 1.0F;
	private float waypointOpcaity = 1.0F;
	private boolean renderWaypoints = true;
	private Color radarColor = new Color(0.25F, 0.25F, 0.25F);
	private float radarScale = 1.0F;
	private float iconScale = 1.0F;
	private float iconSpacing = 2.4F;
	public enum NameLocation {above,below};
	private NameLocation nameLocation = NameLocation.below;
	private float pingVolume = 0.5F;
	private double defaultWebWaypointY = 1.1d;
	private boolean webWaypointsEnabled = false;
	private String webWaypointsURL = "";
	
	
	public Config() {
		mobs = getDefaultMobs();
	}
	
	
	private static ArrayList<Entity>getDefaultMobs(){
		return new ArrayList<Entity>(Arrays.asList(new Entity[]{
			new Entity(EntityBat.class), 
			new Entity(EntityBlaze.class),
			new Entity(EntityCaveSpider.class),
			new Entity(EntityChicken.class),
			new Entity(EntityCow.class),
			new Entity(EntityCreeper.class),
			new Entity(EntityDonkey.class),
			new Entity(EntityElderGuardian.class),
			new Entity(EntityEnderman.class),
			new Entity(EntityEndermite.class),
			new Entity(EntityEvoker.class),
			new Entity(EntityGhast.class),
			new Entity(EntityGiantZombie.class),
			new Entity(EntityGuardian.class),
			new Entity(EntityHorse.class),
			new Entity(EntityHusk.class),
			new Entity(EntityIronGolem.class),
			new Entity(EntityItem.class),
			new Entity(EntityLlama.class),
			new Entity(EntityMagmaCube.class),
			new Entity(EntityMinecart.class),
			new Entity(EntityMooshroom.class),
			new Entity(EntityMule.class),
			new Entity(EntityOcelot.class),
			new Entity(EntityPig.class),
			new Entity(EntityPigZombie.class),
			new Entity(EntityPlayer.class),
			new Entity(EntityPolarBear.class),
			new Entity(EntityRabbit.class),
			new Entity(EntitySheep.class),
			new Entity(EntityShulker.class),
			new Entity(EntitySilverfish.class),
			new Entity(EntitySkeleton.class),
			new Entity(EntitySkeletonHorse.class),
			new Entity(EntitySlime.class),
			new Entity(EntitySnowman.class),
			new Entity(EntitySpider.class),
			new Entity(EntityStray.class),
			new Entity(EntitySquid.class),
			new Entity(EntityVex.class),
			new Entity(EntityVillager.class),
			new Entity(EntityVindicator.class),
			new Entity(EntityWitch.class),
			new Entity(EntityWitherSkeleton.class),
			new Entity(EntityWolf.class),
			new Entity(EntityZombie.class),
			new Entity(EntityZombieHorse.class),
			new Entity(EntityZombieVillager.class)
		}));
	}
	
	
	public boolean getWebWaypointsEnabled(){
		if (this.webWaypointsURL.isEmpty()){
			this.webWaypointsEnabled = false;
		}
		return this.webWaypointsEnabled;
	}
	
	
	public void setWebWaypointsEnabled(boolean newState){
		this.webWaypointsEnabled = newState;
	}
	
	
	public String getWebWaypointsURL(){
		return this.webWaypointsURL;
	}

	
	public ArrayList<Entity> getEntities() {
		ArrayList<Entity> allEntities = new ArrayList<Entity>();
		allEntities.addAll(mobs);
		return allEntities;
	}
	
	
	public void setRender(Class entityClass, boolean enabled) {
		for(Entity e : mobs) {
			if(e.getEntityClass().equals(entityClass))
				e.setEnabled(enabled);
		}
	}
	
	
	public boolean isRender(Class entityClass) {
		for(Entity e : mobs) {
			if(e.getEntityClass().equals(entityClass)) {
				return e.isEnabled();
			}
		}
		return false;
	}
	
	
	public NameLocation getNameLocation() {
		return nameLocation;
	}
	
	
	public void switchNameLocation() {
		if(nameLocation == NameLocation.above) {
			nameLocation = NameLocation.below;
		} else {
			nameLocation = NameLocation.above;
		}
	}
	
	
	public void setRadarScale(float radarScale) {
		this.radarScale = radarScale;
	}
	
	
	public float getRadarScale() {
		return this.radarScale;
	}
	
	
	public void setIconScale(float iconScale) {
		this.iconScale = iconScale;
	}
	
	
	public float getIconScale() {
		return this.iconScale;
	}
	
	
	public float geticonSpacing() {
		return this.iconSpacing;
	}
	
	
	public void setIconSpacing(float iconSpacing) {
		this.iconSpacing = iconSpacing;
	}
	
	
	public Entity getMob(Class entityClass) {
		for(Entity e : mobs) {
			if(e.getEntityClass().equals(entityClass))
				return e;
		}
		return null;
	}
	
	
	public void setExtraPlayerInfo(boolean extraPlayerInfo) {
		this.extraPlayerInfo = extraPlayerInfo;
	}
	
	
	public boolean showExtraPlayerInfo() {
		return extraPlayerInfo;
	}
	
	
	public float getPingVolume() {
		return pingVolume;
	}
	
	
	public void setPingVolume(float pingVolume) {
		this.pingVolume = pingVolume;
	}
	
	
	public boolean showPlayerNames() {
		return playerNames;
	}
	

	public void setPlayerNames(boolean playerNames) {
		this.playerNames = playerNames;
	}
	

	public void setColor(Color c) {
		this.radarColor = c;
	}
	
	
	public void setColor(float red, float green, float blue) {
		this.radarColor = new Color(red, green, blue);
	}
	
	
	public Color getRadarColor() {
		return radarColor;
	}
	
	
	public boolean isEnabled() {
		return enabled;
	}
	
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	public boolean shouldRenderCoordinates() {
		return renderCoordinates;
	}
	
	
	public void setRenderCoordinates(boolean renderCoordinates) {
		this.renderCoordinates = renderCoordinates;
	}
	
	
	public int getRadarOffsetX() {
		return radarXoffset;
	}
	
	
	public void setRadarOffsetX(int radarOffsetX) {
		this.radarXoffset = radarOffsetX;
	}
	
	
	public int getRadarOffsetY() {
		return radarYoffset;
	}
	
	
	public void setRadarOffsetY(int radarOffsetY) {
		this.radarYoffset = radarOffsetY;
	}
	
	
	public int getMaxWaypointDistance() {
		return maxWaypointDistance;
	}
	
	
	public void setMaxWaypointDistance(int maxWaypointDistance) {
		this.maxWaypointDistance = maxWaypointDistance;
	}
	
	
	public float getRadarOpacity() {
		return radarOpacity;
	}
	

	public void setRadarOpacity(float radarOpacity) {
		this.radarOpacity = radarOpacity;
	}
	
	
	public float getIconOpacity() {
		return iconOpacity;
	}
	
	
	public void setIconOpacity(float iconOpacity) {
		this.iconOpacity = iconOpacity;
	}
	

	public float getWaypointOpcaity() {
		return waypointOpcaity;
	}
	

	public void setWaypointOpcaity(float waypointOpcaity) {
		this.waypointOpcaity = waypointOpcaity;
	}
	
	
	public boolean shouldRenderWaypoints() {
		return renderWaypoints;
	}
	
	
	public void setRenderWaypoints(boolean renderWaypoints) {
		this.renderWaypoints = renderWaypoints;
	}
	
	
	public boolean getRadarAlignLeft(){
		return(this.radarAlignLeft);
	}
	
	
	public boolean getRadarAlignTop(){
		return(this.radarAlignTop);
	}
	
	
	public void setRadarAlignLeft(boolean alignLeft){
		this.radarAlignLeft = alignLeft;
	}
	
	
	public void setRadarAlignTop(boolean alignTop){
		this.radarAlignTop = alignTop;
	}
	
	
	public double getDefaultWebWaypointY(){
		return(this.defaultWebWaypointY);
	}

	
	public void setDefaultWebWaypointY(double newY){
		this.defaultWebWaypointY = newY;
	}
	

	public void save(File file) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			String json = gson.toJson(this);
			
			FileWriter fw = new FileWriter(file);
			fw.write(json);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static Config load(File file) {
		Gson gson = new Gson();
		Config loadedConfig = null;
		
		try {
			loadedConfig = (Config) gson.fromJson(new FileReader(file), Config.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (loadedConfig == null){
			return new Config();
		}
				
		// Add any new entities not in the loaded config
		ArrayList<Entity> newMobs = getDefaultMobs();
		for (Entity e1 : newMobs){
			for (Entity e2 : loadedConfig.mobs){
				if (e1.getName().equals(e2.getName())){
					e1.setEnabled(e2.isEnabled());
				}
			}
		}
		loadedConfig.mobs = newMobs;
		return loadedConfig;
	}
	
	
	/**
	 * Check to make sure all icons exist
	 * Note that the game needs to be running before you can call this
	 */
	public static void verifyIcons(){
		RavenRadar.logger.error("$$$$$$$$$$$$$$$$$$$ START OF  MISSING ICONS $$$$$$$$$$$$$$$$$$$");
		for (Entity e : getDefaultMobs()){
			Minecraft mc = Minecraft.getMinecraft();
			ResourceLocation resource = e.getResource();
			try {
				IResource iresource = mc.getResourceManager().getResource(resource);
			} catch (IOException ex) {
				RavenRadar.logger.error("Missing icon: " + e.getEntityName());
			}
		}
		RavenRadar.logger.error("$$$$$$$$$$$$$$$$$$$$$$ END OF  MISSING ICONS $$$$$$$$$$$$$$$$$$$$$");
	}
}
