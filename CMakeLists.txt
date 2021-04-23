cmake_minimum_required(VERSION 3.12 FATAL_ERROR)
project(Cubyz VERSION 0.8.0 LANGUAGES CXX)
cmake_minimum_required(VERSION 3.12)
set (CMAKE_CXX_STANDARD 17)

find_package(Vulkan REQUIRED)
IF (WIN32)
    include_directories("C:/VulkanSDK/1.2.170.0/Include")
    include_directories("C:/Users/Thilo/Documents/Programming/Cubyz/Cubyz/libs/windows_VS_64/include")
    link_directories("C:/Users/Thilo/Documents/Programming/Cubyz/Cubyz/libs/windows_VS_64/libs")
ELSE()
    find_package(SDL2 REQUIRED)
    find_package(glm REQUIRED)
ENDIF()


add_subdirectory(include)
add_subdirectory(src)
set(SFML_DIR "<sfml root prefix>/lib/cmake/SFML")
set(COMPILE_FLAGS -Wall -g)



include_directories( ${VULKAN_INCLUDE_DIRS} ${SDL2_INCLUDE_DIRS} ${GLM_INCLUDE_DIRS} )

