#include <string>

namespace files {
	/**
	Checks if all directories in the path exist and creates them if necessary.
	@param path The path leading up to and including a file.
	@return path
	*/
	std::string ensureExistance(std::string path);
}