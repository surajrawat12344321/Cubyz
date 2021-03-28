package io.cubyz.gui;

import java.util.ArrayList;

public class Container extends Component {
	protected ArrayList<Component> childs = new ArrayList<Component>();
	
	public Container(int x, int y, int width, int height) {
		super(x, y, width, height, (byte)0);
	}

	@Override
	public void render(long nvg, Container parent, int x, int y) {
		for(Component comp : childs) {
			comp.render(nvg, this);
		}
	}
	
	public void addComponent(Component comp) {
		childs.add(comp);
	}
	
	public void removeComponent(Component comp) {
		childs.remove(comp);
	}
	
	public void clear() {
		childs.clear();
	}
}
