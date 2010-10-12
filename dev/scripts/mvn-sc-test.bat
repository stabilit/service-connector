@echo off
call mvn-source-test-install-cmd.bat ..\..\java\sc-test

rem create sc-test
call mvn-assembly-cmd.bat ..\..\java\sc-test
