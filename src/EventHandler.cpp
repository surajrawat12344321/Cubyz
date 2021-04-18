#include "EventHandler.h"

#include <vector>

namespace cubyz {
	std::vector<void (*)(SDL_AudioDeviceEvent& data)> AudioDeviceEventCallbacks;
	std::vector<void (*)(SDL_ControllerAxisEvent& data)> ControllerAxisEventCallbacks;
	std::vector<void (*)(SDL_ControllerButtonEvent& data)> ControllerButtonEventCallbacks;
	std::vector<void (*)(SDL_ControllerDeviceEvent& data)> ControllerDeviceEventCallbacks;
	std::vector<void (*)(SDL_DollarGestureEvent& data)> DollarGestureEventCallbacks;
	std::vector<void (*)(SDL_DropEvent& data)> DropEventCallbacks;
	std::vector<void (*)(SDL_TouchFingerEvent& data)> TouchFingerEventCallbacks;
	std::vector<void (*)(SDL_KeyboardEvent& data)> KeyboardEventCallbacks;
	std::vector<void (*)(SDL_JoyAxisEvent& data)> JoyAxisEventCallbacks;
	std::vector<void (*)(SDL_JoyBallEvent& data)> JoyBallEventCallbacks;
	std::vector<void (*)(SDL_JoyHatEvent& data)> JoyHatEventCallbacks;
	std::vector<void (*)(SDL_JoyButtonEvent& data)> JoyButtonEventCallbacks;
	std::vector<void (*)(SDL_JoyDeviceEvent& data)> JoyDeviceEventCallbacks;
	std::vector<void (*)(SDL_MouseMotionEvent& data)> MouseMotionEventCallbacks;
	std::vector<void (*)(SDL_MouseButtonEvent& data)> MouseButtonEventCallbacks;
	std::vector<void (*)(SDL_MouseWheelEvent& data)> MouseWheelEventCallbacks;
	std::vector<void (*)(SDL_MultiGestureEvent& data)> MultiGestureEventCallbacks;
	std::vector<void (*)(SDL_QuitEvent& data)> QuitEventCallbacks;
	std::vector<void (*)(SDL_SysWMEvent& data)> SysWMEventCallbacks;
	std::vector<void (*)(SDL_TextEditingEvent& data)> TextEditingEventCallbacks;
	std::vector<void (*)(SDL_TextInputEvent& data)> TextInputEventCallbacks;
	std::vector<void (*)(SDL_UserEvent& data)> UserEventCallbacks;
	std::vector<void (*)(SDL_WindowEvent& data)> WindowEventCallbacks;
	std::vector<void (*)(SDL_CommonEvent& data)> CommonEventCallbacks;


	void addAudioDeviceEventCallback(void (*callback)(SDL_AudioDeviceEvent& data)) {
		AudioDeviceEventCallbacks.push_back(callback);
	}
	void addControllerAxisEventCallback(void (*callback)(SDL_ControllerAxisEvent& data)) {
		ControllerAxisEventCallbacks.push_back(callback);
	}
	void addControllerButtonEventCallback(void (*callback)(SDL_ControllerButtonEvent& data)) {
		ControllerButtonEventCallbacks.push_back(callback);
	}
	void addControllerDeviceEventCallback(void (*callback)(SDL_ControllerDeviceEvent& data)) {
		ControllerDeviceEventCallbacks.push_back(callback);
	}
	void addDollarGestureEventCallback(void (*callback)(SDL_DollarGestureEvent& data)) {
		DollarGestureEventCallbacks.push_back(callback);
	}
	void addDropEventCallback(void (*callback)(SDL_DropEvent& data)) {
		DropEventCallbacks.push_back(callback);
	}
	void addTouchFingerEventCallback(void (*callback)(SDL_TouchFingerEvent& data)) {
		TouchFingerEventCallbacks.push_back(callback);
	}
	void addKeyboardEventCallback(void (*callback)(SDL_KeyboardEvent& data)) {
		KeyboardEventCallbacks.push_back(callback);
	}
	void addJoyAxisEventCallback(void (*callback)(SDL_JoyAxisEvent& data)) {
		JoyAxisEventCallbacks.push_back(callback);
	}
	void addJoyBallEventCallback(void (*callback)(SDL_JoyBallEvent& data)) {
		JoyBallEventCallbacks.push_back(callback);
	}
	void addJoyHatEventCallback(void (*callback)(SDL_JoyHatEvent& data)) {
		JoyHatEventCallbacks.push_back(callback);
	}
	void addJoyButtonEventCallback(void (*callback)(SDL_JoyButtonEvent& data)) {
		JoyButtonEventCallbacks.push_back(callback);
	}
	void addJoyDeviceEventCallback(void (*callback)(SDL_JoyDeviceEvent& data)) {
		JoyDeviceEventCallbacks.push_back(callback);
	}
	void addMouseMotionEventCallback(void (*callback)(SDL_MouseMotionEvent& data)) {
		MouseMotionEventCallbacks.push_back(callback);
	}
	void addMouseButtonEventCallback(void (*callback)(SDL_MouseButtonEvent& data)) {
		MouseButtonEventCallbacks.push_back(callback);
	}
	void addMouseWheelEventCallback(void (*callback)(SDL_MouseWheelEvent& data)) {
		MouseWheelEventCallbacks.push_back(callback);
	}
	void addMultiGestureEventCallback(void (*callback)(SDL_MultiGestureEvent& data)) {
		MultiGestureEventCallbacks.push_back(callback);
	}
	void addQuitEventCallback(void (*callback)(SDL_QuitEvent& data)) {
		QuitEventCallbacks.push_back(callback);
	}
	void addSysWMEventCallback(void (*callback)(SDL_SysWMEvent& data)) {
		SysWMEventCallbacks.push_back(callback);
	}
	void addTextEditingEventCallback(void (*callback)(SDL_TextEditingEvent& data)) {
		TextEditingEventCallbacks.push_back(callback);
	}
	void addTextInputEventCallback(void (*callback)(SDL_TextInputEvent& data)) {
		TextInputEventCallbacks.push_back(callback);
	}
	void addUserEventCallback(void (*callback)(SDL_UserEvent& data)) {
		UserEventCallbacks.push_back(callback);
	}
	void addWindowEventCallback(void (*callback)(SDL_WindowEvent& data)) {
		WindowEventCallbacks.push_back(callback);
	}
	void addCommonEventCallback(void (*callback)(SDL_CommonEvent& data)) {
		CommonEventCallbacks.push_back(callback);
	}


	void handleEvent(SDL_Event& event) {
		switch(event.type) {
			case SDL_AUDIODEVICEADDED:
			case SDL_AUDIODEVICEREMOVED:
			for(int i = 0; i < AudioDeviceEventCallbacks.size(); ++i) {
				AudioDeviceEventCallbacks[i](event.adevice);
			}
			break;
			case SDL_CONTROLLERAXISMOTION:
			for(int i = 0; i < ControllerAxisEventCallbacks.size(); ++i) {
				ControllerAxisEventCallbacks[i](event.caxis);
			}
			break;
			case SDL_CONTROLLERBUTTONDOWN:
			case SDL_CONTROLLERBUTTONUP:
			for(int i = 0; i < ControllerButtonEventCallbacks.size(); ++i) {
				ControllerButtonEventCallbacks[i](event.cbutton);
			}
			break;
			case SDL_CONTROLLERDEVICEADDED:
			case SDL_CONTROLLERDEVICEREMOVED:
			case SDL_CONTROLLERDEVICEREMAPPED:
			for(int i = 0; i < ControllerDeviceEventCallbacks.size(); ++i) {
				ControllerDeviceEventCallbacks[i](event.cdevice);
			}
			break;
			case SDL_DOLLARGESTURE:
			case SDL_DOLLARRECORD:
			for(int i = 0; i < DollarGestureEventCallbacks.size(); ++i) {
				DollarGestureEventCallbacks[i](event.dgesture);
			}
			break;
			case SDL_DROPFILE:
			case SDL_DROPTEXT:
			case SDL_DROPBEGIN:
			case SDL_DROPCOMPLETE:
			for(int i = 0; i < DropEventCallbacks.size(); ++i) {
				DropEventCallbacks[i](event.drop);
			}
			break;
			case SDL_FINGERMOTION:
			case SDL_FINGERDOWN:
			case SDL_FINGERUP:
			for(int i = 0; i < TouchFingerEventCallbacks.size(); ++i) {
				TouchFingerEventCallbacks[i](event.tfinger);
			}
			break;
			case SDL_KEYDOWN:
			case SDL_KEYUP:
			for(int i = 0; i < KeyboardEventCallbacks.size(); ++i) {
				KeyboardEventCallbacks[i](event.key);
			}
			break;
			case SDL_JOYAXISMOTION:
			for(int i = 0; i < JoyAxisEventCallbacks.size(); ++i) {
				JoyAxisEventCallbacks[i](event.jaxis);
			}
			break;
			case SDL_JOYBALLMOTION:
			for(int i = 0; i < JoyBallEventCallbacks.size(); ++i) {
				JoyBallEventCallbacks[i](event.jball);
			}
			break;
			case SDL_JOYHATMOTION:
			for(int i = 0; i < JoyHatEventCallbacks.size(); ++i) {
				JoyHatEventCallbacks[i](event.jhat);
			}
			break;
			case SDL_JOYBUTTONDOWN:
			case SDL_JOYBUTTONUP:
			for(int i = 0; i < JoyButtonEventCallbacks.size(); ++i) {
				JoyButtonEventCallbacks[i](event.jbutton);
			}
			break;
			case SDL_JOYDEVICEADDED:
			case SDL_JOYDEVICEREMOVED:
			for(int i = 0; i < JoyDeviceEventCallbacks.size(); ++i) {
				JoyDeviceEventCallbacks[i](event.jdevice);
			}
			break;
			case SDL_MOUSEMOTION:
			for(int i = 0; i < MouseMotionEventCallbacks.size(); ++i) {
				MouseMotionEventCallbacks[i](event.motion);
			}
			break;
			case SDL_MOUSEBUTTONDOWN:
			case SDL_MOUSEBUTTONUP:
			for(int i = 0; i < MouseButtonEventCallbacks.size(); ++i) {
				MouseButtonEventCallbacks[i](event.button);
			}
			break;
			case SDL_MOUSEWHEEL:
			for(int i = 0; i < MouseWheelEventCallbacks.size(); ++i) {
				MouseWheelEventCallbacks[i](event.wheel);
			}
			break;
			case SDL_MULTIGESTURE:
			for(int i = 0; i < MultiGestureEventCallbacks.size(); ++i) {
				MultiGestureEventCallbacks[i](event.mgesture);
			}
			break;
			case SDL_QUIT:
			for(int i = 0; i < QuitEventCallbacks.size(); ++i) {
				QuitEventCallbacks[i](event.quit);
			}
			break;
			case SDL_SYSWMEVENT:
			for(int i = 0; i < SysWMEventCallbacks.size(); ++i) {
				SysWMEventCallbacks[i](event.syswm);
			}
			break;
			case SDL_TEXTEDITING:
			for(int i = 0; i < TextEditingEventCallbacks.size(); ++i) {
				TextEditingEventCallbacks[i](event.edit);
			}
			break;
			case SDL_TEXTINPUT:
			for(int i = 0; i < TextInputEventCallbacks.size(); ++i) {
				TextInputEventCallbacks[i](event.text);
			}
			break;
			case SDL_USEREVENT:
			for(int i = 0; i < UserEventCallbacks.size(); ++i) {
				UserEventCallbacks[i](event.user);
			}
			break;
			case SDL_WINDOWEVENT:
			for(int i = 0; i < WindowEventCallbacks.size(); ++i) {
				WindowEventCallbacks[i](event.window);
			}
			break;
			default:
			for(int i = 0; i < CommonEventCallbacks.size(); ++i) {
				CommonEventCallbacks[i](event.common);
			}
			break;
		}
	}
}