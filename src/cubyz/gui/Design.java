package cubyz.gui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

import java.io.FileWriter;

import org.lwjgl.opengl.GL30;

import cubyz.gui.rendering.Input;
import cubyz.gui.rendering.Keys;
import cubyz.gui.text.CubyzGraphics2D;
import cubyz.utils.json.*;
import cubyz.utils.log.Log;

/**
	{@code Scene} design for a specific screen ratio.
*/
public class Design extends Component{
	
	public Component hovered = null;
	
	@Override
	public String getID() {
		return "Cubyz:Design";
	}
	
	//methods
	public Design(String name,int height,int width) {
		this.name = new String(name);
		this.width.setAsValue(width);
		this.height.setAsValue(height);
		this.parent = this;
		
	}
	
	public Design(String path) {
		loadFromFile(path);
	}
	
	public void loadFromFile(String path) {
		try {
			// Read JSON file
		    JsonObject obj = (JsonObject) JsonParser.parseObjectFromFile(path);
			System.out.println(obj);
			create(obj,this);
		} catch (Exception e) {
			Log.severe(e);
		}
	}

	public void saveAsFile(String path) {
		try {
			FileWriter fw = new FileWriter(path);
			fw.write(toJson().toString());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			Log.warning(e);
		}
	}
	@Override
	public void create(JsonObject design,Component parent) {
		height.setAsValue(design.getInt("height", 1920));
		width.setAsValue(design.getInt("width", 1080));
		name = design.getString("name", "");
		

		children.clear();
		JsonArray jchildren = design.getArrayNoNull("children");
		for (JsonElement jsonElement : jchildren.array) {
			if(jsonElement instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject)jsonElement;
				children.add(ComponentRegistry.createByJson(jsonObject,this));
			}
		}
	}
	@Override
	public JsonObject toJson() {
		JsonObject scene =  new JsonObject();
		scene.put("name", name);
		scene.put("height", height.toJson());
		scene.put("width", width.toJson());
		
		JsonArray jchildren = new JsonArray();
		for (Component guiElement : children) {
			jchildren.add(guiElement.toJson());
		}
		scene.put("children", jchildren);
		
		return scene;
	}
	public void update() {
		//deselect everything if someone pressed somewhere else
		if(Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY))
			Input.selectedText = null;
		hovered = null;
		CubyzGraphics2D.instance.design = this;
		
		for (int i = children.size()-1; i>=0;i--) {
			Component component = children.get(i);
			component.update(this,
					0+left.getAsValue()-component.originLeft.getAsValue(),
					0+top.getAsValue()-component.originTop.getAsValue());
		}
	}
	public void draw() {
		GL30.glDisable(GL_DEPTH_TEST);
		CubyzGraphics2D.instance.design = this;
		super.draw(this, 0, 0);
		GL30.glEnable(GL_DEPTH_TEST);
	}
	public void pushToTop(Component component) {
		if(component==this)
			return;
		children.remove(component);
		children.add(component);
	}
	
	public void setScene(Scene scene) {
		Log.warning("A scene is a scene.You can't set the scene of a scene.");
	}
	/**
	  @return ratio of width/height
	*/
	public float ratio() {
		return (float)width.getAsValue()/height.getAsValue();
	}
	
	
}
