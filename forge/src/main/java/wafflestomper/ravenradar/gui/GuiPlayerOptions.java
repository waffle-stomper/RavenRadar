package wafflestomper.ravenradar.gui;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import wafflestomper.ravenradar.RavenRadar;
import wafflestomper.ravenradar.Config;

public class GuiPlayerOptions extends GuiScreen {

	private GuiScreen parent;
	private Minecraft mc;
	private Config config;
	private GuiButton playerNamesButton;
	private GuiButton playerInfoButton;
	private GuiButton playerNamePosButton;
	private GuiSlider pingVolumeSlider;
	
	public GuiPlayerOptions(GuiScreen parent) {
		this.parent = parent;
		mc = Minecraft.getMinecraft();
		config = RavenRadar.instance.getConfig();
	}
	
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(playerNamesButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 - 20, "Player Names: " + (config.showPlayerNames() ? "Enabled" : "Disabled")));
		this.buttonList.add(playerInfoButton = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 2, "Position Info: " + (config.showExtraPlayerInfo() ? "Enabled" : "Disabled")));
		this.buttonList.add(playerNamePosButton = new GuiButton(3, this.width / 2 - 100, this.height / 4 + 24, "Player Name Location: " + config.getNameLocation().toString()));
		this.buttonList.add(pingVolumeSlider = new GuiSlider(4, this.width / 2 - 100, this.height / 4 + 46, 1.0F, 0.0F, "Ping Volume", config.getPingVolume()));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 68, "Done"));
	}
	
	public void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id == 0) {
				config.setPlayerNames(!config.showPlayerNames());
			}
			if(button.id == 1) {
				config.setExtraPlayerInfo(!config.showExtraPlayerInfo());
			}
			if(button.id == 2) {
				mc.displayGuiScreen(parent);
			}
			if(button.id == 3) {
				config.switchNameLocation();
			}
			RavenRadar.instance.saveConfig();
		}
	}
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void updateScreen() {
		playerNamesButton.displayString = "Player Names: " + (config.showPlayerNames() ? "Enabled" : "Disabled");
		playerInfoButton.displayString = "Position Info: " + (config.showExtraPlayerInfo() ? "Enabled" : "Disabled");
		playerNamePosButton.displayString = "Player Name Location: " + config.getNameLocation().toString();
		if(pingVolumeSlider.getCurrentValue() == 0.0F) {
			pingVolumeSlider.setDisplayString("off");
		} else {
			pingVolumeSlider.updateDisplayString();
		}
		config.setPingVolume(pingVolumeSlider.getCurrentValue());
		RavenRadar.instance.saveConfig();
	}
	
	public void drawScreen(int i, int j, float k) {
		drawDefaultBackground();
		mc.fontRenderer.drawString("Player names:", this.width / 2 - 50, this.height / 4 - 20, Color.WHITE.getRGB());
		drawCenteredString(this.fontRenderer, "Player Options", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());
		super.drawScreen(i, j, k);
	}
}
