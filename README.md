# State-Royale
My first proper GitHub project, I guess. Basically an automated version of the "Battle Royale" short series on Geo Facts' YouTube channel.
**Please credit this repo when you use this program! And don't change the watermark!**

## Prerequisites:
- Java Runtime Environment 16
    - [All downloads](https://www.oracle.com/java/technologies/javase-jdk16-downloads.html)
    - Also works with [OpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk16&jvmVariant=hotspot), guys
- FFmpeg
    - [Windows (this guy explains it well)](https://www.youtube.com/watch?v=r1AtmY-RMyQ)
    - macOS: `brew install ffmpeg` (needs [Homebrew](https://brew.sh/))
    - Linux: use package manager.

## HOW TO USE

In the repo, there are base "mode" files that can be used. (ex: USA.zip) Grab one you like and place it in the same directory as the jar. With a little bit of know-how, you can also create your own modes using mem2.txt.

Use the jar as follows:

`java -jar State-Royale-1.0.jar <args>`

### COMMANDS (args)

**"-rm" (main command)**

To remove all remaining regions in a save file:

`startsave.zip -rm video.mp4`
.mov also works.

To remove a set number of random regions:

`startsave.zip endsave.zip video.mp4 -rm [number]`

To remove specific regions in a randomized order:

`startsave.zip endsave.zip video.mp4 -rm [region1] [region2] [and] [so] [on]`

To remove specific regions in that order:

`startsave.zip endsave.zip video.mp4 -rm! [region1] [region2] [and] [so] [on]`

**"-pl"**

Same as -rm! above, but with the pedestal/medal animation instead of the slot machine one. Ideal for polling, hence the name.

`startsave.zip endsave.zip video.mp4 -pl [region1] [region2] [and] [so] [on]`

**"-ls"**

Prints out a list of regions remaining in a save file to a .txt.

`startsave.zip -ls list.txt`

#### Ending arguments

**"-oo"**

Skips all rewrite prompts, and overwrites any conflicting files.

`startsave.zip endsave.zip video.mp4 -rm 4 -oo`

**"-no"**

Skips all rewrite prompts, and does NOT overwrite any conflicting files.

`startsave.zip endsave.zip video.mp4 -rm 4 -no`

**"-[w]x[h]"**

Overrides the default video resolution of 720x720. Always place this at the very end of your command.

`startsave.zip endsave.zip video.mp4 -rm 4 -1920x1080`

With another ending arg:
`startsave.zip endsave.zip video.mp4 -rm 4 -oo -1920x1080`

## CREDITS

Political map data is provided by the GADM project.

Global Administrative Areas (boundaries). University of Berkeley, Museum of Vertebrate Zoology and the International Rice Research Institute (2012).

Elevation data is provided by the Wolfram Knowledge base with data from © OpenStreetMap contributors.

Wolfram Research, Inc., Wolfram|Alpha Knowledgebase, Champaign, IL (2021).
Map data from Wolfram Knowledgebase with data from © OpenStreetMap contributors: http://www.openstreetmap.org/copyright

Satellite imagery is provided by NASA's "Blue Marble Next Generation" Visible Earth project. The world image from August 2004 is used.

The Pixeled font was made by [OmegaPC777 on Dafont.com.](https://www.dafont.com/omegapc777.d6598)

The KdTree implementation was created by [Jilocasin on GitHub.](https://github.com/Jilocasin/nearest-neighbour)

