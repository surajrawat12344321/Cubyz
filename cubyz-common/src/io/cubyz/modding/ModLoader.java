package io.cubyz.modding;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import io.cubyz.Constants;
import io.cubyz.api.CubyzRegistries;
import io.cubyz.api.CurrentSurfaceRegistries;
import io.cubyz.api.EventHandler;
import io.cubyz.api.LoadOrder;
import io.cubyz.api.Mod;
import io.cubyz.api.Order;
import io.cubyz.api.Proxy;
import io.cubyz.api.Registry;
import io.cubyz.api.Side;
import io.cubyz.api.SideOnly;
import io.cubyz.base.AddonsMod;
import io.cubyz.base.BaseMod;

import static io.cubyz.CubyzLogger.logger;

/**
 * Most methods should ALWAYS be found as if it were on Side.SERVER
 */
public class ModLoader {
	public static final ArrayList<Object> mods = new ArrayList<Object>();
	
	public static void loadMods() {
		// Load Mods (via reflection)
		ArrayList<File> modSearchPath = new ArrayList<>();
		modSearchPath.add(new File("mods"));
		modSearchPath.add(new File("mods/" + Constants.GAME_VERSION));
		ArrayList<String> modPaths = new ArrayList<>();
		
		for (File sp : modSearchPath) {
			if (!sp.exists()) {
				sp.mkdirs();
			}
			for (File mod : sp.listFiles()) {
				if (mod.isFile()) {
					modPaths.add(mod.getAbsolutePath());
					System.out.println("- Add " + mod.getName());
				}
			}
		}
		
		logger.info("Seeking mods..");
		long start = System.currentTimeMillis();
		// Load all mods:
		ArrayList<Class<?>> allClasses = new ArrayList<>();
		for(String path : modPaths) {
			loadModClasses(path, allClasses);
		}
		long end = System.currentTimeMillis();
		logger.info("Took " + (end - start) + "ms for reflection");
		if (!allClasses.contains(BaseMod.class)) {
			allClasses.add(BaseMod.class);
			allClasses.add(AddonsMod.class);
			logger.info("Manually adding BaseMod (probably on distributed JAR)");
		}
		for (Class<?> cl : allClasses) {
			logger.info("Mod class present: " + cl.getName());
			try {
				mods.add(cl.getConstructor().newInstance());
			} catch (Exception e) {
				logger.warning("Error while loading mod:");
				e.printStackTrace();
			}
		}
		logger.info("Mod list complete");
		sortMods();
		
		// TODO re-add pre-init
		for (int i = 0; i < mods.size(); i++) {
			Object mod = mods.get(i);
			logger.info("Pre-initiating " + mod);
			preInit(mod);
		}
		
		// Between pre-init and init code
		
		for (int i = 0; i < mods.size(); i++) {
			Object mod = mods.get(i);
			registerEntries(mod, "block");
		}
		for (int i = 0; i < mods.size(); i++) {
			Object mod = mods.get(i);
			registerEntries(mod, "item");
		}
		for (int i = 0; i < mods.size(); i++) {
			Object mod = mods.get(i);
			registerEntries(mod, "entity");
		}
		for (int i = 0; i < mods.size(); i++) {
			Object mod = mods.get(i);
			registerEntries(mod, "biome");
		}
		
		for (int i = 0; i < mods.size(); i++) {
			Object mod = mods.get(i);
			logger.info("Initiating " + mod);
			init(mod);
		}
	}
	
	public static void postInit() {
		
	}
	
	public static void loadModClasses(String pathToJar, ArrayList<Class<?>> modClasses) {
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
			    if(c.isAnnotationPresent(Mod.class)) modClasses.add(c);
	
			}
			jarFile.close();
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isCorrectSide(Side currentSide, Method method) {
		boolean haveAnnot = false;
		for (Annotation annot : method.getAnnotations()) {
			if (annot.annotationType().equals(SideOnly.class)) {
				SideOnly anno = (SideOnly) annot;
				haveAnnot = true;
				if (anno.side() == currentSide) {
					return true;
				}
			}
		}
		if (!haveAnnot) {
			return true;
		}
		return false;
	}
	
	public static Method eventHandlerMethodSided(Object mod, String eventType, Side side) {
		Class<?> cl = mod.getClass();
		for (Method m : cl.getMethods()) {
			if (m.isAnnotationPresent(EventHandler.class)) {
				if (isCorrectSide(side, m)) {
					if (m.getAnnotation(EventHandler.class).type().equals(eventType)) {
						return m;
					}
				}
			}
		}
		return null;
	}
	
	public static Method eventHandlerMethod(Object mod, String eventType) {
		Class<?> cl = mod.getClass();
		for (Method m : cl.getMethods()) {
			if (m.isAnnotationPresent(EventHandler.class)) {
				if (m.getAnnotation(EventHandler.class).type().equals(eventType)) {
					return m;
				}
			}
		}
		return null;
	}
	
	public static void sortMods() {
		HashMap<String, Object> modIds = new HashMap<>();
		for (Object mod : mods) {
			Mod annot = mod.getClass().getAnnotation(Mod.class);
			modIds.put(annot.id(), mod);
		}
		for (int i = 0; i < mods.size(); i++) {
			Object mod = mods.get(i);
			Class<?> cl = mod.getClass();
			LoadOrder[] orders = cl.getAnnotationsByType(LoadOrder.class);
			for (LoadOrder order : orders) {
				if (order.order() == Order.AFTER) {
					mods.remove(i);
					mods.add(mods.indexOf(modIds.get(order.id()))+1, mod);
				}
			}
		}
	}
	
	public static void preInit(Object mod) {
		injectProxy(mod);
		Method m = eventHandlerMethodSided(mod, "preInit", Side.SERVER);
		if (m != null)
			safeMethodInvoke(true, m, mod);
	}
	
	public static void init(Object mod) {
		Method m = eventHandlerMethodSided(mod, "init", Side.SERVER);
		if (m != null)
			safeMethodInvoke(true, m, mod);
	}
	
	public static void registerEntries(Object mod, String type) {
		Method method = eventHandlerMethod(mod, "register:" + type);
		if (method != null) {
			Registry<?> reg = null;
			switch (type) {
			case "block":
				reg = CubyzRegistries.BLOCK_REGISTRY;
				break;
			case "item":
				reg = CubyzRegistries.ITEM_REGISTRY;
				break;
			case "entity":
				reg = CubyzRegistries.ENTITY_REGISTRY;
				break;
			case "biome":
				reg = CubyzRegistries.BIOME_REGISTRY;
				break;
			}
			safeMethodInvoke(true, method, mod, reg);
		}
	}
	
	public static void postInit(Object mod) {
		Method m = eventHandlerMethodSided(mod, "postInit", Side.SERVER);
		if (m != null)
			safeMethodInvoke(true, m, mod);
	}
	
	/**
	 * Calls mods after the surface has been generated.
	 * @param mod
	 * @param reg registries of this surface.
	 */
	public static void postSurfaceGen(CurrentSurfaceRegistries reg) {
		for(Object mod : mods) {
			Method m = eventHandlerMethodSided(mod, "postSurfaceGen", Side.SERVER);
			if (m != null)
				safeMethodInvoke(true, m, mod, reg);
		}
	}
	
	static void injectProxy(Object mod) {
		Class<?> cl = mod.getClass();
		for (Field field : cl.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Proxy.class)) {
				Proxy a = field.getAnnotation(Proxy.class);
				try {
					if (Constants.getGameSide() == Side.CLIENT) {
						field.set(mod, Class.forName(a.clientProxy()).getConstructor().newInstance());
					} else {
						field.set(mod, Class.forName(a.serverProxy()).getConstructor().newInstance());
					}
				} catch (IllegalArgumentException | IllegalAccessException | InstantiationException
						| InvocationTargetException | NoSuchMethodException | SecurityException
						| ClassNotFoundException e) {
					logger.warning("Could not inject Proxy!");
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	static void safeMethodInvoke(boolean imp /* is it important (e.g. at init) */, Method m, Object o, Object... args) {
		try {
			m.invoke(o, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (e instanceof InvocationTargetException) {
				logger.warning("Error while invoking mod method (" + m + "):");
				e.getCause().printStackTrace();
			} else {
				e.printStackTrace();
			}
			if (imp) {
				System.exit(1);
			}
		}
	}
	
}
