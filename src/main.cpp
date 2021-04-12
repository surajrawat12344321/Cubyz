#include <iostream>
#include <thread>
#include <chrono>
#include "cubyz-gpu/Window.h"

int main(int argc, const char** argv)
{
	window::create();
	while(!window::shouldClose) {
		std::this_thread::sleep_for(std::chrono::milliseconds(200)); // That's even longer than in Java.
		window::render();
	}
	return 0;
}
