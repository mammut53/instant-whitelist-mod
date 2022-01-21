# Instant Whitelist

Minecraft mod for **Forge** and **Fabric** which reloads the whitelist autonomously when the file is edited.

## Features
- Watches for changes in `whitelist.json` and reloads the whitelist.
- Kicks players that are trespassing if `enforce-whitelist=true` in `server.properties`.

## Use case example
Any service which can modify the server directory can now dynamically change in realtime who is whitelisted. For instance the [API](https://dashflo.net/docs/api/pterodactyl/v1/) of the [Pterodactyl Panel](https://pterodactyl.io/) can modify files, therefore you can now manage the whitelist using it.

## Download for [Forge and Fabric](https://www.curseforge.com/minecraft/mc-mods/instant-whitelist)