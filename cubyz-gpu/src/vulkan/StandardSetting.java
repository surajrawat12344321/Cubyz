package vulkan;

import io.cubyz.utils.settings.Settings;
import io.cubyz.utils.settings.SettingsConstants;
import io.cubyz.utils.settings.elements.SettingVector2f;

public abstract class StandardSetting {
	public static void init() {
		//standart values
		Settings.setStandartSetting(SettingsConstants.WINDOW_SIZE, new SettingVector2f(1280,720),null);
	}
}
