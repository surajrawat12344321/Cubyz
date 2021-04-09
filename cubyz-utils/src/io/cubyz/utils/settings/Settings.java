package io.cubyz.utils.settings;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.cubyz.utils.datastructures.Registry;
import io.cubyz.utils.log.Log;
import io.cubyz.utils.settings.elements.SettingVector2f;

/**
 * 
 * Stores all the settings in the game
 *
 */
public final class Settings {

	// List of all Settingstype
	public static final Registry<Setting> settingTypes = new Registry<Setting>(new SettingVector2f());

	// actual settings
	private static HashMap<String, Setting> settings = new HashMap<String, Setting>();
	private static HashMap<String, SettingChanger> settingChanger = new HashMap<String, SettingChanger>();

	public static void setStandartSetting(String string, Setting standartSetting, SettingChanger changer) {
		if (!settings.containsKey(string)) {
			settings.put(string, standartSetting);
			settingChanger.put(string, changer);
		}
	}

	public static Setting get(String string) {
		return settings.get(string);
	}

	public static void set(String string, Setting setting) {
		settings.put(string, setting);
	}

	
	public static Setting createByJson(JsonObject jsonObject) {
		Setting setting = (Setting) settingTypes.getById(jsonObject.getAsJsonPrimitive("type").getAsString());
		if (setting == null) {
			Log.severe("Cubyz:unkown Settings-Type:" + jsonObject.toString());
		}
		try {
			Setting c = setting.getClass().getConstructor().newInstance();
			c.fromJson(jsonObject);
			return c;
		} catch (Exception e) {
			Log.severe(e);
		}
		return null;
	}

}
