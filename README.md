# Game Launcher
[![Windows](https://ci.appveyor.com/api/projects/status/ou9ym965cwmcmldm?svg=true)](https://ci.appveyor.com/project/reworks/gamelauncher)
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
cd build
msbuild game_launcher.sln
```

### Linux
```
Coming soon.
```
### OS X
```
Coming soon.
```