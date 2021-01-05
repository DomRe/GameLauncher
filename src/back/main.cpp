#include "webview.h"

int main(int argc, char* argv[])
{
	webview::webview w(true, nullptr);
	w.set_title("Minimal example");
	w.set_size(480, 320, WEBVIEW_HINT_NONE);
	w.navigate("https://en.m.wikipedia.org/wiki/Main_Page");
	w.run();
	w.terminate();
	return 0;
}