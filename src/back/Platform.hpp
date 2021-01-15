///
/// Platform.hpp
/// See License.txt
///

#ifndef GAMELAUNCHER_SRC_BACK_PLATFORM_HPP_
#define GAMELAUNCHER_SRC_BACK_PLATFORM_HPP_

#ifdef _WIN32 || _WIN64

#include <string_view>

namespace gl
{
	///
	/// \brief Run an application process.
	///
	/// Pauses current application.
	///
	/// \path Path to the application.
	///
	[[nodiscard]] const bool run_process(std::string_view path);
} // namespace gl

#endif
#endif