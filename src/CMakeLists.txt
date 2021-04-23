file(GLOB_RECURSE files CONFIGURE_DEPENDS *.cpp)

include(GenerateExportHeader)

add_executable(Cubyz main.cpp ${files})

IF (MSVC)
    include_directories("C:/VulkanSDK/1.2.170.0/Include")
    include_directories("C:/Users/Thilo/Documents/Programming/Cubyz/Cubyz/libs/windows_VS_64/include")
    
    link_directories("C:/Users/Thilo/Documents/Programming/Cubyz/Cubyz/libs/windows_VS_64/libs")

    find_library(
        LIB_VULKAN 
        vulkan-1
        PATHS 
        "C:/Users/Thilo/Documents/Programming/Cubyz/Cubyz/libs/windows_VS_64/libs")
    find_library(
            LIB_SDL2 
            SDL2
            PATHS 
        "C:/Users/Thilo/Documents/Programming/Cubyz/Cubyz/libs/windows_VS_64/libs")
    target_link_libraries(Cubyz ${LIB_VULKAN} ${GLM_LIBRARY_DIRS} ${LIB_SDL2} stb_image)
Else()
    target_link_libraries(Cubyz -lvulkan ${GLM_LIBRARY_DIRS} -lSDL2 stb_image)
ENDIF() 

