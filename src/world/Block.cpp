#include "Block.h"

// Initialze static member variables. There will probably be more than 128 blocks, so I can just initialize it to that already.
std::vector<int> BlockType::blockClasses(128);
std::vector<float> BlockType::hardnesses(128);
std::vector<std::string> BlockType::registryIDs(128);
std::vector<int> BlockType::emittedLightColors(128);
std::vector<int> BlockType::absorbedLightColors(128);
std::vector<int> BlockType::lodColors(128);
std::vector<RotationMode*> BlockType::rotationModes(128);
std::vector<bool> BlockType::solids(128);

BlockType::BlockType(std::string registryID, int blockClass, float hardness, int emittedLightColor, int absorbedLightColor, int lodColor, RotationMode* rotationMode, bool solid) {
	runtimeID = blockClasses.size();
	registryIDs.push_back(registryID);
	blockClasses.push_back(blockClass);
	hardnesses.push_back(hardness);
	emittedLightColors.push_back(emittedLightColor);
	absorbedLightColors.push_back(absorbedLightColor);
	lodColors.push_back(lodColor);
	rotationModes.push_back(rotationMode);
	solids.push_back(solid);
}

BlockType::BlockType(int runtimeID) :
	runtimeID(runtimeID) {
}

Block::Block(BlockType type) :
	Block(type, type.rotationMode()->defaultBlockData) {
}

Block::Block(BlockType type, unsigned char data) {
	data = (type.runtimeID) | ((unsigned int)data << 24);
}

BlockType Block::getBlockType() {
	return BlockType(data & 0x00ffffff);
}

unsigned char Block::getBlockData() {
	return (unsigned char)(data >> 24);
}