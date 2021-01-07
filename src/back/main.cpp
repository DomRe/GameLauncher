#include <filesystem>
#include <fstream>
#include <functional>

#include <nlohmann/json.hpp>
#include <webview.h>

template<typename... Types>
std::tuple<Types...> parse_config(std::string_view file)
{
	nlohmann::json j = {};

	auto path = std::filesystem::path {file};
	std::ifstream input;
	input.open(path.string(), std::ifstream::in);

	input >> j;

	input.close();

	std::string title = j.at("title");
	int width         = j.at("width");
	int height        = j.at("height");
	std::string bg    = j.at("bg-url");

	return std::make_tuple(title, width, height, bg);
}

void replace(std::string& subject, const std::string& search, const std::string& replace)
{
	size_t pos = 0;
	while ((pos = subject.find(search, pos)) != std::string::npos)
	{
		subject.replace(pos, search.length(), replace);
		pos += replace.length();
	}
}

void install()
{
}

int main(int argc, char* argv[])
{
	std::ifstream ifs;
	ifs.open("assets/front/index.html");

	std::string html {(std::istreambuf_iterator<char>(ifs)), std::istreambuf_iterator<char>()};

	ifs.close();

	auto [title, width, height, bg] = parse_config<std::string, int, int, std::string>("assets/config.json");

	replace(html, "%LAUNCHER_NAME%", title);
	replace(html, "%LAUNCHER_BG_URL%", bg);

	webview::webview window {false, nullptr};

	std::function<std::string(std::string)> on_install =
	    [&](std::string params) -> std::string {
		install();
		return "";
	};

	window.bind("on_install", on_install);

	window.set_title(title);
	window.set_size(width, height, WEBVIEW_HINT_FIXED);
	window.set_html(html);
	window.run();
	window.terminate();

	return EXIT_SUCCESS;
}