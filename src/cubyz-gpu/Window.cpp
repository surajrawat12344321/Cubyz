#include "Window.h"
#include "../Logger.h"
#include "../EventHandler.h"
#include <SDL2/SDL.h>

Window::Window(int width, int height){
	if (SDL_Init(SDL_INIT_VIDEO|SDL_INIT_AUDIO) != 0) {
		logger::fatal(std::string("Unable to initialize SDL: ") + SDL_GetError());
		return;
	}

	window = SDL_CreateWindow("Vulkan", 0, 0, width, height, SDL_WINDOW_SHOWN | SDL_WINDOW_RESIZABLE | SDL_WINDOW_VULKAN);
	
}


Window::~Window() {

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
}
void Window::cleanup(){

}
void Window::render(){
	
}