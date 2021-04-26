#include "Pipeline.h"

#include <cstring>
#include <fstream>

#include "Renderer.h"

static std::vector<char> readFile(const std::string& filename) {
	std::ifstream file(filename, std::ios::ate | std::ios::binary);

	if (!file.is_open()) {
		throw std::runtime_error("failed to open file!");
	}

	size_t fileSize = (size_t) file.tellg();
	std::vector<char> buffer(fileSize);

	file.seekg(0);
	file.read(buffer.data(), fileSize);

	file.close();

	return buffer;
}

static VkShaderModule createShaderModule(std::string path, const VkDevice& device) {
	const std::vector<char>& code = readFile(path);
	VkShaderModuleCreateInfo createInfo{};
	createInfo.sType = VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
	createInfo.codeSize = code.size();
	createInfo.pCode = reinterpret_cast<const uint32_t*>(code.data());

	VkShaderModule shaderModule;
	VkResult result = vkCreateShaderModule(device, &createInfo, nullptr, &shaderModule);
	if (result != VK_SUCCESS) {
		throw std::runtime_error("Failed to create shader module! Error code: "+std::to_string(result));
	}

	return shaderModule;
}

static uint32_t findMemoryType(uint32_t typeFilter, VkMemoryPropertyFlags properties, const VkPhysicalDevice& physicalDevice) {
	VkPhysicalDeviceMemoryProperties memProperties;
	vkGetPhysicalDeviceMemoryProperties(physicalDevice, &memProperties);

	for (uint32_t i = 0; i < memProperties.memoryTypeCount; i++) {
		if ((typeFilter & (1 << i)) && (memProperties.memoryTypes[i].propertyFlags & properties) == properties) {
			return i;
		}
	}

	throw std::runtime_error("failed to find suitable memory type!");
}

static void createBuffer(VkDeviceSize size, VkBufferUsageFlags usage, VkMemoryPropertyFlags properties, VkBuffer& buffer, VkDeviceMemory& bufferMemory, const VkDevice& device, const VkPhysicalDevice& physicalDevice) {
	VkBufferCreateInfo bufferInfo{};
	bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
	bufferInfo.size = size;
	bufferInfo.usage = usage;
	bufferInfo.sharingMode = VK_SHARING_MODE_EXCLUSIVE;

	VkResult result = vkCreateBuffer(device, &bufferInfo, nullptr, &buffer);
	if (result != VK_SUCCESS) {
		throw std::runtime_error("Failed to create buffer! Error code: "+std::to_string(result));
	}

	VkMemoryRequirements memRequirements;
	vkGetBufferMemoryRequirements(device, buffer, &memRequirements);

	VkMemoryAllocateInfo allocInfo{};
	allocInfo.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
	allocInfo.allocationSize = memRequirements.size;
	allocInfo.memoryTypeIndex = findMemoryType(memRequirements.memoryTypeBits, properties, physicalDevice);

	result = vkAllocateMemory(device, &allocInfo, nullptr, &bufferMemory);
	if (result != VK_SUCCESS) {
		throw std::runtime_error("Failed to allocate buffer memory! Error code: "+std::to_string(result));
	}

	vkBindBufferMemory(device, buffer, bufferMemory, 0);
}

Pipeline::Pipeline(Renderer* renderer, std::string shaderPath, VkVertexInputBindingDescription vertexBindingDesc,
				std::vector<VkVertexInputAttributeDescription> vertexAttributeDesc, std::vector<VkDescriptorSetLayoutBinding> uniformLayoutBinding, int uniformBufferSizeBytes) :
				renderer(renderer),
				vertexBindingDesc(vertexBindingDesc),
				vertexAttributeDesc(vertexAttributeDesc),
				uniformLayoutBinding(uniformLayoutBinding),
				uniformBufferSizeBytes(uniformBufferSizeBytes),
				vertShaderModule(createShaderModule(shaderPath+"/vert.spv", renderer->device)),
				fragShaderModule(createShaderModule(shaderPath+"/frag.spv", renderer->device))
				 {
	this->renderer = renderer;
	this->vertexBindingDesc = vertexBindingDesc;
	this->vertexAttributeDesc = vertexAttributeDesc;
	this->uniformLayoutBinding = uniformLayoutBinding;

	createDescriptorSetLayout();
	createPipeline();
}

void Pipeline::createPipeline() {
	VkPipelineShaderStageCreateInfo vertShaderStageInfo{};
	vertShaderStageInfo.sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
	vertShaderStageInfo.stage = VK_SHADER_STAGE_VERTEX_BIT;
	vertShaderStageInfo.module = vertShaderModule;
	vertShaderStageInfo.pName = "main";

	VkPipelineShaderStageCreateInfo fragShaderStageInfo{};
	fragShaderStageInfo.sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
	fragShaderStageInfo.stage = VK_SHADER_STAGE_FRAGMENT_BIT;
	fragShaderStageInfo.module = fragShaderModule;
	fragShaderStageInfo.pName = "main";

	VkPipelineShaderStageCreateInfo shaderStages[] = {vertShaderStageInfo, fragShaderStageInfo};

	VkPipelineVertexInputStateCreateInfo vertexInputInfo{};
	vertexInputInfo.sType = VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO;

	vertexInputInfo.vertexBindingDescriptionCount = 1;
	vertexInputInfo.vertexAttributeDescriptionCount = static_cast<uint32_t>(vertexAttributeDesc.size());
	vertexInputInfo.pVertexBindingDescriptions = &vertexBindingDesc;
	vertexInputInfo.pVertexAttributeDescriptions = vertexAttributeDesc.data();

	VkPipelineInputAssemblyStateCreateInfo inputAssembly{};
	inputAssembly.sType = VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO;
	inputAssembly.topology = VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
	inputAssembly.primitiveRestartEnable = VK_FALSE;

	VkViewport viewport{};
	viewport.x = 0.0f;
	viewport.y = 0.0f;
	viewport.width = (float) renderer->swapChainExtent.width;
	viewport.height = (float) renderer->swapChainExtent.height;
	viewport.minDepth = 0.0f;
	viewport.maxDepth = 1.0f;

	VkRect2D scissor{};
	scissor.offset = {0, 0};
	scissor.extent = renderer->swapChainExtent;

	VkPipelineViewportStateCreateInfo viewportState{};
	viewportState.sType = VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO;
	viewportState.viewportCount = 1;
	viewportState.pViewports = &viewport;
	viewportState.scissorCount = 1;
	viewportState.pScissors = &scissor;

	VkPipelineRasterizationStateCreateInfo rasterizer{};
	rasterizer.sType = VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO;
	rasterizer.depthClampEnable = VK_FALSE;
	rasterizer.rasterizerDiscardEnable = VK_FALSE;
	rasterizer.polygonMode = VK_POLYGON_MODE_FILL;
	rasterizer.lineWidth = 1.0f;
	rasterizer.cullMode = VK_CULL_MODE_BACK_BIT;
	rasterizer.frontFace = VK_FRONT_FACE_COUNTER_CLOCKWISE;
	rasterizer.depthBiasEnable = VK_FALSE;

	VkPipelineMultisampleStateCreateInfo multisampling{};
	multisampling.sType = VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO;
	multisampling.sampleShadingEnable = VK_FALSE;
	multisampling.rasterizationSamples = VK_SAMPLE_COUNT_1_BIT;

	VkPipelineDepthStencilStateCreateInfo depthStencil{};
	depthStencil.sType = VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO;
	depthStencil.depthTestEnable = VK_TRUE;
	depthStencil.depthWriteEnable = VK_TRUE;
	depthStencil.depthCompareOp = VK_COMPARE_OP_LESS;
	depthStencil.depthBoundsTestEnable = VK_FALSE;
	depthStencil.stencilTestEnable = VK_FALSE;

	VkPipelineColorBlendAttachmentState colorBlendAttachment{};
	colorBlendAttachment.colorWriteMask = VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT;
	colorBlendAttachment.blendEnable = VK_FALSE;

	VkPipelineColorBlendStateCreateInfo colorBlending{};
	colorBlending.sType = VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO;
	colorBlending.logicOpEnable = VK_FALSE;
	colorBlending.logicOp = VK_LOGIC_OP_COPY;
	colorBlending.attachmentCount = 1;
	colorBlending.pAttachments = &colorBlendAttachment;
	colorBlending.blendConstants[0] = 0.0f;
	colorBlending.blendConstants[1] = 0.0f;
	colorBlending.blendConstants[2] = 0.0f;
	colorBlending.blendConstants[3] = 0.0f;

	VkPipelineLayoutCreateInfo pipelineLayoutInfo{};
	pipelineLayoutInfo.sType = VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
	pipelineLayoutInfo.setLayoutCount = 1;
	pipelineLayoutInfo.pSetLayouts = &descriptorSetLayout;

	VkResult result = vkCreatePipelineLayout(renderer->device, &pipelineLayoutInfo, nullptr, &layout);
	if (result != VK_SUCCESS) {
		throw std::runtime_error("Failed to create pipeline layout! Error code: "+std::to_string(result));
	}

	VkGraphicsPipelineCreateInfo pipelineInfo{};
	pipelineInfo.sType = VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO;
	pipelineInfo.stageCount = 2;
	pipelineInfo.pStages = shaderStages;
	pipelineInfo.pVertexInputState = &vertexInputInfo;
	pipelineInfo.pInputAssemblyState = &inputAssembly;
	pipelineInfo.pViewportState = &viewportState;
	pipelineInfo.pRasterizationState = &rasterizer;
	pipelineInfo.pMultisampleState = &multisampling;
	pipelineInfo.pDepthStencilState = &depthStencil;
	pipelineInfo.pColorBlendState = &colorBlending;
	pipelineInfo.layout = layout;
	pipelineInfo.renderPass = renderer->renderPass;
	pipelineInfo.subpass = 0;
	pipelineInfo.basePipelineHandle = VK_NULL_HANDLE;

	result = vkCreateGraphicsPipelines(renderer->device, VK_NULL_HANDLE, 1, &pipelineInfo, nullptr, &pipeline);
	if (result != VK_SUCCESS) {
		throw std::runtime_error("Failed to create graphics pipeline! Error code: "+std::to_string(result));
	}
}

void Pipeline::createDescriptorSetLayout() {
	VkDescriptorSetLayoutCreateInfo layoutInfo{};
	layoutInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
	layoutInfo.bindingCount = static_cast<uint32_t>(uniformLayoutBinding.size());
	layoutInfo.pBindings = uniformLayoutBinding.data();

	VkResult result = vkCreateDescriptorSetLayout(renderer->device, &layoutInfo, nullptr, &descriptorSetLayout);
	if (result != VK_SUCCESS) {
		throw std::runtime_error("Failed to create descriptor set layout! Error code: "+std::to_string(result));
	}
}

void Pipeline::createUniformBuffers() {
	uniformBuffers.resize(renderer->swapChainSize);
	uniformBuffersMemory.resize(renderer->swapChainSize);

	for (size_t i = 0; i < renderer->swapChainSize; i++) {
		createBuffer(uniformBufferSizeBytes, VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT, uniformBuffers[i], uniformBuffersMemory[i], renderer->device, renderer->physicalDevice);
	}
}

Pipeline::~Pipeline() {
	vkDestroyPipeline(renderer->device, pipeline, nullptr);
	vkDestroyPipelineLayout(renderer->device, layout, nullptr);

	vkDestroyShaderModule(renderer->device, fragShaderModule, nullptr);
	vkDestroyShaderModule(renderer->device, vertShaderModule, nullptr);
	
	for (size_t i = 0; i < renderer->swapChainSize; i++) {
		vkDestroyBuffer(renderer->device, uniformBuffers[i], nullptr);
		vkFreeMemory(renderer->device, uniformBuffersMemory[i], nullptr);
	}
}

void Pipeline::bind() {
	vkCmdBindPipeline(renderer->commandBuffers[renderer->activeFrame], VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
}

void Pipeline::updateUniform(void* uniformData) {
	void* data;
	vkMapMemory(renderer->device, uniformBuffersMemory[renderer->activeFrame], 0, uniformBufferSizeBytes, 0, &data);
	memcpy(data, uniformData, uniformBufferSizeBytes);
	vkUnmapMemory(renderer->device, uniformBuffersMemory[renderer->activeFrame]);
}