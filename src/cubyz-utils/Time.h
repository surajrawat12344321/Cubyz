#include <ctime>
#include <string>

namespace cubyzTime {
	/**
		Formats the current time into a given format.
		@param format the time format(see https://en.cppreference.com/w/cpp/chrono/c/strftime for a list of all possibilities)
		@return formatted string
	*/
	inline std::string formatTime(const char* format) {
		time_t rawtime;
		struct tm * timeinfo;
		char buffer[80];

		time (&rawtime);
		timeinfo = localtime(&rawtime);

		strftime(buffer,sizeof(buffer), format,timeinfo);
		std::string str(buffer);
		return str;
	}
}