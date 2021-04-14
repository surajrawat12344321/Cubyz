#include <iostream>
#include <thread>
#include <chrono>
#include "cubyz-gpu/Renderer.h"
#include "Logger.h"

int main(int argc, const char** argv) {

	renderer::init();

	logger::debug("Info");
	logger::info("Info");
	logger::warning("Info");
	logger::error("Info");
	logger::fatal("Info");

	while(!renderer::shouldClose()) {
		std::this_thread::sleep_for(std::chrono::milliseconds(200)); // That's even longer than in Java.
		renderer::render();
	}

	renderer::cleanup();

	return 0;
}
