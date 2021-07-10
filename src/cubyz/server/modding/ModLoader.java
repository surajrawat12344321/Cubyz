package cubyz.server.modding;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cubyz.server.AddonsLoader;
import cubyz.utils.gui.StatusInfo;
import cubyz.utils.log.Log;
import cubyz.world.Registries;

/**
 * Loads mods from the mods folder using java Reflections API.<br>
 * Mod loading is done once per universe.
 */
public class ModLoader {
	public final Mod[] mods;
	
	/**
	 * 
	 * @param modNames path to the file that contains all the mod names.
	 * @param modLoadingStatus info about the mod loading process that will be shown on the status bar.
	 * @param internalMods mod classes that are part of the main game.
	 */
	public ModLoader(File modNames, StatusInfo modLoadingStatus, Mod... internalMods) {
		// Ensure that there are no leftovers from the last universe:
		Registries.clear();

		modLoadingStatus.totalProcesses = 8;
		modLoadingStatus.currentProcess = 0;
		modLoadingStatus.processName = "Downloading Mods...";
		// Read all mod names from the file:
		String[] modIdentifier = new String[0];
		try {
			modIdentifier = Files.readAllLines(modNames.toPath()).toArray(modIdentifier);
			if(modIdentifier.length == 0) throw new Exception("No mods found.");
		} catch(Exception e) {
			Log.severe("Could not load any mods!");
			Log.severe(e);
		}
		
		ArrayList<File> loadList = new ArrayList<File>();
		for(int i = 0; i < modIdentifier.length; i++) {
			File modPath = findOrDownloadMod(modIdentifier[i]);
			if(modPath == null) continue;
			loadList.add(modPath);
		}

		modLoadingStatus.currentProcess = 1;
		modLoadingStatus.processName = "Collecting Mods...";
		// Find all jars in each folder:
		ArrayList<String> modJars = new ArrayList<String>();
		for(File file : loadList) {
			for(String child : file.list()) {
				if(child.endsWith(".jar"))
					modJars.add(child);
			}
		}
		
		// Find all the Mod implementations in the jar files using reflections:
		ArrayList<Class<?>> modClasses = new ArrayList<>();
		for(String path : modJars) {
			loadModsFromJar(path, modClasses);
		}
		
		
		ArrayList<Mod> modList = new ArrayList<>();
		
		// Add the default mods to the list.
		for(Mod mod : internalMods) {
			modList.add(mod);
		}
		
		// Instantiate the mods:
		for (Class<?> cl : modClasses) {
			try {
				modList.add((Mod)cl.getConstructor().newInstance());
			} catch (Exception e) {
				Log.warning("Error while loading mod:");
				Log.warning(e);
			}
		}
		
		mods = modList.toArray(new Mod[0]);
		
		// Load the mods and register stuff.
		StatusInfo modStatus = new StatusInfo();
		modStatus.totalProcesses = mods.length;
		
		modLoadingStatus.currentProcess = 2;
		modLoadingStatus.processName = "Initializing Mods...";
		modLoadingStatus.subProcess = modStatus;
		// Init:
		for(int i = 0; i < mods.length; i++) {
			Mod mod = mods[i];
			modStatus.currentProcess = i;
			modStatus.processName = mod.getName();
			
			mod.init();
		}

		modLoadingStatus.currentProcess = 3;
		modLoadingStatus.processName = "Initializing Items...";
		// Items:
		for(int i = 0; i < mods.length; i++) {
			Mod mod = mods[i];
			modStatus.currentProcess = i;
			modStatus.processName = mod.getName();
			
			mod.registerItems(Registries.ITEMS);
		}

		modLoadingStatus.currentProcess = 4;
		modLoadingStatus.processName = "Initializing Blocks...";
		// Blocks:
		for(int i = 0; i < mods.length; i++) {
			Mod mod = mods[i];
			modStatus.currentProcess = i;
			modStatus.processName = mod.getName();
			
			mod.registerBlocks();
		}
		AddonsLoader.loadBlocks("assets");

		modLoadingStatus.currentProcess = 5;
		modLoadingStatus.processName = "Initializing Entities...";
		// Entities:
		for(int i = 0; i < mods.length; i++) {
			Mod mod = mods[i];
			modStatus.currentProcess = i;
			modStatus.processName = mod.getName();
			
			mod.registerEntities(Registries.ENTITIES);
		}

		modLoadingStatus.currentProcess = 6;
		modLoadingStatus.processName = "Initializing Biomes...";
		// Biomes:
		for(int i = 0; i < mods.length; i++) {
			Mod mod = mods[i];
			modStatus.currentProcess = i;
			modStatus.processName = mod.getName();
			
			mod.registerBiomes(Registries.BIOMES);
		}

		modLoadingStatus.currentProcess = 7;
		modLoadingStatus.processName = "Post-Initializing Mods...";
		// Post-Init:
		for(int i = 0; i < mods.length; i++) {
			Mod mod = mods[i];
			modStatus.currentProcess = i;
			modStatus.processName = mod.getName();
			
			mod.postInit();
		}
		
	}
	
	public static void loadModsFromJar(String pathToJar, ArrayList<Class<?>> modClasses) {
		try {
			JarFile jarFile = new JarFile(pathToJar);
			Enumeration<JarEntry> e = jarFile.entries();
	
			URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);
	
			while (e.hasMoreElements()) {
			    JarEntry je = e.nextElement();
			    if(je.isDirectory() || !je.getName().endsWith(".class") || je.getName().contains("module-info")){
			        continue;
			    }
			    // -6 because of .class
			    String className = je.getName().substring(0,je.getName().length()-6);
			    className = className.replace('/', '.');
			    Class<?> c = cl.loadClass(className);
			    if(Mod.class.isAssignableFrom(c)) modClasses.add(c);
	
			}
			jarFile.close();
		} catch(Exception e) {
			Log.warning("Something went wrong while loading the jar file "+pathToJar);
			Log.warning(e);
		}
	}
	
	public static File findOrDownloadMod(String description) {
		// Check if it's in the mods folder:
		File modPath = new File("mods/"+description);
		if(modPath.exists() && modPath.isDirectory()) {
			// Check if mod was downloaded without errors:
			File checkFile = new File(modPath.getAbsolutePath()+"/.check");
			if(checkFile.exists()) return modPath;
		} else {
			modPath.mkdirs();
		}
		
		try {
			// TODO: Actually download the mod.
			
			// Add the .check file to give the info, that the download was successful:
			Files.createFile(new File(modPath.getAbsolutePath()+"/.check").toPath());
			return modPath;
		} catch(Exception e) {
			Log.severe("Could not download mod: "+description+".");
			Log.severe(e);
			return null;
		}
	}
}
