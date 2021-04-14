#ifndef _LOGGER_H_
#define _LOGGER_H_

#include <string>

namespace logger {
	/**
	All possible severities of a message.
	These determine the color of the message in unix systems:
	DEBUG	→ blue background
	INFO	→ white
	WARNING	→ yellow
	ERROR	→ red
	FATAL	→ red, fat, underlined
	*/
	enum Severity{DEBUG, INFO, WARNING, ERROR, FATAL};
	/**
	Log a message with custom type.
	@param message Your log message
	@param type gets print before the actual message (right after the date)
	@param severity how severe the message is(which determines the color it is drawn in)
	*/
	void log(std::string message, std::string type, Severity severity);
	inline void debug(std::string message) {
		log(message, "debug", DEBUG);
	}
	inline void info(std::string message) {
		log(message, "info", INFO);
	}
	inline void warning(std::string message) {
		log(message, "warning", WARNING);
	}
	inline void error(std::string message) {
		log(message, "error", ERROR);
	}
	inline void fatal(std::string message) {
		log(message, "fatal", FATAL);
	}
}

#endif