#include <SDL2/SDL.h>

/*
Handles the renderer.
This includes making the window and the graphics initialization.
*/
class Window
{
private:
	bool run = true;
	int width, height;

	SDL_Window* window;

	void cleanup();
	void render();

	//set position
	void inline setPosition(int x,int y);

public:
	Window(int width, int height);
	~Window();

	void start();
};
