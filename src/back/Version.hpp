///
/// Version.hpp
/// See License.txt
///

#ifndef GAMELAUNCHER_SRC_BACK_VERSION_HPP_
#define GAMELAUNCHER_SRC_BACK_VERSION_HPP_

#include <filesystem>
#include <fstream>
#include <stdexcept>
#include <string_view>

#include <nlohmann/json.hpp>

namespace gl
{
	class Version final
	{
	public:
		inline Version()
		    : m_version {0.0}, m_url {""}
		{
		}

		inline ~Version() = default;

		inline void load_json(std::string_view file)
		{
			nlohmann::json json;
			std::ifstream ifs;

			const auto path = std::filesystem::path(static_cast<std::string>(file)).string();

			ifs.open(path);
			if (ifs.good())
			{
				ifs >> json;

				m_version = json.at("version");
				m_url     = json.at("update-url");
			}
			else
			{
				ifs.close();
				throw std::runtime_error("Failed to load config file.");
			}

			ifs.close();
		}

		[[nodiscard]] inline const double version() const noexcept
		{
			return m_version;
		}

		[[nodiscard]] inline const std::string& url() const noexcept
		{
			return m_url;
		}

	private:
		double m_version;
		std::string m_url;
	};
} // namespace gl

#endif