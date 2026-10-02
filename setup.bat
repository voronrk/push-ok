@echo off
chcp 65001 >nul
echo Creating MicroPlanner project structure...

REM Gradle wrapper folder
mkdir gradle\wrapper

REM App source folders
mkdir app\src\main\java\com\example\microplanner\data
mkdir app\src\main\java\com\example\microplanner\domain\model
mkdir app\src\main\java\com\example\microplanner\ui\theme
mkdir app\src\main\java\com\example\microplanner\ui\home
mkdir app\src\main\java\com\example\microplanner\ui\tasks
mkdir app\src\main\java\com\example\microplanner\ui\edit
mkdir app\src\main\java\com\example\microplanner\ui\settings
mkdir app\src\main\res\drawable
mkdir app\src\main\res\values
mkdir app\src\main\res\mipmap-hdpi
mkdir app\src\test\java\com\example\microplanner

echo Done! Project structure created.
pause