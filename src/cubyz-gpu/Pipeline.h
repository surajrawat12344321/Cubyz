#include <vulkan/vulkan.h>

#include <string>
#include <vector>

class Renderer;

/**
 * @brief Handles the graphics pipeline and all the things related to it.
 * This includes shaders, vertext buffers, uniforms, ...
 */
class Pipeline {
public:
	/**
	 * @brief Construct a new Pipeline object
	 * 
	 * @param renderer 
	 * @param shaderPath path to the shader files frag.spv and vert.spv
	 * @param vertexBindingDesc describes how vertex buffers are handled.
	 * @param vertexAttributeDesc describes how all the elements of vertex buffers are handled
	 * @param uniformLayoutBinding binding information of all available uniforms.
	 * @param uniformBufferSizeBytes the size of all uniforms in bytes.
	 */
	Pipeline(Renderer* renderer, std::string shaderPath, VkVertexInputBindingDescription vertexBindingDesc, std::vector<VkVertexInputAttributeDescription> vertexAttributeDesc, std::vector<VkDescriptorSetLayoutBinding> uniformLayoutBinding, int uniformBufferSizeBytes);
	~Pipeline();

	/**
	 * @brief Binds the pipeline to the active command buffer.
	 */
	void bind();
	/**
	 * @brief updates the uniform data for the current frame.
	 * 
	 * @param uniformData pointer to the new uniform. Must be at least uniformBufferSizeBytes bytes long!
	 */
	void updateUniform(void* uniformData);
private:
	VkPipelineLayout layout;
	VkPipeline pipeline;

	VkShaderModule vertShaderModule;
	VkShaderModule fragShaderModule;

	VkVertexInputBindingDescription vertexBindingDesc;
	std::vector<VkVertexInputAttributeDescription> vertexAttributeDesc;

	std::vector<VkDescriptorSetLayoutBinding> uniformLayoutBinding;
	VkDescriptorSetLayout descriptorSetLayout;
	std::vector<VkBuffer> uniformBuffers;
	std::vector<VkDeviceMemory> uniformBuffersMemory;
	const int uniformBufferSizeBytes;

	Renderer* renderer;

	void createDescriptorSetLayout();
	void createPipeline();
	void createUniformBuffers();
};