rem delete old files
cd %1%
del /s /q target
rem assembly
mvn assembly:assembly -B
exit