#if __has_include(<filesystem>)
#include <filesystem>
using namespace std::filesystem;
#else
// For older compilers:
#include <experimental/filesystem>
using namespace std::experimental::filesystem;
#endif

#include "FileUtils.h"

namespace files {
	std::string ensureExistance(std::string pathString) {
		path relPath(pathString);
		relPath.remove_filename();
		create_directories(relPath);

		return pathString;
	}
}