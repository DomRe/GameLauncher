///
/// GUI.hpp
/// See License.txt
///

#ifndef GAMELAUNCHER_SRC_BACK_GUI_HPP_
#define GAMELAUNCHER_SRC_BACK_GUI_HPP_

#include <filesystem>
#include <fstream>
#include <stdexcept>
#include <string_view>

namespace gl
{
	class GUI final
	{
	public:
		inline GUI()
		    : m_html {"<!doctype html><head></head><body></body>"}
		{
		}

		inline ~GUI() = default;

		inline void load_html(std::string_view file)
		{
			std::ifstream ifs;

			const auto path = std::filesystem::path(static_cast<std::string>(file)).string();

			ifs.open(path);
			if (ifs.good())
			{
				m_html = {(std::istreambuf_iterator<char>(ifs)),
					  std::istreambuf_iterator<char>()};
			}
			else
			{
				throw std::runtime_error("Failed to load html index.");
			}

			ifs.close();
		}

		inline void replace(const std::string& search, const std::string& replace)
		{
			std::size_t pos = 0;
			while ((pos = m_html.find(search, pos)) != std::string::npos)
			{
				m_html.replace(pos, search.length(), replace);
				pos += replace.length();
			}
		}

		[[nodiscard]] inline const std::string& html() const
		{
			return m_html;
		}

	private:
		std::string m_html;
	};
} // namespace gl

#endif