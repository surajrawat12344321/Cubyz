#include "GuiLength.h"
#include "../Logger.h"


GuiLength::GuiLength(GuiLength* parent){
	setParent(parent);
}
void GuiLength:: setParent(GuiLength* parent){
	//prevent circle parenting:
	GuiLength* rootParent = parent;
	while(rootParent!=this&&rootParent!=nullptr){
		rootParent = parent->parent;
	}
	if(rootParent == this)
		return logger::error("GuiLength is circling around itself.");

	this->parent = parent;
}
void GuiLength::setPercentage(float percentage){
	if(parent==nullptr)
	{
		logger::error("GuiLength has no parent.");
		return;
	}
	isPercentage = true;
	length = percentage;
}
void GuiLength::setLength(float data){
	isPercentage = false;
	length = data;
}
float GuiLength::getPercentage(){
	if(!parent)
		return length;
	return isPercentage?length:length/parent->getLength();
}
float GuiLength::getLength(){
	if(!parent)
		return length;
	return isPercentage?parent->getLength()*length:length;
}
