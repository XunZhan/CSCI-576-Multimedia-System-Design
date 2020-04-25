cmake_minimum_required(VERSION 3.16)
project(hecate)

set(CMAKE_CXX_STANDARD 14)

find_package(OpenCV REQUIRED)

include_directories(${OpenCV_INCLUDE_DIRS})
include_directories(include)

add_executable(hecate
        src/hecate/gflseg.cpp
        src/hecate/hecate_highlight.cpp
        src/hecate/hecate_main.cpp
        src/hecate/hecate_thumbnail.cpp
        src/hecate/video_parser.cpp
        src/tools/hecate.cpp)

target_link_libraries(hecate ${OpenCV_LIBS})