#include "Window.h"

namespace window {
	GLFWwindow* window;
	bool shouldClose = false;

	void create() {
		glfwInit();
		window = glfwCreateWindow(1920, 1080, "Cubyz", NULL, NULL);
	}

	void render() {
		glfwPollEvents();
		shouldClose = glfwWindowShouldClose(window);
	}
}