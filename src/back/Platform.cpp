///
/// Platform.cpp
/// See License.txt
///

#ifdef _WIN32 || _WIN64

#include <Windows.h>

#include "Platform.hpp"

namespace gl
{
	const bool run_process(std::string_view path)
	{
		const auto str = static_cast<std::string>(path);

		STARTUPINFO startup_info = {sizeof(startup_info)};
		PROCESS_INFORMATION process_info;
		return CreateProcess(str.c_str(), nullptr, nullptr, nullptr, TRUE, 0, nullptr, nullptr, &startup_info, &process_info);
	}
} // namespace gl

#endif