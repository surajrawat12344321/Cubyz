file(GLOB_RECURSE files CONFIGURE_DEPENDS *.cpp)

add_executable(Cubyz main.cpp ${files})


target_link_libraries(Cubyz -lvulkan ${GLM_LIBRARY_DIRS} -lSDL2 stb_image)