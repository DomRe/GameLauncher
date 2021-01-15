///
/// Config.hpp
/// See License.txt
///

#ifndef GAMELAUNCHER_SRC_BACK_CONFIG_HPP_
#define GAMELAUNCHER_SRC_BACK_CONFIG_HPP_

#include <filesystem>
#include <fstream>
#include <stdexcept>
#include <string_view>

#include <nlohmann/json.hpp>

namespace gl
{
	class Config final
	{
	public:
		inline Config()
		    : m_title {"default"}, m_width {1280}, m_height {720}, m_bg_image {""}, m_executable {""}, m_install_url {""}
		{
		}

		inline ~Config() = default;

		inline void load_json(std::string_view file)
		{
			nlohmann::json json;
			std::ifstream ifs;

			const auto path = std::filesystem::path(static_cast<std::string>(file)).string();

			ifs.open(path);
			if (ifs.good())
			{
				ifs >> json;

				m_title       = json.at("title");
				m_width       = json.at("width");
				m_height      = json.at("height");
				m_bg_image    = json.at("bg-image");
				m_executable  = json.at("executable");
				m_install_url = json.at("install-url");

				if (std::filesystem::path(m_bg_image).extension() != ".png")
				{
					ifs.close();
					throw std::runtime_error("Background image must be PNG.");
				}
			}
			else
			{
				ifs.close();
				throw std::runtime_error("Failed to load config file.");
			}

			ifs.close();
		}

		[[nodiscard]] inline const std::string& title() const noexcept
		{
			return m_title;
		}

		[[nodiscard]] inline const int width() const noexcept
		{
			return m_width;
		}

		[[nodiscard]] inline const int height() const noexcept
		{
			return m_height;
		}

		[[nodiscard]] inline const std::string& bg_image() const noexcept
		{
			return m_bg_image;
		}

		[[nodiscard]] inline const std::string& executable() const noexcept
		{
			return m_executable;
		}

		[[nodiscard]] inline const std::string& install_url() const noexcept
		{
			return m_install_url;
		}

	private:
		std::string m_title;
		int m_width;
		int m_height;
		std::string m_bg_image;
		std::string m_executable;
		std::string m_install_url;
	};
} // namespace gl

#endif