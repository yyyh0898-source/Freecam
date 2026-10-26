cmake_minimum_required(VERSION 3.22.1)
project("freecam_engine")

add_library(freecam_engine SHARED
    freecam_engine.cpp
)

find_library(log-lib log)

target_link_libraries(freecam_engine
    ${log-lib}
)
