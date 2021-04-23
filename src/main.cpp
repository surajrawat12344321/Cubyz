#include <iostream>
#include <thread>
#include <chrono>
#include "cubyz-gpu/Renderer.h"
#include "Logger.h"

int main(int argc, const char** argv) {
	try {
		renderer::run();

		logger::info("Exited Normally");
	} catch(std::exception e) {
		logger::fatal(e.what());
	}


	return 0;
}
