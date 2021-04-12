file(GLOB_RECURSE files CONFIGURE_DEPENDS *.cpp)

add_executable(Cubyz main.cpp ${files})


target_link_libraries(Cubyz ${OPENGL_LIBRARIES} ${GLFW3_LIBRARY})