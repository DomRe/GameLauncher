#include <functional>

#include <base64.h>
#include <webview.h>

#include "Config.hpp"
#include "GUI.hpp"

void install()
{
}

void update()
{
}

void play()
{
}

int main(int argc, char* argv[])
{
	if (!std::filesystem::exists("assets/downloads"))
	{
		std::filesystem::create_directory("assets/downloads");
	}

	gl::Config config;
	config.load_json("assets/config.json");

	gl::GUI gui;
	gui.load_html("assets/front/index.html");
	gui.replace("%LAUNCHER_NAME%", config.title());

	std::ifstream ifs;
	ifs.open(config.bg_image(), std::ifstream::in | std::ifstream::binary | std::ifstream::ate);
	if (!ifs.good())
	{
		ifs.close();
		throw std::runtime_error("Failed to load background image.");
	}
	else
	{
		const auto size = ifs.tellg();
		char* data      = new char[size];
		ifs.seekg(0, std::ifstream::beg);
		ifs.read(data, size);
		ifs.close();

		const std::string encoded_image = "data:image/png;base64," + base64_encode(reinterpret_cast<unsigned char*>(data), size, false);
		delete[] data;

		gui.replace("%LAUNCHER_BG_DATA%", encoded_image);
		webview::webview window {false, nullptr};

		std::function<std::string(std::string)> on_exit =
		    [&](std::string params) -> std::string {
			window.terminate();
			return "";
		};

		window.bind("on_exit", on_exit);

		window.set_title(config.title());
		window.set_size(config.width(), config.height(), WEBVIEW_HINT_FIXED);
		window.set_html(gui.html());
		window.run();
		window.terminate();
	}

	return EXIT_SUCCESS;
}