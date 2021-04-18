cmake_minimum_required(VERSION 3.12 FATAL_ERROR)
project(Cubyz VERSION 0.8.0 LANGUAGES CXX)
cmake_minimum_required(VERSION 3.12)
set (CMAKE_CXX_STANDARD 17)

add_subdirectory(include)
add_subdirectory(src)
set(SFML_DIR "<sfml root prefix>/lib/cmake/SFML")
set(COMPILE_FLAGS -Wall -g)


find_package(Vulkan REQUIRED)
find_package(glfw3 REQUIRED)
find_package(glm REQUIRED)
find_package(SDL2 REQUIRED)
include_directories( ${VULKAN_INCLUDE_DIRS} ${SDL2_INCLUDE_DIRS} ${GLM_INCLUDE_DIRS} )

