# PengueeBot
Read Wiki and manuals

This is an automation tool, you can use it to develop
  * game bots
  * testing automation
  * things that need to be automated, but you don't know how to do it via source codes or API



CONSOLE PARAMETERS:

EXAMPLE:

#an example how to run this software in console mode using specific scriptname and pass some arguments to the script itself

java -jar PengueeBot.jar -nogui -script myscript.py -args script_arg1 script_arg2

-__nogui__                                OPTIONAL                        ##run software in console mode

-__script__ <ScriptFileName>              OPTIONAL, DEFAULT ./script.py   ##run specified script
  
-__forceUseGPU__                          OPTIONAL                        ##force use GPGPU to search for fragments on screen. (Alpha test)

-__fragments__ <PATH>                     OPTIONAL, DEFAULT "frag"        ##set custom fragments directory
  
-__args__ <SCRIPT_ARG!> <SCRIPT_ARG2>...  OPTIONAL                        ##Here you can set arguments you want to be passed to script. Set this at THE END ONLY.

Linux/Unix systems require to specify full path to .jar package, otherwise fragments being misloaded from user home directory

Some useful features

Right mouse click can show you some additional options in GUI
![](https://puu.sh/EZ4ge/0a5480bb95.gif)


Use arrow keys to move selection rectangle
CTRL+ arrow keys will modify rectangles' bounds
Use SHIFT if you want to speedup
![](https://puu.sh/EZ4is/6bbe8b44ab.gif)


Don't forget about MONO fragmens, they can save you alot of time when dealing with changing dackgrounds
![](https://puu.sh/EZ4qk/a8c0b0a3db.gif)
