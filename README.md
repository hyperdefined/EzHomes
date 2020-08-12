# CompassTracker
[![Downloads](https://img.shields.io/github/downloads/hyperdefined/EzHomes/total?logo=github)](https://github.com/hyperdefined/EzHomes/releases) [![Donate with Bitcoin](https://en.cryptobadges.io/badge/micro/1F29aNKQzci3ga5LDcHHawYzFPXvELTFoL)](https://en.cryptobadges.io/donate/1F29aNKQzci3ga5LDcHHawYzFPXvELTFoL) [![Donate with Ethereum](https://en.cryptobadges.io/badge/micro/0x0f58B66993a315dbCc102b4276298B5Ff8895F41)](https://en.cryptobadges.io/donate/0x0f58B66993a315dbCc102b4276298B5Ff8895F41)

A homes plugin that is super simple.

For Minecraft 1.16 and above.

# Features
- Works just like any homes plugin.
- Use /home <name> to teleport to a home.
- Use /homes to list your homes.
- Use /sethome <name> to set a home.
- Use /delhome <name> to delete a home.
- Use /updatehome <name> to update a home's location.
- Use /where <home> to see a home's location.
- Set a cooldown between teleports.
- Set a limit on total homes.
- Very lightweight, super easy to use.

# Config
```yaml
# How many seconds a player has to wait before teleporting again.
teleport-cooldown: 100

# How many homes a player can have.
total-homes: 5
```

# Permissions
- ezhomes.reload
    - Reload the config.