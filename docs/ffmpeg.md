# How to install FFMPEG on Windows

### For this guide, you will need:

- A computer running Windows 7 or later
- Administrator permissions on said computer
- 7-Zip [![](https://shields.io/badge/Download-7zip-fff?&logo=windows-95&style=flat-square&logoColor=fff)]((https://www.7-zip.org/))

### Step 1

- Download the latest gyandev build of FFMPEG [here](https://www.gyan.dev/ffmpeg/builds/ffmpeg-git-full.7z). [![](https://shields.io/badge/Download_gyan.dev's-FFMPEG-007808?&logo=ffmpeg&style=flat-square&logoColor=007808)](https://www.gyan.dev/ffmpeg/builds/ffmpeg-git-full.7z)

### Step 2

- Extract the downloaded .7z file (most likely in your `Downloads` folder)
- Rename the extracted folder to `ffmpeg`
- Move the folder to the root of the `C:\` drive
  - This can be found in `This PC > Local Disk (C:)`

### Step 3

- Hit the ![](https://shields.io/badge/-fff?&logo=windows&logoColor=111) button on your keyboard

- Type in `cmd` and click "Run command prompt as Administrator"

- Copy this:

  ````bat
  setx /m PATH "C:\ffmpeg\bin;%PATH%"
  ````

- Paste it into the prompt and hit the <kbd>Enter</kbd> button on your keyboard

### Step 4

- Reboot your computer

### Step 5.

- Hit the ![](https://shields.io/badge/-fff?&logo=windows&logoColor=111)button on your keyboard
- Type in `cmd` and hit the <kbd>Enter</kbd> button on your keyboard
- Type in `ffmpeg --version` into the prompt and hit the <kbd>Enter</kbd> button on your keyboard
- If it shows something similar to the screenshot below, congratulations, you've installed FFMPEG! :tada: 

![verifying ffmpeg installation on windows](https://media.geeksforgeeks.org/wp-content/uploads/20210912212115/Screenshotfrom20210912212044.png)
