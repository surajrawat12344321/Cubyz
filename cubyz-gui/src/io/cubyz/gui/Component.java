package io.cubyz.gui;

import java.util.ArrayList;

import io.cubyz.utils.datastructures.RegistryElement;

public class Component implements RegistryElement{
	ArrayList<Component> children = new ArrayList<Component>();
	
	public float left,top,width,height;
	
	@Override
	public String getID(){
		return "";
	}
	
	public Component(float left,float top,float width,float height){
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	public void draw(Scene scene) {
		
	}
	
}
