#define SDL_MAIN_HANDLED
#include <stdio.h>
#include <iostream>
#include <thread>
#include <chrono>

#include "cubyz-gpu/Window.h"
#include "Logger.h"
#include "cubyz-utils/GuiLength.h"


int main(int argc, const char* argv[]) {
	try {
		Window window(1280, 720);
		window.start();
		logger::info("Exited Normally");
	} catch(std::exception e) {
		logger::fatal(e.what());
	}
	return 0;
}
