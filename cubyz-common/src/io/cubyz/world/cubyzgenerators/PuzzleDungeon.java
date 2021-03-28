package io.cubyz.world.cubyzgenerators;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.joml.Vector2i;
import org.joml.Vector3i;

import io.cubyz.api.CubyzRegistries;
import io.cubyz.api.Resource;
import io.cubyz.blocks.Block;
import io.cubyz.math.CubyzMath;
import io.cubyz.world.Chunk;
import io.cubyz.world.NormalChunk;
import io.cubyz.world.Region;
import io.cubyz.world.Surface;
import io.cubyz.world.cubyzgenerators.biomes.Biome;

public class PuzzleDungeon implements Generator {

	//this code is cursed, touch it on your own risk
	//the curse will hunt you
	
	@Override
	public long getGeneratorSeed() {
		return 0xa891e2d3faf57cd7L;
	}

	@Override
	public Resource getRegistryID() {
		// TODO Auto-generated method stub
		return new Resource("cubyz:puzzle_dungeon");
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 1000000;
	}
	Block torch = CubyzRegistries.BLOCK_REGISTRY.getByID("cubyz:torch");
	@Override
	public void generate(long seed, int wx, int wy, int wz, Chunk chunk, Region containingRegion, Surface surface) {	
		//if(chunk.getWidth()!=32)
		if(true)	return;
		Random random = new Random(seed);
		random.setSeed(wx^wy^wz^seed);
		
		for(int x = 0;x<chunk.getWidth();x+=32) {
			for(int y = 0;y<chunk.getWidth();y+=32) {
				for(int z = 0;z<chunk.getWidth();z+=32) {
					generateRoom(x,y,z,random,chunk);			
				}
			}
		}
	}
	
	public class Room{
		public ArrayList<ArrayList<Vector2i>> layers = new ArrayList<ArrayList<Vector2i>>();
	}
	
	
	public Room generateRoomFloorPattern(int x,int z,Random random) {			
		Room room = new Room();
		
		ArrayList<Vector2i> list = new ArrayList<Vector2i>();
		list.add(new Vector2i(0,0));
		int roomsize = 50+random.nextInt(100);
		while(list.size() < roomsize){
			Vector2i expandpoint = new Vector2i(list.get(random.nextInt(list.size())));
			switch (random.nextInt()%4) {
			case 0:
				expandpoint.x++;
				break;
			case 1:
				expandpoint.y++;			
				break;
			case 2:
				expandpoint.x--;
				break;
			case 3:
				expandpoint.y--;
				break;
			default:
				break;
			}
			
			
			if(!list.contains(expandpoint))
			{
				list.add(expandpoint);
				if(list.size()%20==0) {
					room.layers.add(new ArrayList<Vector2i>(list));
				}
			}	
			
		}
		return room;
	}
	
	private int cap(int x) {
		if(0>x)
			return 0;
		if(31<x)
			return 31;
		return x;
	}
	
	public void generateRoom(int x,int y,int z,Random random,Chunk chunk) {
		Room room = generateRoomFloorPattern(x,z,random);
		
		System.out.println(room.layers.size());
		
		int setoff = 0;
		
		for (int layer = room.layers.size()-1;0<=layer; layer--) {
			int height = 2+random.nextInt(2);
			ArrayList<Vector2i> lay = room.layers.get(layer);
			for (int i = 0; i <lay.size() ; i++) {
				for(int py = 0;py<height;py++) {
					chunk.updateBlock(cap(lay.get(i).x+16), cap(py+setoff), cap(lay.get(i).y+16),null);
					//generateRoom(x+px,y+py,z+pz,random,chunk);			
				}
				if(random.nextInt(10)==0&&layer==0)
					chunk.updateBlock(cap(lay.get(i).x+16), 0, cap(lay.get(i).y+16),torch,(byte)16);
			}
			setoff+= height;
		}
	}
	
}



