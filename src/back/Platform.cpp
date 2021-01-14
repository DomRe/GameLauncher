///
/// Platform.cpp
/// See License.txt
///

#ifdef _WIN32 || _WIN64

#include <Windows.h>

#include "Windows.hpp"

namespace gl
{
	void run_process(std::string_view path)
	{
		const std::wstring wstr = std::wstring(path.begin(), path.end());

		STARTUPINFO startup_info = {sizeof(startup_info)};
		PROCESS_INFORMATION process_info;
		if (CreateProcess(wstr.c_str(), nullptr, nullptr, nullptr, TRUE, 0, nullptr, nullptr, &startup_info, &process_info))
		{
			WaitForSingleObject(process_info.hProcess, INFINITE);
			CloseHandle(process_info.hProcess);
			CloseHandle(process_info.hThread);
		}
	}
} // namespace gl

#endif