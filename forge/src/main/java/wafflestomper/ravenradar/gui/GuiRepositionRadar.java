package wafflestomper.ravenradar.gui;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import wafflestomper.ravenradar.RavenRadar;
import wafflestomper.ravenradar.Config;

public class GuiRepositionRadar extends GuiScreen {
	
	private GuiScreen parentScreen;
	private Config config;
	
	private static final int BTN_HORIZONTAL_ALIGN = 1;
	private static final int BTN_VERTICAL_ALIGN = 2;
	private static final int BTN_SNAP_TOP_LEFT = 3;
	private static final int BTN_SNAP_TOP_RIGHT = 4;
	private static final int BTN_SNAP_BOTTOM_LEFT = 5;
	private static final int BTN_SNAP_BOTTOM_RIGHT = 6;
	private static final int BTN_DONE = 7;
	
	private GuiButton btnHorizontalAlign;
	private GuiButton btnVerticalAlign;
	
	
	public GuiRepositionRadar(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		config = RavenRadar.instance.getConfig();
	}
	
	
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(this.btnHorizontalAlign = new GuiButton(BTN_HORIZONTAL_ALIGN, this.width/2 - 101, 66, 202, 20, ""));
		this.buttonList.add(this.btnVerticalAlign = new GuiButton(BTN_VERTICAL_ALIGN, this.width/2 - 101, 88, 202, 20, ""));
		this.buttonList.add(new GuiButton(BTN_SNAP_TOP_RIGHT, this.width/2 + 1, 110, 100, 20, "Snap top right"));
		this.buttonList.add(new GuiButton(BTN_SNAP_TOP_LEFT, this.width/2 - 101, 110, 100, 20, "Snap top left"));
		this.buttonList.add(new GuiButton(BTN_SNAP_BOTTOM_LEFT, this.width/2 - 101, 132, 100, 20, "Snap bottom left"));
		this.buttonList.add(new GuiButton(BTN_SNAP_BOTTOM_RIGHT, this.width/2 + 1, 132, 100, 20, "Snap bottom right"));
		this.buttonList.add(new GuiButton(BTN_DONE, this.width/2 - 101, 160, 202, 20, "Done"));
		updateButtons();
	}
	
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		RavenRadar.instance.saveConfig();
	}
	
	
	private void updateButtons(){
		if (config.getRadarAlignLeft()){
			this.btnHorizontalAlign.displayString = "H Align: Left";
		}
		else{
			this.btnHorizontalAlign.displayString = "H Align: Right";
		}
		if (config.getRadarAlignTop()){
			this.btnVerticalAlign.displayString = "V Align: Top";
		}
		else{
			this.btnVerticalAlign.displayString = "V Align: Bottom";
		}
	}
	
	
	public void actionPerformed(GuiButton button) {
		if(!button.enabled) {
			return;
		}
		switch (button.id){
			case BTN_HORIZONTAL_ALIGN:
				config.setRadarAlignLeft(!config.getRadarAlignLeft());
				break;
			case BTN_VERTICAL_ALIGN:
				config.setRadarAlignTop(!config.getRadarAlignTop());
				break;
			case BTN_SNAP_TOP_LEFT:
				config.setRadarOffsetX(0);
				config.setRadarOffsetY(0);
				config.setRadarAlignLeft(true);
				config.setRadarAlignTop(true);
				break;
			case BTN_SNAP_TOP_RIGHT:
				config.setRadarOffsetX(0);
				config.setRadarOffsetY(0);
				config.setRadarAlignLeft(false);
				config.setRadarAlignTop(true);
				break;
			case BTN_SNAP_BOTTOM_LEFT:
				config.setRadarOffsetX(0);
				config.setRadarOffsetY(0);
				config.setRadarAlignLeft(true);
				config.setRadarAlignTop(false);
				break;
			case BTN_SNAP_BOTTOM_RIGHT:
				config.setRadarOffsetX(0);
				config.setRadarOffsetY(0);
				config.setRadarAlignLeft(false);
				config.setRadarAlignTop(false);
				break;
		}
		if(button.id == BTN_DONE) {
			mc.displayGuiScreen(parentScreen);
		}
		RavenRadar.instance.saveConfig();
		updateButtons();
	}
	

	public void updateScreen() {
		int xChange = 0;
		int yChange = 0;
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			if (config.getRadarAlignLeft()){
				xChange--;
			}
			else{
				xChange++;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			if (config.getRadarAlignLeft()){
				xChange++;
			}
			else{
				xChange--;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			if (config.getRadarAlignTop()){
				yChange--;
			}
			else{
				yChange++;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if (config.getRadarAlignTop()){
				yChange++;
			}
			else{
				yChange--;
			}
		}
		int oldX = config.getRadarOffsetX();
		int oldY = config.getRadarOffsetY();
		int newX = oldX+xChange;
		int newY = oldY+yChange;
		if (newX >= 0 && newX != oldX){
			config.setRadarOffsetX(newX);
		}
		if (newY >= 0 && newY != oldY){
			config.setRadarOffsetY(newY);
		}
	}
	
	
	public void drawScreen(int i, int j, float k) {
		drawCenteredString(mc.fontRendererObj, "Use arrow keys to reposition radar", this.width / 2, 50, Color.WHITE.getRGB());
		super.drawScreen(i, j, k);
	}

}
