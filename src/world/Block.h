#pragma once

#include <map>
#include <string>
#include <vector>

/**
 * @brief An interface for interpreting the block data of each block in the world.
 * Used for rotation, connecting blocks or automatic model generation.
 * TODO: Add all the missing stuff.
 */
class RotationMode {
public:
	/** The default block data value for newly constructed blocks. */
	const unsigned char defaultBlockData = 0;

	/** Whether the block data needs to be updated when neighboring blocks change. */
	const bool dependsOnNeighbors = false;
};

/**
 * @brief Contains the index to the properties of a BlockType.
 * Internally it is handled using an ECS-structure.
 * I chose an ECS-like structure for the following reasons:
 * a) I don't need to worry about pointers.
 * b) All the data is in one place and could be trasmitted easily through the web.
 * c) More cache locality.
 */
class BlockType {
private: // The static data vectors:

	/** stone, wood, … */
	static std::vector<int> blockClasses;
	/** How long it takes to break the block. (In seconds by hand) */
	static std::vector<float> hardnesses;

	/** How this will be accessed in the data files and in the console. */
	static std::vector<std::string> registryIDs;

	/** What color the emitted light has. */
	static std::vector<int> emittedLightColors;
	/** What and how much light gets absorbed. */
	static std::vector<int> absorbedLightColors;
	/** How this block should look far away. */
	static std::vector<int> lodColors;

	/** How the block data gets interpreted. */
	static std::vector<RotationMode*> rotationModes;

	/** Whether the block can interact with entities or fluids. */
	static std::vector<bool> solids;


public:
	/** Constructs a block type with a known ID. */
	BlockType(int runtimeID);

	/** Registers an entirely new BlockType. Only do this once for each BlockType! */
	BlockType(std::string registryID, int blockClass, float hardness, int emittedLightColor, int absorbedLightColor, int lodColor, RotationMode* rotationMode, bool solid);

	/** TODO: constructor from data file. */

	/** Just the index for the respective data arrays. */
	int runtimeID;

	/** stone, wood, … */
	inline int blockClass() {
		return blockClasses[runtimeID];
	}
	/** How long it takes to break the block. (In seconds by hand) */
	inline float hardness() {
		return hardnesses[runtimeID];
	}

	/** How this will be accessed in the data files and in the console. */
	inline std::string registryID() {
		return registryIDs[runtimeID];
	}

	/** What color the emitted light has. */
	inline int emittedLightColor() {
		return emittedLightColors[runtimeID];
	}
	/** What and how much light gets absorbed. */
	inline int absorbedLightColor() {
		return absorbedLightColors[runtimeID];
	}
	/** How this block should look far away. */
	inline int lodColor() {
		return lodColors[runtimeID];
	}

	/** How the block data gets interpreted. */
	inline RotationMode* rotationMode() {
		return rotationModes[runtimeID];
	}

	/** Whether the block can interact with entities or fluids. */
	inline bool solid() {
		return solids[runtimeID];
	}

};

class Block {
private:
	/** Contains the block type in the lower 24 bits and the block data in the upper 8 bits. */
	int data;

public:
	/** Creates the block data from the default value. */
	Block(BlockType type);
	Block(BlockType type, unsigned char data);
	BlockType getBlockType();
	unsigned char getBlockData();
};