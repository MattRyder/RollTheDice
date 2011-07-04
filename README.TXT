===========================
Roll The Dice v0.2
===========================

A luck-based plugin for CraftBukkit.

FEATURES:
 * Randomly generated Items!
 * Intuitive configuration!
 * Permissions Support!
 * Preset your stacks or randomly generate!

USAGE: /roll

PERMISSION NODES (For Permissions):
 * For one-shot use: rollthedice.roll
 * For multiple use: rollthedice.roll.multi


HOW TO (INSTALL):
* Copy RollTheDice.JAR to the Bukkit Server's /plugin directory.
* Start or Reload the server.

A working RTD plugin will show:
  - [INFO] Roll The Dice - v0.x (Matt Ryder) [ENABLED]

HOW TO (COMPILE):
* Install/Load Netbeans or any good Java IDE.
* Perform edits to RollTheDice.java (optional)
* Perform a Clean & Build to obtain RollTheDice.JAR in /dist


TROUBLESHOOTING:
Q: How do I edit the config.dat?
A: The input is in the format [Material]:[Amount] ( i.e. Diamond:10 )
   The material has to be a valid Bukkit/Minecraft name (i.e. "Dirt" or "Snow")
   The amount can be between 1 and 64.

Q: Can you supply more info about the nodes?
A: The 'rollthedice.roll' will enable the user one use/roll of the command per login.
   They would need to logout and back in to be applicable for another shot.
   The 'rollthedice.roll.multi' will enable the user an infinite amount of uses per login.
   I would advise limited use of this permission, save Admins/Moderator/Trusted Players.

Q: I've been told to configure the plugin, what does this mean?
A: There's no items in the config.dat file, either remove the config.dat and reload the server,
   or add some items manually to the config.dat.

Q: I've hit an error and/or unhanded exception! How do I contact you to fix this?
A: My contact information is at the bottom of this README. Mail me a good explanation please!


CONTACT INFO:
 * EMAIL: matt@mattryder.co.uk
 * TWITTER: @CSMattRyder


This application is released under the GNU GPLv3.0, a copy should be bundled with every release of this product.
If not, the text is located here: http://www.gnu.org/licenses/gpl-3.0.txt