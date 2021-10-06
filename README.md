# SimpleLock
Simple way to lock & protect your blocks & items.

## Supported blocks/items
- Chest, Trap Chest, Barrel, Shulker Box
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

## Lock protection
1. Players who are not allowed cannot break/open locked blocks.
2. Locked blocks are immune to explosion.
3. (WIP) Locked blocks ignore redstone signal. Should be configurable in further release.