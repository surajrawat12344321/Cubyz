#pragma once

#include <vulkan/vulkan.h>

/**
 * @brief Handles the rendering and initializing of all rendering related things.
 */
class Renderer {
public:
	VkDevice device;
	VkPhysicalDevice physicalDevice = VK_NULL_HANDLE;

	VkExtent2D swapChainExtent;
	VkRenderPass renderPass;

	int activeFrame;
	int swapChainSize;

	std::vector<VkCommandBuffer> commandBuffers;
};