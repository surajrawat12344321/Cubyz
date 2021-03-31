package io.cubyz.server.modding;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import io.cubyz.utils.log.Log;

/**
 * Loads mods from the mods folder using java Reflections API.<br>
 * Mod loading is done once per universe.
 */
public class ModLoader {
	public final Mod[] mods;
	
	/**
	 * 
	 * @param modNames path to the file that contains all the mod names.
	 */
	public ModLoader(File modNames) {
		// Read all mod names from the file:
		String[] modIdentifier = new String[0];
		try {
			modIdentifier = Files.readAllLines(modNames.toPath()).toArray(modIdentifier);
		} catch(IOException e) {
			Log.severe("Could not load any mods!");
			Log.severe(e);
		}
		
		ArrayList<File> loadList = new ArrayList<File>();
		for(int i = 0; i < modIdentifier.length; i++) {
			File modPath = findOrDownloadMod(modIdentifier[i]);
			if(modPath == null) continue;
			loadList.add(modPath);
		}
		
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
		
		// TODO: Add the addons mod to the list.
		
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
		
		// TODO: Load the mods and register stuff.
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
