package cubyz.gui;

import cubyz.gui.element.*;
import cubyz.gui.text.Text;
import cubyz.utils.datastructures.Registry;
import cubyz.utils.json.*;
import cubyz.utils.log.Log;

public class ComponentRegistry {
	//List of all Components
	public static final Registry<Component> ComponentList = new Registry<Component>(new Button(),new Text(),new GuiWindow(),new List());	
	
	public static Component createByJson(JsonObject jsonObject,Component parent) {
		Component component = (Component)ComponentList.getById(jsonObject.getString("type", "cubyz:none"));
		if(component == null) {
			Log.severe("Cubyz:unkown Gui-Type:"+jsonObject.toString()+" created Button instead");
			component = new Button();
		}
		try {
			Component c = component.getClass().getConstructor().newInstance();
			c.create(jsonObject,parent);
			return c;
		} catch (Exception e) {
			Log.severe(e);
		}
		return null;
	}
}
