@echo off
set TMPATH=%PATH%
set PATH=%PATH%;C:\mingw64\bin
make
set PATH=%TMPATH%