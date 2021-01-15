cmake_minimum_required(VERSION 3.7)

project(game_launcher LANGUAGES C CXX)

if(PROJECT_SOURCE_DIR STREQUAL PROJECT_BINARY_DIR)
    message(FATAL_ERROR "In-source builds not allowed. Please make a new build directory and run CMake from there.\n")
endif()

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED True)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

set(CMAKE_CONFIGURATION_TYPES
        "Debug"
        "Release"
        CACHE STRING "" FORCE
)

set_property(GLOBAL PROPERTY USE_FOLDERS ON)
set_source_files_properties(*.c LANGUAGE C)
set_source_files_properties(*.cc LANGUAGE CXX)
set_source_files_properties(*.cpp LANGUAGE CXX)
set_source_files_properties(*.h LANGUAGE C)
set_source_files_properties(*.hh LANGUAGE CXX)
set_source_files_properties(*.hpp LANGUAGE CXX)
set_source_files_properties(*.inl LANGUAGE CXX)

file(GLOB_RECURSE game_launcher_src
    "${CMAKE_SOURCE_DIR}/src/back/*.cpp"
    "${CMAKE_SOURCE_DIR}/src/back/*.hpp"
    "${CMAKE_SOURCE_DIR}/deps/base64/base64.cpp"
    "${CMAKE_SOURCE_DIR}/deps/curlcpp/src/*.cpp"
)

source_group(${PROJECT_NAME} ${game_launcher_src})
add_executable(${PROJECT_NAME} ${game_launcher_src})

set_target_properties(${PROJECT_NAME} PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/output/bin"
    ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/output/bin"
    PDB_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/output/bin"
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/output/bin"
)

if (MSVC)
    add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD COMMAND ${CMAKE_COMMAND} -E copy "${CMAKE_SOURCE_DIR}/deps/webview/windows/lib/webview.dll" "${CMAKE_SOURCE_DIR}/output/bin/${CMAKE_BUILD_TYPE}/webview.dll")
    add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD COMMAND ${CMAKE_COMMAND} -E copy "${CMAKE_SOURCE_DIR}/deps/webview/windows/lib/WebView2Loader.dll" "${CMAKE_SOURCE_DIR}/output/bin/${CMAKE_BUILD_TYPE}/WebView2Loader.dll")
    
    set(USE_MSVC_RUNTIME_LIBRARY_DLL OFF CACHE BOOL "" FORCE)
    set(FORCE_STATIC_VCRT ON CACHE BOOL "" FORCE)
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")

    set(CURL_INCLUDE_DIRS "${CMAKE_SOURCE_DIR}/deps/libcurl/include")
    set(CURL_LIBRARIES "${CMAKE_SOURCE_DIR}/deps/libcurl/lib/libcurl_a.lib")

    set(WEBVIEW_GUID "${CMAKE_SOURCE_DIR}/deps/webview/windows/lib/WebView2Guid.lib")
    set(WEBVIEW_LOADER "${CMAKE_SOURCE_DIR}/deps/webview/windows/lib/WebView2Loader.lib")

    set(WINDOWS_HEADERS
    	"${CMAKE_SOURCE_DIR}/deps/webview/include"
    )

    set(LIB_FILES
        "${CURL_LIBRARIES}"
        "${WEBVIEW_GUID}"
        "${WEBVIEW_LOADER}"
        "opengl32.lib"
        "kernel32.lib"
        "user32.lib"
        "gdi32.lib"
        "winspool.lib"
        "comdlg32.lib"
        "advapi32.lib"
        "shell32.lib"
        "ole32.lib"
        "oleaut32.lib"
        "uuid.lib"
        "winmm.lib"
        "Shlwapi.lib"
        "psapi.lib"
        "dsound.lib"
        "synchronization.lib"
    )
elseif(NOT APPLE)
    find_package(CURL REQUIRED)
    find_package(PkgConfig REQUIRED)
    pkg_check_modules(WEBVIEW_DEPS REQUIRED libgtk-3-0 libwebkit2gtk-4.0-37)

    set(LIB_FILES
        "${CURL_LIBRARIES}"
        "${WEBVIEW_DEPS_LIBRARIES}"
        "pthread"
        "dl"
    )
else()
    find_package(CURL REQUIRED)

    set(LIB_FILES
        "${CURL_LIBRARIES}"
        "pthread"
        "dl"
    )
endif()

if (MSVC)
	set(HEADERS
		"${CMAKE_SOURCE_DIR}/deps/json/include"
		"${CMAKE_SOURCE_DIR}/deps/base64"
	    "${CURL_INCLUDE_DIRS}"
	    "${CMAKE_SOURCE_DIR}/deps/webview"
	    "${CMAKE_SOURCE_DIR}/deps/curlcpp/include"
	    "${WINDOWS_HEADERS}"
	)
elseif(NOT APPLE)
	set(HEADERS
		"${CMAKE_SOURCE_DIR}/deps/json/include"
		"${CMAKE_SOURCE_DIR}/deps/base64"
	    "${CURL_INCLUDE_DIRS}"
	    "${CMAKE_SOURCE_DIR}/deps/webview"
	    "${CMAKE_SOURCE_DIR}/deps/curlcpp/include"
        "${WEBVIEW_DEPS_INCLUDE_DIRS}"
	)
else()
    set(HEADERS
    	"${CMAKE_SOURCE_DIR}/deps/json/include"
    	"${CMAKE_SOURCE_DIR}/deps/base64"
        "${CURL_INCLUDE_DIRS}"
        "${CMAKE_SOURCE_DIR}/deps/webview"
        "${CMAKE_SOURCE_DIR}/deps/curlcpp/include"
    )
endif()
target_include_directories(${PROJECT_NAME} PUBLIC ${HEADERS})

include(cmake/CompileDefs.cmake)
include(cmake/LinkDefs.cmake)
include(cmake/PreprocessorDefs.cmake)

if ("${CMAKE_BUILD_TYPE}" STREQUAL "Debug")
    target_compile_definitions(${PROJECT_NAME} PUBLIC ${PREPROCESSOR_FLAGS_DEBUG})
    target_compile_options(${PROJECT_NAME} PUBLIC ${COMPILE_FLAGS_DEBUG})
    target_link_options(${PROJECT_NAME} PUBLIC ${LINK_FLAGS_DEBUG})
elseif("${CMAKE_BUILD_TYPE}" STREQUAL "Release")
    target_compile_definitions(${PROJECT_NAME} PUBLIC ${PREPROCESSOR_FLAGS_RELEASE})
    target_compile_options(${PROJECT_NAME} PUBLIC ${COMPILE_FLAGS_RELEASE})
    target_link_options(${PROJECT_NAME} PUBLIC ${LINK_FLAGS_RELEASE})
else()
    message(FATAL_ERROR "Could not determine build configuration. Is currently: ${CMAKE_BUILD_TYPE}")
endif()

target_link_libraries(${PROJECT_NAME} PUBLIC "${LIB_FILES}")

add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD COMMAND ${CMAKE_COMMAND} -E copy "${CMAKE_SOURCE_DIR}/src/front/index.html" "${CMAKE_SOURCE_DIR}/output/bin/${CMAKE_BUILD_TYPE}/assets/front/index.html")
add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD COMMAND ${CMAKE_COMMAND} -E copy "${CMAKE_SOURCE_DIR}/src/front/config.json" "${CMAKE_SOURCE_DIR}/output/bin/${CMAKE_BUILD_TYPE}/assets/config.json")