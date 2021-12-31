![GitHub all releases](https://img.shields.io/github/downloads/pixer415/State-Royale/total?color=blue&style=plastic) ![GitHub issues](https://img.shields.io/github/issues/pixer415/State-Royale?color=red&style=plastic) ![GitHub closed issues](https://img.shields.io/github/issues-closed-raw/pixer415/State-Royale?style=plastic) ![GitHub](https://img.shields.io/github/license/pixer415/State-Royale?color=yellow&style=plastic)

# State-Royale

![Various examples of program output](https://cdn.discordapp.com/attachments/926256179276034080/926260495256793138/topexamples.png)

<h3 align="left">Made by <a href="https://twitter.com/pixer415">Pixer415</a>, with some help from <a href="https://t1c.dev">ThatOneCalculator</a></h3>
<a href="https://twitter.com/pixer415" target="blank"><img src="https://shields.io/badge/follow-@pixer415-1DA1F2?logo=twitter&style=for-the-badge" alt="twitter"/></a>
<a href="https://voring.me/@thatonecalculator" target="blank"><img src="https://shields.io/badge/follow-@thatonecalculator-3088D4?logo=mastodon&style=for-the-badge" alt="voring.me (mastodon)"/></a>

This project needs your contributions. New modes/new features/typo fixes/suggestions/maintenance, etc. are welcomed. 
All contributors are listed [here.](https://github.com/pixer415/State-Royale/graphs/contributors)

<b>Please credit this repository when you use this program! If you like it, consider leaving a star on this repo!</b>

Examples of program video output are linked [here.](https://github.com/pixer415/State-Royale/blob/main/EXAMPLES.md)

## Explanation

State-Royale is an interactive nation-conquering simulator.

The program aims to automatically generate entertainment simulation videos 
based on the "Battle Royale" series of short videos created by the YouTube channel GeoFacts(LINK TO CHANNEL)
Examples of these videos are provided here.(link to playlist of the shorts series)

The purpose of this program is to create a simulation in which all of the regions in a geographic area (for example, the 50 states of the United States)
go head to head in an elimination battle. Each region is eliminated at random (or at the behest of a user), one by one. 
Eliminating a region is accomplished by geographically partitioning it amongst the regions that surround it. 
This process continues until a single region rules the entire area.

<i>Command to recreate the output shown below:</i> `java -jar State-Royale-1.0.1.jar SouthAmerica.zip save.zip video.mp4 -rm Argentina`

Each output video begins with an overview map of a geographic area, either from one of the base files provided or a user-generated save file.

example-1

One of the regions then is selected either randomly or by the user. In this case, Argentina was selected by the user.

example-2

The region is then shown being partitioned and removed.

example-3

An overview of the current situation is then shown.

example-4

This process, again, can continue until one region remains on the map and it rules the area.

Instructions
Prerequisites:
Java Runtime Environment 16
All downloads
Also works with OpenJDK
FFmpeg
Windows: instructions
macOS: brew install ffmpeg (needs Homebrew)
Linux: use package manager.
Getting started:
To get started, download the latest jar from the releases page.



Then, go here and download one of the base area .zip files, or input.zip as it will henceforth be known. Place it in the same directory as the .jar file.



CLI usage:
java -jar State-Royale-[version].jar <flags> The flags and the example commands that use them are listed below. Replace [version] with whatever's the version number (ex: State-Royale-1.0.1.jar)

Flags:
-rm: Eliminates either: 
     - a set number of remaining regions, chosen randomly
     - regions specified by name in a randomized order 
     - regions specified by name in the order presented (use rm!)
     - all of the remaining regions in a save file

     Examples:

     java -jar State-Royale-[version].jar input.zip output.zip video.mp4 -rm <integer arg>
     java -jar State-Royale-[version].jar input.zip output.zip video.mp4 -rm <region1> <region2> <and> <so> <on>
     java -jar State-Royale-[version].jar input.zip output.zip video.mp4 -rm! <region1> <region2> <and> <so> <on>
          // must be invoked after the input, output, and video files, in that order

     java -jar State-Royale-[version].jar input.zip -rm video.mp4 
          // must be invoked before the video file

-pl: Similar to -rm!, but uses the "pedastal" animation for video.mp4 instead of the default 
     "slot machine" one. Ideal for poll results. 

     Example:

     java -jar State-Royale-[version].jar input.zip output.zip video.mp4 -pl <region1> <region2>
          // must be invoked after the input, output, and video files, in that order

-ls: Creates a .txt containing a comma-separated list of regions remaining in a .zip file. 

     Example:

     java -jar State-Royale-[version].jar input.zip -ls list.txt
          // must be invoked before the text file

-oo: Overrides any overwrite prompts, and overwrites any conflicting files. 

     Example:

     java -jar State-Royale-[version].jar input.zip -ls list.txt -oo
          // must be invoked at the very end of the command

-no: Overrides any overwrite prompts, and does NOT overwrite any conflicting files. 

     Example:

     java -jar State-Royale-[version].jar input.zip -ls list.txt -no 
          // must be invoked at the very end of the command

-[x]x[y]: Overrides the default video resolution of 720x720 for -rm and -pl. 

     Example:

     java -jar State-Royale-[version].jar input.zip -rm video.mp4 -1920x1080
          // must be invoked at the very end of the command; must be invoked after -oo or -no if they are used.

-help: Displays this CLI usage guide. This guide also appears when no args are present. 
 
     Example:

     java -jar State-Royale-[version].jar -help
          // must be invoked as the only arg in the command
(input.zip was mentioned earlier in this readme.)

(output.zip is the save file that will be generated, saving your progress on the current battle royale; it can also be used as an input.zip once it is created.)

(video.mp4 is the entertainment simulation video that will be generated based on the regional eliminations selected. Examples of the content seen in these videos are shown in EXAMPLES.md.)

(An "ffmpeg_output_msg.txt" file will also be created next to the .jar. This is the console output of ffmpeg.)

TO-DO
Make an actually good CLI interface using org.apache.commons.cli
Add a GUI element of some sort
Add display options for the output videos
CREDITS
Political map data is provided by the GADM project, version 2.8.

Global Administrative Areas (boundaries). University of Berkeley, Museum of Vertebrate Zoology and the International Rice Research Institute (2012).

Elevation data is provided by the Wolfram Knowledge base with data from © OpenStreetMap contributors.

Wolfram Research, Inc., Wolfram|Alpha Knowledgebase, Champaign, IL (2021). Map data from Wolfram Knowledgebase with data from © OpenStreetMap contributors: http://www.openstreetmap.org/copyright

Real-ESRGAN was used to upscale the elevation image.

Satellite imagery is provided by NASA's "Blue Marble Next Generation" Visible Earth project. The world image from August 2004 is used.

The Pixeled font was made by OmegaPC777 on Dafont.com.

The KdTree implementation was created by Jilocasin on GitHub.
