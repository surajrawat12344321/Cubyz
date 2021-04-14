#include "../Renderer.h"
#include "../../Logger.h"
#include <vulkan/vulkan.h>
#include <SFML/Window.hpp>

// For further info about this, visit: https://vulkan-tutorial.com

namespace renderer {
	// Window:
	sf::Window window(sf::VideoMode(200, 200), "SFML works!");

	int width = 1920, height = 1080;

	// Vulkan:
	VkInstance instance;


	void init() {
		//glfwInit();
		//glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API); // We don't want openGL.

		//window = glfwCreateWindow(width, height, "Cubyz", nullptr, nullptr);


		// Create the vulkan instance:
		VkApplicationInfo appInfo{};
		appInfo.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
		appInfo.pApplicationName = "Cubyz";
		appInfo.applicationVersion = VK_MAKE_VERSION(1, 0, 0);
		appInfo.pEngineName = "Cubyz Engine";
		appInfo.engineVersion = VK_MAKE_VERSION(1, 0, 0);
		appInfo.apiVersion = VK_API_VERSION_1_0;


		VkInstanceCreateInfo createInfo{};
		createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
		createInfo.pApplicationInfo = &appInfo;

		uint32_t glfwExtensionCount = 0;
		const char** glfwExtensions;

		//glfwExtensions = glfwGetRequiredInstanceExtensions(&glfwExtensionCount);

		createInfo.enabledExtensionCount = glfwExtensionCount;
		createInfo.ppEnabledExtensionNames = glfwExtensions;

		createInfo.enabledLayerCount = 0;


		/*VkResult result = vkCreateInstance(&createInfo, nullptr, &instance);
		if (result != VK_SUCCESS) {
			// TODO: Logger
		}*/

	}

	void render() {
		//glfwPollEvents();
	}

	bool shouldClose() {
		return true;//glfwWindowShouldClose(window);
	}

	void cleanup() {
		//vkDestroyInstance(instance, nullptr);

		//glfwDestroyWindow(window);

		//glfwTerminate();
	}
}