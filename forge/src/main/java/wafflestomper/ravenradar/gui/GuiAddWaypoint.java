package wafflestomper.ravenradar.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import wafflestomper.ravenradar.RavenRadar;
import wafflestomper.ravenradar.Waypoint;
import wafflestomper.ravenradar.WaypointSave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiAddWaypoint extends GuiScreen {

	private GuiScreen parent;
	private Minecraft mc;
	private WaypointSave waypoints;
	private GuiTextField waypointNameField;
	private GuiTextField waypointXField;
	private GuiTextField waypointYField;
	private GuiTextField waypointZField;
	private GuiButton saveButton;
	private GuiSlider redSlider;
	private GuiSlider greenSlider;
	private GuiSlider blueSlider;
	private ArrayList<GuiTextField> textFieldList;
	
	public GuiAddWaypoint(GuiScreen parent) {
		this.parent = parent;
		mc = Minecraft.getMinecraft();
		waypoints = RavenRadar.instance.getWaypointSave();
		textFieldList = new ArrayList();
	}
	
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		textFieldList.add(waypointNameField = new GuiTextField(1, fontRendererObj, this.width / 2 - 100, this.height / 4 - 16, 200, 20));
		textFieldList.add(waypointXField = new GuiTextField(2, fontRendererObj, this.width / 2 - 100, this.height / 4 + 8, 64, 20));
		textFieldList.add(waypointYField = new GuiTextField(2, fontRendererObj, this.width / 2 - 32, this.height / 4 + 8, 64, 20));
		textFieldList.add(waypointZField = new GuiTextField(2, fontRendererObj, this.width / 2 + 36, this.height / 4 + 8, 64, 20));
		waypointXField.setText("" + (int) mc.thePlayer.posX);
		waypointYField.setText("" + (int) mc.thePlayer.posY);
		waypointZField.setText("" + (int) mc.thePlayer.posZ);
		Random random = new Random();
		this.buttonList.add(redSlider = new GuiSlider(0, this.width / 2 - 100, this.height / 4 + 32, 1.0F, 0.0F, "Red", random.nextFloat()));
		this.buttonList.add(greenSlider = new GuiSlider(0, this.width / 2 - 100, this.height / 4 + 56, 1.0F, 0.0F, "Green", random.nextFloat()));
		this.buttonList.add(blueSlider = new GuiSlider(0, this.width / 2 - 100, this.height / 4 + 80, 1.0F, 0.0F, "Blue", random.nextFloat()));
		this.buttonList.add(saveButton = new GuiButton(100, this.width / 2 - 100, this.height / 4 + 152, "Save"));
		waypointNameField.setFocused(true);
	}
	
	public void updateScreen() {
		redSlider.updateDisplayString();
		greenSlider.updateDisplayString();
		blueSlider.updateDisplayString();
		boolean validName = waypointNameField.getText().length() > 0;
		boolean validCoords = true;
		try {
			int x = Integer.parseInt(waypointXField.getText());
			int y = Integer.parseInt(waypointYField.getText());
			int z = Integer.parseInt(waypointZField.getText());
		} catch (Exception e) {
			validCoords = false;
		}
		if(!validName || !validCoords) {
			saveButton.enabled = false;
		} else {
			saveButton.enabled = true;
		}
	}
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void mouseClicked(int x, int y, int mouseButton) {
		try {
			super.mouseClicked(x, y, mouseButton);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(GuiTextField field : textFieldList) {
			field.mouseClicked(x, y, mouseButton);
		}
	}
	
	public void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id == 100) {
				try {
					Color color = new Color(redSlider.getCurrentValue(), greenSlider.getCurrentValue(), blueSlider.getCurrentValue());
					RavenRadar.instance.getWaypointSave().addWaypoint(new Waypoint(Integer.parseInt(waypointXField.getText().trim()), Integer.parseInt(waypointYField.getText().trim()),
						Integer.parseInt(waypointZField.getText().trim()), waypointNameField.getText().trim(), color, true));
					RavenRadar.instance.saveWaypoints();
					mc.displayGuiScreen(parent);
				} catch (Exception e) {
					e.printStackTrace();
					mc.displayGuiScreen(parent);
				}
			}
		}
	}
	
	public void keyTyped(char keyChar, int keyCode) {
		for(int i = 0; i < textFieldList.size(); i++) {
			if(textFieldList.get(i).isFocused()) {
				textFieldList.get(i).textboxKeyTyped(keyChar, keyCode);
			}
		}
		if(keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(parent);
		}
	}
	
	public void drawScreen(int i, int j, float k) {
		drawDefaultBackground();
		drawCenteredString(this.fontRendererObj, "Add Waypoint", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());
		for(GuiTextField field : textFieldList) {
			field.drawTextBox();
		}
		this.drawRect(this.width / 2 - 100, this.height / 4 + 109, this.width / 2 + 100, this.height / 4 + 141, Color.BLACK.getRGB());
		this.drawRect(this.width / 2 - 99, this.height / 4 + 110, this.width / 2 + 99, this.height / 4 + 140, new Color(redSlider.getCurrentValue(), greenSlider.getCurrentValue(), blueSlider.getCurrentValue()).getRGB());
		super.drawScreen(i, j, k);
	}
}
