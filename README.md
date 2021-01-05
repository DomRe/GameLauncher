# Game Launcher
[![Windows](https://ci.appveyor.com/api/projects/status/ou9ym965cwmcmldm?svg=true)](https://ci.appveyor.com/project/reworks/gamelauncher)
[![Linux & OS X](https://travis-ci.org/DomRe/GameLauncher.svg?branch=master)](https://travis-ci.org/DomRe/GameLauncher)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

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
