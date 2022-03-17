# SimpleLock
Simple way to lock & protect your blocks & items.

[[KR]](/Readme/README_KR.md)

## Supported blocks/items
- Chest, Trap Chest
- Barrel, Shulker Box
- Furnace, Blast Furnace, Smoker
- Dispenser, Dropper
- Hopper
- Campfire, Soul Campfire
- Lectern
- Brewing Stand
- Jukebox
- Daylight Detector
- Beehive

## How to use
1. Hold an item to lock, or face at the block to lock. Use `/lock` to lock it.

   ⚠️ If you both hold lockable item on hand & face at lockable block, item will be locked.
      (Priority : Item > Block)
2. If you want to allow a player to access your lock, use `/lock allow {playername}`.
3. If you want to deny a player to access your lock, use `/lock deny {playername}`.

   ⚠️ Denying player only works if the player is previously allowed.
4. Notification message is sent when any player accesses your locked block. You can disable/enable it by using the command `/lock notification`.

   ⚠️ This command swaps previous state. If notification was enabled, it disables it. If it was disabled, it enables it.
5. To view who is the owner of the lock and who are allowed, you can use `/lock view` while looking the locked block.


## Lock protection
1. Players who are not allowed cannot break/open locked blocks.
2. Locked blocks are immune to explosion.
3. (WIP) Locked blocks ignore redstone signal. Should be configurable in further release.

## Planned features
1. Lock for doors

## Knwon issues
1. The player is included twice in a lock's allowed player list. This does not interrupt gameplay though.