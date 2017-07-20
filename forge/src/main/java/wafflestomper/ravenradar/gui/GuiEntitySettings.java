package wafflestomper.ravenradar.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNoCallback;

import wafflestomper.ravenradar.RavenRadar;
import wafflestomper.ravenradar.Config;
import wafflestomper.ravenradar.Entity;

public class GuiEntitySettings extends GuiScreen implements GuiYesNoCallback {

	private final GuiScreen parent;
	private final ArrayList<Entity> entityList;
	private int selected = -1;
	private GuiSlider opacitySlider;
	private GuiButton enableButton;
	private GuiButton disableButton;
	private EntityList entityListContainer;
	
	public GuiEntitySettings(GuiScreen parent) {
		this.parent = parent;
		this.entityList = RavenRadar.instance.getConfig().getEntities();
	}
	
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(opacitySlider = new GuiSlider(3, this.width / 2 -100, this.height - 63, 1.0F, 0.0F, "Icon Opacity", RavenRadar.instance.getConfig().getIconOpacity()));
		this.buttonList.add(enableButton = new GuiButton(0, this.width / 2 - 100, this.height - 42, 99, 20, "Enable"));
		this.buttonList.add(disableButton = new GuiButton(1, this.width / 2 + 1, this.height - 42, 99, 20, "Disable"));
		this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height - 21, "Done"));
		this.entityListContainer = new EntityList(this.mc);
		this.entityListContainer.registerScrollButtons(4, 5);
	}
	
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.entityListContainer.handleMouseInput();
	}
	
	public void updateScreen() {
		disableButton.enabled = selected >= 0 && selected < entityList.size();
		enableButton.enabled = selected >= 0 && selected < entityList.size();
		Config config = RavenRadar.instance.getConfig();
		config.setIconOpacity(opacitySlider.getCurrentValue());
		//RavenRadar.instance.saveConfig();
		opacitySlider.updateDisplayString();
	}
	
	private void enableOrDisableSelectedEntity(boolean enabled) {
		Class selectedEntityClass = entityList.get(selected).getEntityClass();
		RavenRadar.instance.getConfig().setRender(selectedEntityClass, enabled);
		RavenRadar.instance.saveConfig();
	}
	
	protected void actionPerformed(GuiButton button) throws IOException	 {
		if(button.enabled) {
			if(button.id == 0) {
				enableOrDisableSelectedEntity(true);
			}
			if(button.id == 1) {
				enableOrDisableSelectedEntity(false);
			}
			if(button.id == 100) {
				mc.displayGuiScreen(parent);
			}
		}
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.entityListContainer.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, "Edit enabled entities", this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
	
	class EntityList extends GuiSlot {
		
		public EntityList(Minecraft mc) {
			super(mc, GuiEntitySettings.this.width, GuiEntitySettings.this.height, 32, GuiEntitySettings.this.height - 64, 36);
		}
		
		protected int getSize() {
			return GuiEntitySettings.this.entityList.size();
		}
		
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			GuiEntitySettings.this.selected = slotIndex;
			boolean isValidSlot = slotIndex >= 0 && slotIndex < getSize();
			GuiEntitySettings.this.enableButton.enabled = isValidSlot;
			GuiEntitySettings.this.disableButton.enabled = isValidSlot;
		}
		
		protected boolean isSelected(int slotIndex) {
			return slotIndex == GuiEntitySettings.this.selected;
		}
		
		protected int getContentHeight() {
			return getSize() * 36;
		}
		
		protected void drawBackground() {
			GuiEntitySettings.this.drawDefaultBackground();
		}
		
		protected void drawSlot(int entryId, int par2, int par3, int par4, int par5, int par6, float unused) {
			Entity entity = GuiEntitySettings.this.entityList.get(entryId);
			GuiEntitySettings.this.drawString(mc.fontRenderer, entity.getEntityName(), par2 + 1, par3 + 1, Color.WHITE.getRGB());
			GuiEntitySettings.this.drawString(mc.fontRenderer, entity.isEnabled() ? "Enabled" : "Disabled", par2 + 215 - mc.fontRenderer.getStringWidth("Disabled"), par3 + 1, entity.isEnabled() ? Color.GREEN.getRGB() : Color.RED.getRGB());
		}
	}
}
