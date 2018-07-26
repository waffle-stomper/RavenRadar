package wafflestomper.ravenradar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import wafflestomper.ravenradar.Config.NameLocation;

public class RenderHandler extends Gui {

	private Config config = RavenRadar.instance.getConfig();
	private Minecraft mc = Minecraft.getMinecraft();
	private Color radarColor;
	private List entityList;
	private float radarScale = 1.0f;
	private float iconScale = 1.0f;
	private float iconSpacing = 1.0f;
	ArrayList<String> inRangePlayers;
	//private String currentWorld = "";
	//TODO: Support much larger radars for high resolution settings (GUI scale = small)
	private static final double radarMaxRadius = 63.0D;
	private static final double radarDiagSectorCoOrd = Math.sin(Math.toRadians(45))*radarMaxRadius;
	
	public RenderHandler() {
		inRangePlayers = new ArrayList<String>();
	}
	
	@SubscribeEvent
	public void renderRadar(RenderGameOverlayEvent event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS){
			return;
		}
		if(config.isEnabled()) {
			drawRadar();
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.START && mc.world != null) {
			// Make sound when new players come into range
			entityList = mc.world.loadedEntityList;
			ArrayList<String> newInRangePlayers = new ArrayList();
			for(Object o : entityList) {
				if(o instanceof EntityOtherPlayerMP) {
					newInRangePlayers.add(((EntityOtherPlayerMP)o).getName());
				}
			}
			ArrayList<String> temp = (ArrayList)newInRangePlayers.clone();
			newInRangePlayers.removeAll(inRangePlayers);
			for(String name : newInRangePlayers) {
				mc.player.playSound(new SoundEvent(new ResourceLocation("block.note.pling")), config.getPingVolume(), 1.0F);
			}
			inRangePlayers = temp;
		}
	}
	
	@SubscribeEvent
	public void renderWaypoints(RenderWorldLastEvent event) {
		if(RavenRadar.instance.getWaypointSave() == null) {
			return;
		}
		if(config.shouldRenderWaypoints()) {
			for(Waypoint point : RavenRadar.instance.getWaypointSave().getWaypoints()) {
				if(point.getDimension() == mc.world.provider.getDimension() && point.isEnabled()) {
					renderWaypoint(point, event);
				}
			}
		}
	}
	
	private void drawRadar() {
		this.radarColor = config.getRadarColor();
		this.radarScale = config.getRadarScale();
		this.iconScale = config.getIconScale();
		this.iconSpacing = config.geticonSpacing();
		ScaledResolution res = new ScaledResolution(mc);
		int width = res.getScaledWidth();
		int height = res.getScaledHeight();
		GL11.glPushMatrix();
		// Set up for top/left
		double xOffset = config.getRadarOffsetX() + (radarMaxRadius * this.radarScale) + 0.5;
		double yOffset = config.getRadarOffsetY() + (radarMaxRadius * this.radarScale) + 0.5;
		// Handle bottom/right
		if (!config.getRadarAlignLeft()){
			xOffset = width - (radarMaxRadius * this.radarScale) - config.getRadarOffsetX() - 0.5;
		}
		if (!config.getRadarAlignTop()){
			yOffset = height - (radarMaxRadius * this.radarScale) - config.getRadarOffsetY() - 0.5;
			if (config.shouldRenderCoordinates()){
				yOffset -= 10;
			}
		}
		GL11.glTranslated(xOffset, yOffset, 0.0F);
		GL11.glScalef(1.0F, 1.0F, 1.0F);
		if(config.shouldRenderCoordinates()) {
			String coords = "(" + (int) mc.player.posX + "," + (int) mc.player.posY + "," + (int) mc.player.posZ + ")";
			//TODO: Figure out why the colors are flipped here: It seems to happen when there isn't a player/item in view
			//Co-ordinates are disabled until I figure this out
			mc.fontRenderer.drawString(coords, -(mc.fontRenderer.getStringWidth(coords) / 2), (int) ((radarMaxRadius+2) * radarScale), 0);
		}
		GL11.glScalef(this.radarScale, this.radarScale, this.radarScale);
		GL11.glRotatef(-mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
		drawCircle(0, 0, radarMaxRadius, radarColor, true); //fill
		GL11.glLineWidth(2.0F);
		drawCircle(0, 0, radarMaxRadius, radarColor, false); //border
		GL11.glLineWidth(1.0F);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBegin(1);
		GL11.glColor4f(radarColor.getRed() / 255.0F, radarColor.getGreen() / 255.0F, radarColor.getBlue() / 255.0F, config.getRadarOpacity() + 0.5F);
		// Sector lines radiating out from the center
		GL11.glVertex2d(0.0D, -radarMaxRadius);
		GL11.glVertex2d(0.0D, radarMaxRadius);
		GL11.glVertex2d(-radarMaxRadius, 0.0D);
		GL11.glVertex2d(radarMaxRadius, 0.0D);
		GL11.glVertex2d(-radarDiagSectorCoOrd, -radarDiagSectorCoOrd);
		GL11.glVertex2d(radarDiagSectorCoOrd, radarDiagSectorCoOrd);
		GL11.glVertex2d(-radarDiagSectorCoOrd, radarDiagSectorCoOrd);
		GL11.glVertex2d(radarDiagSectorCoOrd, -radarDiagSectorCoOrd);
		GL11.glEnd();
		GL11.glRotatef(mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
		drawTriangle(0, 0, Color.WHITE);
		GL11.glRotatef(-mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		drawRadarIcons();
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
	
	private void drawCircle(int x, int y, double radius, Color c, boolean filled) {
		GL11.glEnable(3042);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, filled ? config.getRadarOpacity() : config.getRadarOpacity() + 0.5F);
		GL11.glBegin(filled ? 6 : 2);
		for (int i = 0; i <= 360; i++) {
			double x2 = Math.sin(i * Math.PI / 180.0D) * radius;
			double y2 = Math.cos(i * Math.PI / 180.0D) * radius;
			GL11.glVertex2d(x + x2, y + y2);
		}
		GL11.glEnd();
		GL11.glDisable(2848);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(3042);
	}
	
	private void drawTriangle(int x, int y, Color c) {
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glColor4f(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, config.getRadarOpacity() + 0.5F);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glBegin(4);
		GL11.glVertex2d(x, y + 3);
		GL11.glVertex2d(x + 3, y - 3);
		GL11.glVertex2d(x - 3, y - 3);
		GL11.glEnd();
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glRotatef(-180.0F, 0.0F, 0.0F, 1.0F);
	}
	
	private void drawRadarIcons() {
		if(entityList == null) {
			return;
		}
		float playerPosX = (float)mc.player.posX;
		float playerPosZ = (float)mc.player.posZ;
		for(Object o : entityList) {
			Entity e = (Entity) o;
			if(e != mc.player) {
				int entityPosX = (int) e.posX;
				int entityPosZ = (int) e.posZ;
				float displayPosX = (playerPosX-entityPosX) * this.iconSpacing;
				float displayPosZ = (playerPosZ-entityPosZ) * this.iconSpacing;
				if(e instanceof EntityItem && config.isRender(EntityItem.class)) {
					renderItemIcon(displayPosX, displayPosZ, ((EntityItem)e).getItem());
				}
				else if(e instanceof EntityOtherPlayerMP && config.isRender(EntityPlayer.class)) {
					try {
						renderPlayerHeadIcon(displayPosX, displayPosZ, (EntityOtherPlayerMP)e);
					} catch (Exception ex) {
						// TODO: Add a red box where the player's head should be
						// I don't want to print one or more stacktraces every tick if something's going wrong
					}
				} 
				else if(e instanceof EntityMinecart && config.isRender(EntityMinecart.class)) {
					ItemStack cart = new ItemStack(Items.MINECART);
					renderItemIcon(displayPosX, displayPosZ, cart);
				} 
				else if(config.isRender(o.getClass())) {
					renderIcon(displayPosX, displayPosZ, config.getMob(e.getClass()).getResource());
				}
			}
		}
	}
	
	private void renderItemIcon(float x, float y, ItemStack item) {
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glTranslatef(x +1, y +1, 0.0F);
		GL11.glScalef(this.iconScale, this.iconScale, this.iconScale);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, config.getIconOpacity());
		GL11.glRotatef(mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting(); // <-------------------------------AWW
		mc.getRenderItem().renderItemAndEffectIntoGUI(item, -8, -8);
		RenderHelper.disableStandardItemLighting(); //   <-------------------------------YISS
		GL11.glScalef(1/this.iconScale, 1/this.iconScale, 1/this.iconScale);
		GL11.glTranslatef(-x -1, -y -1, 0.0F);
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		GL11.glDisable(2896);
		GL11.glPopMatrix();
	}
	
	
    /**
     * Draws a textured rectangle at the given Z level
     */
    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight, double zLevel)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)x, (double)(y + height), zLevel)
        				.tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), zLevel)
        				.tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)y, zLevel)
        				.tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex();
        vertexbuffer.pos((double)x, (double)y, zLevel)
        				.tex((double)(u * f), (double)(v * f1)).endVertex(); 
        tessellator.draw();
    }
	
	private void renderPlayerHeadIcon(float x, float y, EntityOtherPlayerMP player) throws Exception {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, config.getIconOpacity());
		GL11.glEnable(3042);
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glTranslatef(x + 1, y + 1, 0.0F);
		GL11.glRotatef(mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
		GL11.glScalef(this.iconScale, this.iconScale, this.iconScale);
		mc.getTextureManager().bindTexture(new ResourceLocation("ravenradar", "icons/player.png"));
		drawModalRectWithCustomSizedTexture(-8, -8, 0, 0, 16, 16, 16, 16, 1000d);
		GL11.glScalef(1/this.iconScale, 1/this.iconScale, 1/this.iconScale);
		GL11.glTranslatef(-x -1, -y -1, 0.0F);
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		GL11.glDisable(2896);
		GL11.glDisable(3042);
		GL11.glPopMatrix();
		if(!config.showPlayerNames()) {
			return;
		}
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glTranslatef(x, y, 0.0F);
		GL11.glRotatef(mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-x, -y, 0.0F);
		String playerName = player.getName();
		if(config.showExtraPlayerInfo()) {
			playerName += " (" + (int) mc.player.getDistance(player) + "m)(Y" + (int) player.posY + ")";
		}
		
		int yOffset = config.getNameLocation() == NameLocation.below ? 10 : -10;
		drawCenteredString(mc.fontRenderer, playerName, (int)x + 8, (int)y + yOffset, Color.WHITE.getRGB());
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		GL11.glPopMatrix();
	}
	
	private void renderIcon(float x, float y, ResourceLocation resource) {
		mc.getTextureManager().bindTexture(resource);		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, config.getIconOpacity());
		GL11.glEnable(3042);
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glTranslatef(x + 1, y + 1, 0.0F);
		GL11.glScalef(this.iconScale, this.iconScale, this.iconScale);
		GL11.glRotatef(mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
		drawModalRectWithCustomSizedTexture(-8, -8, 0, 0, 16, 16, 16, 16, 0);
		GL11.glScalef(1/this.iconScale, 1/this.iconScale, 1/this.iconScale);
		GL11.glTranslatef(-x -1, -y -1, 0.0F);
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		GL11.glDisable(2896);
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}
	
	/**
	 * Renders the waypoints in 3D space
	 */
	private void renderWaypoint(Waypoint point, RenderWorldLastEvent event) {
		String name = point.getName();
		Color c = point.getColor();
		float partialTickTime = event.getPartialTicks();
		double distance = point.getDistance(mc);
		int maxView = mc.gameSettings.renderDistanceChunks * 22;
		if(distance <= config.getMaxWaypointDistance() || config.getMaxWaypointDistance() < 0) {
			FontRenderer fr = mc.fontRenderer;
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder vb = tess.getBuffer();
			RenderManager rm = mc.getRenderManager();
			
			float playerX = (float) (mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * partialTickTime);
			float playerY = (float) (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * partialTickTime);
			float playerZ = (float) (mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * partialTickTime);
			
			float displayX = (float)point.getX() - playerX;
			float displayY = (float)point.getY() + 1.3f - playerY;
			float displayZ = (float)point.getZ() - playerZ;
			
			if(distance > maxView) {
				float slope = displayZ / displayX;
				displayX = Math.abs((float)Math.sqrt(Math.pow(maxView, 2) / (1 + Math.pow(slope, 2)))) * (point.getX() < 0 ? -1 : 1);
				displayZ = slope * displayX;
			}
			
			int width = fr.getStringWidth(name);
			int height = 10;
			int stringMiddle = width / 2;
			
			//TODO: convert this hardcoded old scale factor into something configurable
			float scale = 0.45F * 10f/120f;
			
	    	// Waypoint background pane(pain) code from WorldBorderViewer
			GlStateManager.pushMatrix();
	        GlStateManager.enableBlend();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
	        GlStateManager.color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, config.getWaypointOpcaity());
	        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	        GlStateManager.translate(displayX, displayY, displayZ);
	        GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-scale, -scale, scale);
	        GlStateManager.scale(1.0D, 1.0D, 1.0D);
	        float f3 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
	        float f7 = 0.0F;
	        
	        vb.pos(-stringMiddle - 1, -1, 0).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex(); //Origin
	        vb.pos(-stringMiddle - 1, 1 + height,0).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex(); //Up
	        vb.pos(stringMiddle + 1,  1 + height, 0).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex(); //Up and out
	        vb.pos(stringMiddle + 1, -1,0).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex(); //Out
	        
	        tess.draw();
	        
	        GlStateManager.disableBlend();
	        GlStateManager.enableTexture2D();
	        GlStateManager.depthMask(true);
	        
	        fr.drawString(name, -width / 2, 1, Color.WHITE.getRGB());
	        GlStateManager.popMatrix();
		}
	}
}
