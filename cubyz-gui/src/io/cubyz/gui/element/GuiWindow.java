package io.cubyz.gui.element;

import org.joml.Vector2d;

import com.google.gson.JsonObject;

import io.cubyz.gui.Component;
import io.cubyz.gui.Design;
import io.cubyz.gui.rendering.Input;
import io.cubyz.gui.rendering.Keys;
import io.cubyz.gui.text.Text;

public class GuiWindow extends Component {

	@Override
	public String getID() {
		return "cubyz:window";
	}

	// state of the button
	public boolean pressed;
	public boolean hovered;
	
	//coords when the mouse first touched the screen
	private Vector2d holdPositionMouse = new Vector2d();
	private Vector2d holdPositionWindow = new Vector2d();
	private boolean moving = false;
	
	//children
	Button close;
	Button background;
	
	@Override
	public void create(JsonObject object, Component parent) {
		super.create(object, parent);
		
		//background
		background = new Button();
		background.width.setAsPercentage(1f,width);
		background.height.setAsPercentage(1f,height);
		
		background.color_hovered = background.color_pressed;
		background.color_std = background.color_pressed;
		background.color_std[0] = 160;
		background.color_std[1] = 160;
		background.color_std[2] = 160;
		background.color_std[3] = 225;
		background.shadowWidth = 5f;
		background.shadowHeight = 5f;
		add(background);
		
		
		//title
		Text title = new Text();
		title.height.setAsValue(60);
		title.top.setAsValue(10);
		title.left.setAsPercentage(0.5f, width);
		title.originLeft.setAsPercentage(0.5f, title.width);
		//title.setText("ண் U̵̞̬̲͇̝ͧ͗̏͆̅ͫͫṋ͉̭̻͋͋ͫ͗̏ͧ̓î͇͎͇̠̂̊͒̌̐͆c̨͈̮̝͇̰̓̐͑̚o̫̪̙̍ͣ̍ͤ̋ͧ̈́d̵̪͖̩ͯͧ̃̔ͮ̚͡ẹ̡̖͚̦̿̀͘͠ wo⃗⃗rks!ண்a اختبار تقديم النص aa ab ac");
		title.setText("unnamed window");
		add(title);
		
		
		//close
		close = new Button();
		close.width.setAsValue(60);
		close.height.setAsValue(60);
		close.color_hovered = new float[]{255,0,0,255};
		close.color_std = new float[]{0,255,0,255};
		close.color_pressed = new float[]{0,0,255,255};
		
		
		close.originLeft.setAsPercentage(1.f, close.width);
		close.left.setAsPercentage(1.f, width);
		//close.setTexture("assets/cubyz/textures/crosshair.png");
		//close.shadowIntensity = 0;
		close.shadowWidth = 5f;
		close.shadowHeight = 5f;
		add(close);
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		
		return object;
	}
	@Override
	public void update(Design design, float parentalOffsetX, float parentalOffsetY) {
		super.update(design, parentalOffsetX, parentalOffsetY);
		
		
		Vector2d mousepos = Input.getMousePosition(design);
		mousepos.x -= parentalOffsetX;
		mousepos.y -= parentalOffsetY;

		hovered = background.hovered;
		if(hovered)
			design.hovered = this;
		
		boolean old_pressed = pressed;
		pressed = hovered ? Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY) : false;
		
		
		if(pressed&&!old_pressed&&!moving) {
			holdPositionMouse.x = mousepos.x;
			holdPositionMouse.y = mousepos.y;
			holdPositionWindow.x = left.getAsValue();
			holdPositionWindow.y = top.getAsValue();
			moving = true;
			design.pushToTop(this);
		}
		if(moving)
			moving = Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY);
		if(moving) {
			double x = mousepos.x - holdPositionMouse.x;
			double y = mousepos.y - holdPositionMouse.y;
			
			left.setAsValue((float)holdPositionWindow.x+(float)x);
			top.setAsValue((float)holdPositionWindow.y+(float)y);
		}
		
		if(close.release)
			scene.currentDesign.remove(this);
		
		if (!pressed && old_pressed && scene != null && hovered)
			scene.triggerEvent(this, "button_release");
	}

	@Override
	public void draw(Design design, float parentalOffsetX, float parentalOffsetY) {
		super.draw(design, parentalOffsetX, parentalOffsetY);
	}
}
