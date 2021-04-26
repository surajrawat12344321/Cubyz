#pragma once 
#include <iostream>
/*
	Stores a length,which is either absolute or relative to another length.
*/
class GuiLength
{
private:
	bool isPercentage = false;
	float length;
	GuiLength* parent = nullptr;
public:
	GuiLength(GuiLength* parent = nullptr);
	
	void setParent(GuiLength* parent);

	
	void setLength(float length);
	void setPercentage(float percentage);

	float getPercentage();
	float getLength();
};