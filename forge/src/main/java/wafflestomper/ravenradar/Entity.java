package wafflestomper.ravenradar;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;

public class Entity {

	private final String className;
	private boolean enabled = true;
	
	public Entity(Class entityClass) {
		this.className = entityClass.getName();
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Class getEntityClass() {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Entity.class;
	}
	
	public String getName() {
		return className;
	}
	
	public String getEntityName() {
		String[] className = this.className.split("\\.");
		return className[className.length - 1].substring(6);
	}

	public ResourceLocation getResource() {
		return new ResourceLocation("ravenradar", "icons/" + getEntityName().toLowerCase() + ".png");
	}
}
