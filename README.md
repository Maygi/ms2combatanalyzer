# ms2combatanalyzer
This is an overlay designed to run with **Maplestory2** in the background, that parses useful combat information using the [Sikuli API](http://doc.sikuli.org/). It collects data by essentially taking screenshots and looking for text or images - no intrusive memory reading or packet sniffing included!

![The MSCA Overlay](https://media.giphy.com/media/RkHuicEN5HldWz6sYO/giphy.gif)
![An example parse](https://i.imgur.com/eEpsxFN.png)

# How to use
Currently, MSCA requires that you play in fullscreen mode. Due to how it collects data, note that obstructing your UI at any time may lead to inaccurate results. It was made for 1920x1080 resolution but should (keyword: should) work in different resolutions as long as you're in **fullscreen**. Additionally, you must go to your game settings and make sure **Interface Size is at 50%**. Lastly, this overlay only parses encounters with **boss enemies**.

**IMPORTANT!!**
Nexon has not given their OFFICIAL stance on this yet. I have run the program by them and the best reply I've gotten so far was:

> We truly appreciate your efforts toward the game. We would like to inform you that as long as you didn't violate any of the TOS, you will not receive any sanctions. I assure you that the game team are reviewing your inquiry.

I have gone over the [Terms of Use](https://www.nexon.com/main/en/legal/tou) and [End User License Agreement](http://www.nexon.net/legal/end-user-license-agreement/) many times and concluded that this overlay should be compliant. However, because Nexon hasn't given official word on it yet, **use of this program is at your own risk** >_>

But, with that in mind: to use MSCA, it's simple! Just follow these easy steps.

1. Go to the [releases](https://github.com/Maygi/ms2combatanalyzer/releases) and download the latest version at the bottom. It will be a .zip or .gz file.
2. Extract the MSCA zip folder.
3. Run the **MSCA.jar** file.
4. That's it!

*Not working?*
Scroll to the bottom for some common mistakes that people make~

# Features
* Parses total party DPS
* Parser only starts when entering combat with a boss
* User can reset / pause the parse
* Calculates uptime
  * Personal buffs (Celestial Guardian, Iron Defense, etc)
  * Personal debuffs (Celestial Light, Shadow Chaser, etc)
  * Party buffs (Celestial Blessings, Focus Seal, etc)
  * Party debuffs (Smiting Aura, Shield Toss, etc)
* Estimates damage contribution
  * Smiting Aura
  * Shield Toss
  * Mark of Death
  * Static Flash
  * Holy Symbol
* Sound triggers
  * Varrekant's wings
  * Blue bomb debuff in Wrath of Infernog

# Notes
* All calculations are estimates! They may not be 100% accurate.
* Current debuff contribution calculations calculate contribution as if all debuffs stack multiplicatively
 with each other, when Smite/MOD and Static Flash/Shield Toss do not
* Holy Symbol contribution calculation is a work in progress and may be even more inaccurate. As such, I've provided a metric for the total damage dealt under Holy Symbol as well
* If you cast Holy Symbol within the first minute or so, the Holy Symbol damage contribution won't appear for another minute. This is intended; the program needs to see around one minute of combat without Holy Symbol to estimate how much damage the party is doing normally
* Pausing when in combat with a boss won't do anything, because it'll automatically start up instantly. Pause is only useful for when the encounter is finished, or you leave early.

# Upcoming Features
* Add an option for toggling sound triggers
* Add an option to change the UI color (I'm sure not everyone likes pink as much as I do...)
* Save the UI's position in the screen so it launches in the same place when reloading
* Improve calculations
* Add a feature for estimating the clear time
* other stuffs... we'll see :3 

# HELP! IT'S NOT WORKING!

*If the overlay is collecting no data...*
* Are you in fullscreen? It only works in fullscreen :c
* Is your interface size 50%? You can change this in the game settings.
* Are you fighting a boss monster? It only parses bosses!

*If the overlay looks like this...*
![Broken overlay](https://i.imgur.com/sitE9Q0.png)
* Did you download the entire folder? You'll need the resource folders - namely, MSCA_lib, fonts, images, and sound - to get the overlay to work properly. MSCA.jar should be in a folder outside those.
* Did you open it with Java? Some people have jar folders to open with WinZip by default. Right-click the .jar file and make sure to open with Java. If you don't have it, you can [download it from the Oracle website.](https://www.java.com/en/download/)
