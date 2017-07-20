package wafflestomper.ravenradar.gui;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import wafflestomper.ravenradar.RavenRadar;
import wafflestomper.ravenradar.Config;

public class GuiRadarOptions extends GuiScreen {

	
	private GuiScreen parentScreen;
	private GuiSlider opacitySlider;
	private GuiSlider radarScaleSlider;
	private GuiSlider iconScaleSlider;
	private GuiSlider iconSpacingSlider;
	private GuiButton coordToggle;
	private GuiButton radarButton;
	
	private static final int BTN_REPOSITION_RADAR = 0;
	private static final int BTN_ICON_SETTINGS = 1;
	private static final int SLD_RADAR_OPACITY = 3;
	private static final int BTN_RADAR_COLOR = 4;
	private static final int BTN_PLAYER_OPTIONS = 5;
	private static final int SLD_RADAR_SCALE = 6;
	private static final int BTN_COORD_TOGGLE = 7;
	private static final int BTN_WAYPOINT_OPTIONS = 8;
	private static final int BTN_RADAR_TOGGLE = 10;
	private static final int BTN_REFRESH_WAYPOINTS = 20;
	private static final int SLD_ICON_SCALE = 21;
	private static final int SLD_ICON_SPACING = 22;
	private static final int BTN_DONE = 100;
	
	
	private static final Logger logger = LogManager.getLogger("RavenRadar:GuiRadarOptions");
	
	public GuiRadarOptions(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
	}
	
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(BTN_REPOSITION_RADAR, this.width / 2 - 100, this.height / 4 - 32, 100, 20, "Reposition Radar"));
		this.buttonList.add(new GuiButton(BTN_ICON_SETTINGS, this.width / 2 + 1, this.height / 4 - 32, 100, 20, "Icon Settings"));
		this.buttonList.add(this.opacitySlider = new GuiSlider(SLD_RADAR_OPACITY, this.width / 2 -100, this.height / 4 -10, 1.0F, 0.0F, "Radar Opacity", RavenRadar.instance.getConfig().getRadarOpacity()));
		this.buttonList.add(new GuiButton(BTN_RADAR_COLOR, this.width / 2 - 100, this.height / 4 + 12, 100, 20, "Edit Radar Color"));
		this.buttonList.add(new GuiButton(BTN_PLAYER_OPTIONS, this.width / 2 + 1, this.height /4 + 12, 100, 20, "Edit Player Options"));
		this.buttonList.add(this.radarScaleSlider = new GuiSlider(SLD_RADAR_SCALE, this.width / 2 - 100, this.height / 4 + 34, 2.0F, 1.0F, "Radar Scale", RavenRadar.instance.getConfig().getRadarScale()));
		this.buttonList.add(this.iconScaleSlider = new GuiSlider(SLD_ICON_SCALE, this.width / 2 - 100, this.height / 4 + 56, 1.5F, 0.5F, "Icon Scale", RavenRadar.instance.getConfig().getIconScale()));
		// TODO: Implement my own version of GuiSlider so that it accepts any min and max values (not just a pair that are exactly 1 apart)
		this.buttonList.add(this.iconSpacingSlider = new GuiSlider(SLD_ICON_SPACING, this.width / 2 - 100, this.height / 4 + 78, 2.5F, 1.5F, "Icon Spacing", RavenRadar.instance.getConfig().geticonSpacing()));
		this.buttonList.add(this.coordToggle = new GuiButton(BTN_COORD_TOGGLE, this.width / 2 - 100, this.height / 4 + 100, 100, 20, "Coordinates: "));
		this.buttonList.add(new GuiButton(BTN_WAYPOINT_OPTIONS, this.width / 2 + 1, this.height / 4 + 100, 100, 20, "Waypoint Options"));
		this.buttonList.add(this.radarButton = new GuiButton(BTN_RADAR_TOGGLE, this.width / 2 - 100, this.height / 4 + 122, 100, 20, "Radar: "));
		if (RavenRadar.instance.getConfig().getWebWaypointsEnabled() == true){
			this.buttonList.add(new GuiButton(BTN_REFRESH_WAYPOINTS, this.width / 2 + 1, this.height / 4 + 122, 100, 20, "Refresh Waypoints"));
		}
		this.buttonList.add(new GuiButton(BTN_DONE, this.width / 2 - 100, this.height / 4 + 144, "Done"));
	}
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		RavenRadar.instance.saveConfig();
	}
	
	public void actionPerformed(GuiButton guiButton) {
		if(!guiButton.enabled){
			return;
		}
		
		switch (guiButton.id){
			case BTN_REPOSITION_RADAR:
				mc.displayGuiScreen(new GuiRepositionRadar(this));
				break;
			case BTN_REFRESH_WAYPOINTS:
				RavenRadar.instance.refreshWaypoints();
				break;
			case BTN_ICON_SETTINGS:
				mc.displayGuiScreen(new GuiEntitySettings(this));
				break;
			case BTN_RADAR_COLOR:
				mc.displayGuiScreen(new GuiEditRadarColor(this));
				break;
			case BTN_PLAYER_OPTIONS:
				mc.displayGuiScreen(new GuiPlayerOptions(this));
				break;
			case BTN_COORD_TOGGLE:
				RavenRadar.instance.getConfig().setRenderCoordinates(!RavenRadar.instance.getConfig().shouldRenderCoordinates());
				RavenRadar.instance.saveConfig();
				break;
			case BTN_RADAR_TOGGLE:
				RavenRadar.instance.getConfig().setEnabled(!RavenRadar.instance.getConfig().isEnabled());
				RavenRadar.instance.saveConfig();
				break;
			case BTN_WAYPOINT_OPTIONS:
				mc.displayGuiScreen(new GuiWaypointOptions(this));
				break;
			case BTN_DONE:
				mc.displayGuiScreen(parentScreen);
				break;
		}
	}
	
	/**
	 * Compares two floats to determine if they are significantly different
	 * @param oldValue first float
	 * @param newValue second float
	 * @return true if they're significantly different
	 */
	private static boolean floatChanged(float oldValue, float newValue){
		if (Math.abs(oldValue - newValue) > 0.01){
			return true;
		}
		return false;
	}
	
	
	public void updateScreen() {
		Config config = RavenRadar.instance.getConfig();
		config.setRadarScale(this.radarScaleSlider.getCurrentValue());
		config.setIconScale(this.iconScaleSlider.getCurrentValue());
		config.setRadarOpacity(opacitySlider.getCurrentValue());
		config.setIconSpacing(this.iconSpacingSlider.getCurrentValue());
		coordToggle.displayString = "Coordinates: " + (RavenRadar.instance.getConfig().shouldRenderCoordinates() ? "On" : "Off");
		radarButton.displayString = "Radar: " + (RavenRadar.instance.getConfig().isEnabled() ? "On" : "Off");
		opacitySlider.updateDisplayString();
		this.radarScaleSlider.displayString = String.format("Radar scale: %.2f", this.radarScaleSlider.getCurrentValue());
		this.iconScaleSlider.displayString = String.format("Icon scale: %.2f", this.iconScaleSlider.getCurrentValue());
		this.iconSpacingSlider.displayString = String.format("Icon spacing: %.2f", this.iconSpacingSlider.getCurrentValue());
	}
	
	public void drawScreen(int i, int j, float k) {
		drawDefaultBackground();
		drawCenteredString(this.fontRenderer, "RavenRadar Options", this.width / 2, this.height / 4 - 50, Color.WHITE.getRGB());
		super.drawScreen(i, j, k);
	}
}
