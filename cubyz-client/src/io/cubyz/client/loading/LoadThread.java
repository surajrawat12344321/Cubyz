package io.cubyz.client.loading;

import java.util.ArrayList;

import io.cubyz.ClientOnly;
import io.cubyz.ClientSettings;
import io.cubyz.api.CubyzRegistries;
import io.cubyz.api.Resource;
import io.cubyz.blocks.Block;
import io.cubyz.client.Cubyz;
import io.cubyz.client.GameLauncher;
import io.cubyz.entity.EntityType;
import io.cubyz.modding.ModLoader;
import io.cubyz.rendering.Mesh;
import io.cubyz.rendering.ModelLoader;
import io.cubyz.ui.LoadingGUI;
import io.cubyz.utils.ResourceContext;
import io.cubyz.utils.ResourceManager;

/**
 * Loads all mods.
 */

public class LoadThread extends Thread {

	static int i = -1;
	static Runnable run;
	static ArrayList<Runnable> runnables = new ArrayList<>();
	
	public static void addOnLoadFinished(Runnable run) {
		runnables.add(run);
	}
	
	public void run() {
		setName("Load-Thread");
		Cubyz.renderDeque.add(ClientSettings::load); // run in render thread due to some graphical reasons
		LoadingGUI l = LoadingGUI.getInstance();
		l.setStep(1, 0, 0);
		
		l.setStep(2, 0, 0); // load mods
		
		ModLoader.loadMods();
		// TODO: Make progress bars work again for mod loading and initing.
		
		Object lock = new Object();
		run = new Runnable() {
			public void run() {
				i++;
				boolean finishedMeshes = false;
				if(i < CubyzRegistries.BLOCK_REGISTRY.size() || i < CubyzRegistries.ENTITY_REGISTRY.size()) {
					if(i < CubyzRegistries.BLOCK_REGISTRY.size()) {
						Block b = CubyzRegistries.BLOCK_REGISTRY.registered(new Block[0])[i];
						ClientOnly.createBlockMesh.accept(b);
					}
					if(i < CubyzRegistries.ENTITY_REGISTRY.size()) {
						EntityType e = CubyzRegistries.ENTITY_REGISTRY.registered(new EntityType[0])[i];
						if (!e.useDynamicEntityModel()) {
							ClientOnly.createEntityMesh.accept(e);
						}
					}
					if(i < CubyzRegistries.BLOCK_REGISTRY.size()-1 || i < CubyzRegistries.ENTITY_REGISTRY.size()-1) {
						Cubyz.renderDeque.add(run);
						l.setStep(4, i+1, CubyzRegistries.BLOCK_REGISTRY.size());
					} else {
						finishedMeshes = true;
						synchronized (lock) {
							lock.notifyAll();
						}
					}
				} else {
					finishedMeshes = true;
					synchronized (lock) {
						lock.notifyAll();
					}
				}
				if(finishedMeshes) {
					try {
						GameLauncher.logic.skyBodyMesh = new Mesh(ModelLoader.loadModel(new Resource("cubyz:sky_body.obj"), ResourceManager.lookupPath(ResourceManager.contextToLocal(ResourceContext.MODEL3D, new Resource("cubyz:sky_body.obj")))));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		Cubyz.renderDeque.add(run);
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e) {
			return;
		}
		
		l.setStep(5, 0, ModLoader.mods.size());
		ModLoader.postInit();
		l.finishLoading();
		
		for (Runnable r : runnables) {
			r.run();
		}
		
		System.gc();
	}
	
}
