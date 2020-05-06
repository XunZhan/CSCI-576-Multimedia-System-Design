cmake_minimum_required(VERSION 3.0.0 FATAL_ERROR)
project(CreateSynopsisImage)

set(CMAKE_CXX_STANDARD 14)

find_package(OpenCV REQUIRED)

include_directories(${OpenCV_INCLUDE_DIRS})
include_directories(include)

add_executable(CreateSynopsisImage
        src/hecate/gflseg.cpp
        src/hecate/item.cpp
        src/hecate/hecate_highlight.cpp
        src/hecate/hecate_main.cpp
        src/hecate/hecate_thumbnail.cpp
        src/hecate/video_parser.cpp
        src/tools/hecate.cpp
        src/hecate/Image.cpp)

target_link_libraries(CreateSynopsisImage ${OpenCV_LIBS})