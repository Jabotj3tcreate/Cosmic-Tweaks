# Cosmic Tweaks

A Fabric 1.21.11 client-side utility mod with a modular HUD, custom overlays, and gameplay toggles.

## Features planned

- Modular HUD editor
- Keystrokes
- CPS counter
- FPS display
- Ping display
- Coordinates
- Armor status
- Potion effects
- Item durability
- Clock
- Zoom
- Custom crosshair
- Toggle sprint/sneak
- Item HUD
- Direction/compass
- Scoreboard customization
- Chat customization
- Fullbright
- Freelook where technically possible
- Waypoints
- Cosmic HUD customization
- In-game settings/editor

## Quick start

1. Open the project in IntelliJ IDEA or VS Code.
2. Let Gradle download the Fabric dependencies.
3. Run the Fabric client configuration from your IDE.
4. Extend the modules under `src/main/java/com/cosmictweaks`.

## Project layout

- `src/main/java/com/cosmictweaks/CosmicTweaks.java` - Mod bootstrap
- `src/main/java/com/cosmictweaks/CosmicTweaksClient.java` - Client setup and registration
- `src/main/java/com/cosmictweaks/config/CosmicConfig.java` - Toggle/config storage
- `src/main/java/com/cosmictweaks/hud/HudRenderer.java` - HUD overlay rendering
- `src/main/resources/fabric.mod.json` - Fabric metadata
