cmake_minimum_required(VERSION 3.4.1)
include_directories (../Include ./)


add_library(
            jniinterface
            SHARED
   					com_xindawn_jni_PlatinumJniProxy.cpp 
    				H264MediaCodec.cpp 
    				sps_pps.cpp 
   					Lock.cpp 
    				videosource.cpp 
    				XinDawnMultiScreen.cpp
            )

set(libdir ${PROJECT_SOURCE_DIR}/AirPlay)


target_link_libraries(jniinterface log ${libdir}/libAirPlay.so ${libdir}/libmdnsserver.so ${libdir}/libShairplay.so android mediandk)
