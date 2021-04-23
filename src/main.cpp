#include <iostream>
#include <thread>
#include <chrono>
#include "cubyz-gpu/Window.h"
#include "Logger.h"

int main(int argc, const char** argv) {
	try {
		Window window(1920, 1080);

		logger::info("Exited Normally");
	} catch(std::exception e) {
		logger::fatal(e.what());
	}


	return 0;
}
