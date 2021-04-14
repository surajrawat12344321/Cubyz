file(GLOB_RECURSE files CONFIGURE_DEPENDS *.cpp)

add_executable(Cubyz main.cpp ${files})


target_link_libraries(Cubyz ${VULKAN_LIB_LIST} ${GLFW3_LIBRARY} -lstdc++fs)