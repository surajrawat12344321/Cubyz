#include <iostream>
#include <thread>
#include <chrono>
#include "cubyz-gpu/Window.h"
#include "Logger.h"

int main(int argc, const char** argv) {

	window::create();

	logger::debug("Info");
	logger::info("Info");
	logger::warning("Info");
	logger::error("Info");
	logger::fatal("Info");

	while(!window::shouldClose()) {
		std::this_thread::sleep_for(std::chrono::milliseconds(200)); // That's even longer than in Java.
		window::render();
	}
	return 0;
}
