package wafflestomper.ravenradar;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import wafflestomper.ravenradar.gui.GuiAddWaypoint;
import wafflestomper.ravenradar.gui.GuiRadarOptions;

@Mod(modid=RavenRadar.MODID, name=RavenRadar.MODNAME, version=RavenRadar.VERSION, dependencies = "required-after:wafflecore",
     updateJSON = "https://raw.githubusercontent.com/waffle-stomper/RavenRadar/master/update.json", canBeDeactivated = true)
public class RavenRadar {
	
	//TODO: Fix squashed witch icons
	
	public static final String MODID = "ravenradar";
	public static final String MODNAME = "RavenRadar";
	public static final String VERSION = "1.0.5";
	private RenderHandler renderHandler;
	private Config radarConfig;
	private File configFile;
	private KeyBinding radarOptions = new KeyBinding("RavenRadar Settings", Keyboard.KEY_R, "RavenRadar");
	private KeyBinding addWaypoint = new KeyBinding("Add Waypoint", Keyboard.KEY_NUMPAD1, "RavenRadar");
	Minecraft mc;
	public static RavenRadar instance;
	private WaypointSave currentWaypoints;
	private File saveFile;
	public static String currentServer = "";
	public static File waypointDir;
	private File radarDir;
	private static WebData webdata;
	public static Logger logger = LogManager.getLogger("RavenRadar");
	private boolean devEnv = false;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.devEnv = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
		mc = Minecraft.getMinecraft();
		instance = this;
		File oldConfig = new File(event.getModConfigurationDirectory(), "RavenRadar.json");
		File radarDir = new File(mc.mcDataDir,  "/mods/RavenRadar/");
		if(!radarDir.isDirectory()) {
			radarDir.mkdir();
		}
		configFile = new File(radarDir, "config.json");
		if(oldConfig.exists()) {
			try {
				FileUtils.copyFile(oldConfig, configFile);
				oldConfig.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!configFile.isFile()) {
			try {
				configFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			radarConfig = new Config();
			radarConfig.save(configFile);
		} else {
			radarConfig = Config.load(configFile);
			if(radarConfig == null) {
				radarConfig = new Config();
			}
			radarConfig.save(configFile);
		}
		renderHandler = new RenderHandler();
		
		waypointDir = new File(radarDir, "/waypoints/");
		if(!waypointDir.isDirectory()) {
			waypointDir.mkdir();
		}
		currentWaypoints = new WaypointSave();
		FMLCommonHandler.instance().bus().register(renderHandler);
		MinecraftForge.EVENT_BUS.register(renderHandler);
		FMLCommonHandler.instance().bus().register(this);
		ClientRegistry.registerKeyBinding(radarOptions);
		ClientRegistry.registerKeyBinding(addWaypoint);
		webdata = new WebData();
		webdata.fetchData();
	}
	
	
	@SubscribeEvent
	public void keyPress(KeyInputEvent event) {
		if(radarOptions.isPressed()) {
			mc.displayGuiScreen(new GuiRadarOptions(mc.currentScreen));
		}
		if(addWaypoint.isPressed()) {
			mc.displayGuiScreen(new GuiAddWaypoint(mc.currentScreen));
		}
	}

	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(mc.theWorld != null) {
			if(mc.isSingleplayer()) {
				String worldName = mc.getIntegratedServer().getWorldName();
				if(worldName == null) {
					return;
				}
				if(!currentServer.equals(worldName)) {
					currentServer = worldName;
					loadWaypoints(new File(waypointDir, worldName + ".points"));
				}
			} else if (mc.getCurrentServerData() != null) {
				String s = mc.getCurrentServerData().serverIP;
				if (s.contains(":")){
					s = s.substring(0, s.indexOf(':'));
				}
				if(!currentServer.equals(s)) {
					currentServer = s;
					loadWaypoints(new File(waypointDir, currentServer + ".points"));
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public void onDisconnect(ClientDisconnectionFromServerEvent event) {
		currentServer = "";
	}
	
	
	public void insertWebWaypoints(){
		if (currentServer.equals("mc.civcraft.co") || this.devEnv == true){
			// Sync web data
			if (webdata.hasData()){
				logger.warn("Syncing web waypoints with local waypoints...");
				WaypointSave newWaypoints = new WaypointSave();
				for (WebWaypoint ww : webdata.getWaypoints()){
					logger.warn(ww.toString());
					boolean pointExists = false;
					Waypoint newPoint = new Waypoint(ww.getX(), ww.getY(), ww.getZ(), ww.getName(), ww.getColor(), true);
					newWaypoints.addWaypoint(newPoint);
				}
				currentWaypoints = newWaypoints;
			}
		}
	}
	
	
	public void loadWaypoints(File saveFile) {
		if(!saveFile.isFile()) {
			try {
				saveFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentWaypoints.save(saveFile);
		}
		currentWaypoints = WaypointSave.load(saveFile);
		insertWebWaypoints();
		currentWaypoints.save(saveFile);
		this.saveFile = saveFile;
	}
	
	
	public Config getConfig() {
		return radarConfig;
	}
	
	
	public void saveConfig() {
		logger.info("Saving config!");
		radarConfig.save(configFile);
	}
	
	
	public WaypointSave getWaypointSave() {
		return currentWaypoints;
	}
	
	
	public void saveWaypoints() {
		currentWaypoints.save(this.saveFile);
	}
	
	
	public boolean isDevEnv(){
		return(this.devEnv);
	}
	
	
	/**
	 * Triggers a new download and insert of waypoints from the spreadsheet
	 */
	public void refreshWaypoints(){
		webdata.fetchData();
		currentWaypoints = WaypointSave.load(saveFile);
		insertWebWaypoints();
		currentWaypoints.save(saveFile);
	}
}
