cmake_minimum_required(VERSION 3.12 FATAL_ERROR)
project(Cubyz VERSION 0.8.0 LANGUAGES CXX)
add_subdirectory(src)
cmake_minimum_required(VERSION 3.12)
set(COMPILE_FLAGS -Wall -g)



find_package(OpenGL REQUIRED)
find_package(glfw3 REQUIRED)
include_directories( ${OPENGL_INCLUDE_DIRS} ${GLFW3_INCLUDE_DIR} )

