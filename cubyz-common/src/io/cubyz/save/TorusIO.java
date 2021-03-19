package io.cubyz.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.cubyz.blocks.Block;
import io.cubyz.entity.Entity;
import io.cubyz.entity.Player;
import io.cubyz.items.Item;
import io.cubyz.math.Bits;
import io.cubyz.ndt.NDTContainer;
import io.cubyz.world.LocalStellarTorus;
import io.cubyz.world.LocalSurface;

public class TorusIO {

	final File dir;
	private LocalStellarTorus torus;
	public Palette<Block> blockPalette = new Palette<Block>(null, null);
	public Palette<Item> itemPalette = new Palette<Item>(null, null);

	public TorusIO(LocalStellarTorus torus, File directory) {
		dir = directory;
		if (!dir.exists()) {
			dir.mkdirs();
		}
		this.torus = torus;
	}

	public boolean hasTorusData() {
		return new File(dir, "torus.dat").exists();
	}

	public void loadTorusData(LocalSurface surface) {
		try {
			InputStream in = new FileInputStream(new File(dir, "torus.dat"));
			byte[] len = new byte[4];
			in.read(len);
			int l = Bits.getInt(len, 0);
			byte[] dst = new byte[l];
			in.read(dst);
			
			NDTContainer ndt = new NDTContainer(dst);
			if (ndt.getInteger("version") < 2) {
				in.close();
				throw new RuntimeException("World is out-of-date");
			}
			torus.setName(ndt.getString("name"));
			blockPalette = new Palette<Block>(ndt.getContainer("blockPalette"), surface.registries.blockRegistry);
			itemPalette = new Palette<Item>(ndt.getContainer("itemPalette"), surface.registries.itemRegistry);
			ArrayList<Entity> entities = new ArrayList<Entity>();
			
			for (int i = 0; i < ndt.getInteger("entityCount"); i++) {
				// TODO: Only load entities that are in loaded chunks.
				Entity ent = EntityIO.loadEntity(in, surface);
				if(ent != null)
					entities.add(ent);
			}
			if (surface != null) {
				surface.setEntities(entities.toArray(new Entity[0]));
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveTorusData(LocalSurface surface) {
		try {
			OutputStream out = new FileOutputStream(new File(dir, "torus.dat"));
			NDTContainer ndt = new NDTContainer();
			ndt.setInteger("version", 2);
			ndt.setString("name", torus.getName());
			ndt.setInteger("entityCount", surface == null ? 0 : (surface.getEntities().length + surface.getOfflinePlayers().size()));
			ndt.setContainer("blockPalette", blockPalette.saveTo(new NDTContainer()));
			ndt.setContainer("itemPalette", itemPalette.saveTo(new NDTContainer()));
			byte[] len = new byte[4];
			Bits.putInt(len, 0, ndt.getData().length);
			out.write(len);
			out.write(ndt.getData());
			if (surface != null) {
				for (Entity ent : surface.getEntities()) {
					if(ent != null)
						EntityIO.saveEntity(ent, out);
				}
				for (Player player : surface.getOfflinePlayers()) {
					if(player != null)
						EntityIO.saveEntity(player, out);
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
