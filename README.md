# Game Launcher
A launcher to help with updating and playing games.
Built with HTMl5, CSS3 and JS as a frontend, and C++ as a backend.

## Building
### Windows
You need to install CMake and git and add them to your PATH.
x64 only.

```
git clone --recursive https://github.com/DomRe/GameLauncher.git
cd GameLauncher
cmake -G "Visual Studio 16 2019" -DCMAKE_BUILD_TYPE=Release -Bbuild -H.
```

### Linux
```
sudo apt update
sudo apt install build-essential gcc curl libcurl-dev libgtk-3-0 libgtk-3-dev libwebkit2gtk-4.0-37 libwebkit2gtk-4.0-dev cmake
```
### OS X
```
brew update
brew install curl
brew install cmake
```

## Credits
