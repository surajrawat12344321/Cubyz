#include "OperatingSystem.h"


Vector2i getDisplaySize(int displayID)
{
	SDL_DisplayMode DM;
	SDL_GetCurrentDisplayMode(displayID, &DM);
	return Vector2i(DM.w,DM.h);
}