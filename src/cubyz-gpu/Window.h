#ifndef _WINDOW_H_
#define _WINDOW_H_

#include <GLFW/glfw3.h>

/*
Initializes a window using glfw.
*/
namespace window {
	extern bool shouldClose;
	void create();

	void render();
}
#endif