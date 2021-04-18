#include <SDL2/SDL.h>

namespace cubyz {
	void addAudioDeviceEventCallback(void (*callback)(SDL_AudioDeviceEvent& data));
	void addControllerAxisEventCallback(void (*callback)(SDL_ControllerAxisEvent& data));
	void addControllerButtonEventCallback(void (*callback)(SDL_ControllerButtonEvent& data));
	void addControllerDeviceEventCallback(void (*callback)(SDL_ControllerDeviceEvent& data));
	void addDollarGestureEventCallback(void (*callback)(SDL_DollarGestureEvent& data));
	void addDropEventCallback(void (*callback)(SDL_DropEvent& data));
	void addTouchFingerEventCallback(void (*callback)(SDL_TouchFingerEvent& data));
	void addKeyboardEventCallback(void (*callback)(SDL_KeyboardEvent& data));
	void addJoyAxisEventCallback(void (*callback)(SDL_JoyAxisEvent& data));
	void addJoyBallEventCallback(void (*callback)(SDL_JoyBallEvent& data));
	void addJoyHatEventCallback(void (*callback)(SDL_JoyHatEvent& data));
	void addJoyButtonEventCallback(void (*callback)(SDL_JoyButtonEvent& data));
	void addJoyDeviceEventCallback(void (*callback)(SDL_JoyDeviceEvent& data));
	void addMouseMotionEventCallback(void (*callback)(SDL_MouseMotionEvent& data));
	void addMouseButtonEventCallback(void (*callback)(SDL_MouseButtonEvent& data));
	void addMouseWheelEventCallback(void (*callback)(SDL_MouseWheelEvent& data));
	void addMultiGestureEventCallback(void (*callback)(SDL_MultiGestureEvent& data));
	void addQuitEventCallback(void (*callback)(SDL_QuitEvent& data));
	void addSysWMEventCallback(void (*callback)(SDL_SysWMEvent& data));
	void addTextEditingEventCallback(void (*callback)(SDL_TextEditingEvent& data));
	void addTextInputEventCallback(void (*callback)(SDL_TextInputEvent& data));
	void addUserEventCallback(void (*callback)(SDL_UserEvent& data));
	void addWindowEventCallback(void (*callback)(SDL_WindowEvent& data));
	void addCommonEventCallback(void (*callback)(SDL_CommonEvent& data));

	/**
	Calls all callbacks for the specified event type.
	*/
	void handleEvent(SDL_Event& event);
}