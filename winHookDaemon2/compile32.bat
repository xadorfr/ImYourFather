@echo off
set TMPATH=%PATH%
set PATH=%PATH%;C:\MinGW\bin;C:\MinGW\msys\1.0\bin
make
set PATH=%TMPATH%