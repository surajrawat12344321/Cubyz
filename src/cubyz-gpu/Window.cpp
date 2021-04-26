

#include "Window.h"
#include "../Logger.h"
#include "../EventHandler.h"
#include <SDL2/SDL.h>
#include "../OperatingSystem.h"

Window::Window(int width, int height){
	if (SDL_Init(SDL_INIT_VIDEO|SDL_INIT_AUDIO) != 0) {
		logger::fatal(std::string("Unable to initialize SDL: ") + SDL_GetError());
		return;
	}

	window = SDL_CreateWindow("Vulkan", 0, 0, width, height, SDL_WINDOW_SHOWN | SDL_WINDOW_RESIZABLE | SDL_WINDOW_VULKAN);
	
	//centering the window
	auto displaySize = getDisplaySize(0);
	setPosition(displaySize.x/2-width/2,displaySize.y/2-height/2);
}


Window::~Window() {

}
void Window::setPosition(int x,int y){
	SDL_SetWindowPosition(window,x,y);
}

void Window::start() {
	SDL_Event event;
	while(run) { // cursed while(run)!
		while(SDL_PollEvent(&event)) {
			if(event.type == SDL_QUIT) {
				run = false;
			}
			cubyz::handleEvent(event);
		}
		render();
	}
	cleanup();
	SDL_DestroyWindow(window);
}
void Window::cleanup(){

}
void Window::render(){
	
}