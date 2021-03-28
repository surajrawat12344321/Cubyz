package io.cubyz.server.entity;

import java.util.ArrayList;

import org.joml.Vector3f;

/**
 * 
 * stores Informations about enteties
 *
 */
public class EntityManager {
	
	static final int minimumExpandSize = 50; // the minimum size,the entity can grow.
	
	static int entitySize = 0;
	static int entityArrayReserve = 0;
	static public float[] array_position = new float[0];	// 3 entries per entity	x,y,z
	static public float[] array_velocity = new float[0]; 	// 3 entries per entity x,y,z
	static public float[] array_acceleration = new float[0]; 	// 3 entries per entity x,y,z
	static public float[] array_weight = new float[0];		// 1 entries per entity weight
	
	static public ArrayList<Entity> entities = new ArrayList<Entity>();
	
	
	//Expands the size of the Arrays to a minimum of size i.
	static private void extendArrayReserve(int i) {
		if(i<=entityArrayReserve)
			return;
		
		
		synchronized (entities) {
			
			int new_entityArrayReserve = entityArrayReserve+minimumExpandSize;
			
			//create a new bigger array
			float[] new_array_position = new float[new_entityArrayReserve*3];
			float[] new_array_velocity = new float[new_entityArrayReserve*3];
			float[] new_array_acceleration = new float[new_entityArrayReserve*3];
			float[] new_array_weight = new float[new_entityArrayReserve];
			

			//transfer old data to the new one
			System.arraycopy(array_position, 0, new_array_position, 0, entityArrayReserve*3);
			System.arraycopy(array_velocity, 0, new_array_velocity, 0, entityArrayReserve*3);
			System.arraycopy(array_acceleration, 0, new_array_acceleration, 0, entityArrayReserve*3);
			System.arraycopy(array_weight, 0, new_array_weight, 0, entityArrayReserve);
			
			
			
			//replace old by new 
			entityArrayReserve=new_entityArrayReserve;
			array_position = new_array_position;
			array_velocity = new_array_velocity;
			array_acceleration = new_array_acceleration;
			array_weight = new_array_weight;
			
		}
	}
	
	static void setPosition(Entity entity,float x,float y,float z) {
		array_position[entity.id*3] = x;
		array_position[entity.id*3+1] = y;
		array_position[entity.id*3+2] = z;
		
	}
	static void setVelocity(Entity entity,float x,float y,float z) {
		array_velocity[entity.id*3] = x;
		array_velocity[entity.id*3+1] = y;
		array_velocity[entity.id*3+2] = z;
	}
	static void setAcceleration(Entity entity,float x,float y,float z) {
		array_acceleration[entity.id*3] = x;
		array_acceleration[entity.id*3+1] = y;
		array_acceleration[entity.id*3+2] = z;
	}
	static void applyForce(Entity entity,float x,float y,float z) {
		array_acceleration[entity.id*3] += x;
		array_acceleration[entity.id*3+1] += y;
		array_acceleration[entity.id*3+2] += z;
	}
	
	
	
	static void setWeight(Entity entity,float weight) {
		array_weight[entity.id] = weight;
	}
	static void remove(Entity entity) {
		synchronized (entities) {
			Entity lastEntity = entities.get(entitySize-1);
			synchronized (entity) {
				synchronized (lastEntity) {
					
				//put last object in the former space of entity
				array_position[entity.id*3] = array_position[(entitySize-1)*3];
				array_position[entity.id*3+1] = array_position[(entitySize-1)*3+1];
				array_position[entity.id*3+2] = array_position[(entitySize-1)*3+2];
				
				array_velocity[entity.id*3] = array_velocity[(entitySize-1)*3];
				array_velocity[entity.id*3+1] = array_velocity[(entitySize-1)*3+1];
				array_velocity[entity.id*3+2] = array_velocity[(entitySize-1)*3+2];
				
				array_acceleration[entity.id*3]   = array_acceleration[(entitySize-1)*3];
				array_acceleration[entity.id*3+1] = array_acceleration[(entitySize-1)*3+1];
				array_acceleration[entity.id*3+2] = array_acceleration[(entitySize-1)*3+2];				
				
				array_weight[entity.id] = array_velocity[entitySize-1];
				
				entities.set(entity.id, lastEntity);
				entities.remove(entitySize);
				entitySize--;
				
				//this entity is dead.
				lastEntity.id = entity.id;
				entity.id=-1;
				}
			}
		}
	}
	
	static float getWeight(Entity entity) {
		return array_weight[entity.id];
	}
	static Vector3f getPosition(Entity entity) {
		return new Vector3f(
				array_position[entity.id*3],
				array_position[entity.id*3+1],
				array_position[entity.id*3+2]);
	}
	static Vector3f getVelocity(Entity entity) {
		return new Vector3f(
				array_velocity[entity.id*3],
				array_velocity[entity.id*3+1],
				array_velocity[entity.id*3+2]);
	}
	static Vector3f getAccerleration(Entity entity) {
		return new Vector3f(
				array_acceleration[entity.id*3],
				array_acceleration[entity.id*3+1],
				array_acceleration[entity.id*3+2]);
	}
	
	
	static void register(Entity entity) {
		synchronized (entities) {
			synchronized (entity) {
				//make space
				extendArrayReserve(entitySize+1);
				
				//add entity
				entity.id = entitySize;
				
				entitySize++;
				entities.add(entity);
			}
		}
	}
	
	static void update() {
		synchronized (entities) {
			for (int i = 0; i < entitySize; i++) {
				//get the position
				float posx = array_position[i*3];
				float posy = array_position[i*3+1];
				float posz = array_position[i*3+2];
				//get the velocity
				float velx = array_velocity[i*3];
				float vely = array_velocity[i*3+1];
				float velz = array_velocity[i*3+2];
				//get the acceleration
				float accx = array_acceleration[i*3];
				float accy = array_acceleration[i*3+1];
				float accz = array_acceleration[i*3+2];
				//weight
				float weight = array_weight[i];
	
				
			}
		}
	}
	
		
}
