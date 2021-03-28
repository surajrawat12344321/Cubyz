package io.cubyz.server.entity;

import org.joml.Vector3f;

public class Entity {
	int id = -1;
	
	public Entity(){
		EntityManager.register(this);	
	}
	public void setPosition(Vector3f position) {
		EntityManager.setPosition(this, position.x,position.y,position.z);		
	}
	public void setPosition(float x,float y,float z) {
		EntityManager.setPosition(this, x,y,z);		
	}
	public void setVelocity(Vector3f velocity) {
		EntityManager.setVelocity(this, velocity.x,velocity.y,velocity.z);		
	}
	public void setVelocity(float x,float y,float z) {
		EntityManager.setVelocity(this, x,y,z);		
	}
	public void setWeight(float weight) {
		EntityManager.setWeight(this, weight);		
	}
	
	public Vector3f getPosition() {
		return EntityManager.getPosition(this);		
	}
	public Vector3f getVelocity() {
		return EntityManager.getVelocity(this);		
	}
	public float getweight() {
		return EntityManager.getWeight(this);		
	}
	
	
	public boolean isAlive() {
		return id==-1;
	}
	public void remove() {
		EntityManager.remove(this);
	}
	
}
