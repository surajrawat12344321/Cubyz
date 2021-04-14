#include <fstream>
#include <iostream>

#include "Macros.h"
#include "FileUtils.h"
#include "cubyz-utils/Time.h"

#include "Logger.h"

namespace logger {
	std::fstream latestLog(files::ensureExistance("./logs/latest.log"), std::ios_base::out);
	std::fstream dateLog(files::ensureExistance("./logs/"+cubyzTime::formatTime("%d-%m-%Y_%H-%M-%S")+".log"), std::ios_base::out);

	void log(std::string message, std::string type, Severity severity) {

		message = "[" + cubyzTime::formatTime("%d-%m-%Y %H:%M:%S") + "|" + type + "]" + message;
		latestLog << message << '\n';
		dateLog << message << '\n';

		// Color the message if its print in a unix terminal:
		#ifdef UNIX
		switch(severity) {
			case DEBUG:
				message = "\033[37;44m" + message;
				break;
			case INFO:
				message = "\033[37m" + message;
				break;
			case WARNING:
				message = "\033[33m" + message;
				break;
			case ERROR:
				message = "\033[31m" + message;
				break;
			case FATAL:
				message = "\033[1;4;31m" + message;
				break;
		}
		message += "\033[0m";
		#endif

		std::cerr << message << '\n';
	}
}