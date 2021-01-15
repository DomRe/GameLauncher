///
/// Downloader.hpp
/// See License.txt
///

#ifndef GAMELAUNCHER_SRC_BACK_DOWNLOADER_HPP_
#define GAMELAUNCHER_SRC_BACK_DOWNLOADER_HPP_

#include <fstream>
#include <functional>
#include <ostream>
#include <string_view>

#include "curl_easy.h"
#include "curl_ios.h"
#include "curl_exception.h"

using curl::curl_easy;
using curl::curl_easy_exception;
using curl::curl_ios;
using curl::curlcpp_traceback;

using CallbackFunction = std::function<int(void*, double, double, double, double)>;

namespace gl
{
	class Downloader final
	{
	public:
		inline Downloader(std::string_view webfile)
		{
			m_url = static_cast<std::string>(webfile);
		}

		inline void write_to_file(std::string_view file)
		{
			std::ofstream ofs;
			ofs.open(static_cast<std::string>(file), std::ofstream::out | std::ofstream::trunc);

			curl_ios<std::ostream> writer {ofs};
			curl_easy easy {writer};

			easy.add<CURLOPT_URL>(m_url.c_str());
			easy.add<CURLOPT_FOLLOWLOCATION>(1L);
			easy.add<CURLOPT_NOPROGRESS>(false);
			easy.add<CURLOPT_PROGRESSFUNCTION>(m_progress_func.target<int(void*, double, double, double, double)>());

			easy.perform();
		}

		inline void set_func_callback(CallbackFunction&& func)
		{
			m_progress_func = std::move(func);
		}

	private:
		Downloader() = delete;

		std::string m_url;
		CallbackFunction m_progress_func;
	};
} // namespace gl

#endif