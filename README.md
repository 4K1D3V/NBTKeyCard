# NBTKeyCard

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.4-brightgreen.svg)
![Spigot API](https://img.shields.io/badge/Spigot_API-1.21-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![GitHub Issues](https://img.shields.io/github/issues/4K1D3V/NBTKeyCard)

Welcome to **NBTKeyCard**, a cutting-edge Spigot plugin designed to secure your Minecraft mines with NBT-tagged key cards! Crafted with passion by Kite, this plugin offers a seamless and immersive way to manage access to exclusive mining areas. Whether you're running a bustling server or a private world, NBTKeyCard brings advanced features, top-notch performance, and rock-solid stability to your gameplay.

🌐 **Website**: [ks.akii.pro](https://ks.akii.pro)  
📬 **Issues**: [Report a Bug](https://github.com/4K1D3V/NBTKeyCard/issues)

---

## Features

- **Secure Mine Access**: Restrict entry to mines using custom NBT-tagged key cards.
- **Multi-World Support**: Define mines across different worlds (e.g., `world`, `world_nether`).
- **Economy Integration**: Purchase key cards via an in-game shop with Vault support.
- **Temporary Key Cards**: Create key cards with expiration times or usage limits.
- **Dynamic Mine Management**: Add new mines in-game with intuitive commands.
- **Visual Feedback**: See mine boundaries with particle effects and receive denial notifications with sounds and particles.
- **Performance Optimized**: Built-in caching and event optimizations for lag-free operation.
- **Customizable**: Configure messages and mine settings to match your server’s style.

---

## Installation

1. **Prerequisites**:
   - **Minecraft Server**: Spigot 1.21.4
   - **Dependencies**: [Vault](https://www.spigotmc.org/resources/vault.41918/) and an economy plugin (e.g., [EssentialsX Economy](https://www.spigotmc.org/resources/essentialsx.9089/))

2. **Download**:
   - Grab the latest `NBTKeyCard.jar` from the [Releases](https://github.com/4K1D3V/NBTKeyCard/releases) page.

3. **Install**:
   - Drop `NBTKeyCard.jar` into your server’s `plugins/` folder.
   - Ensure Vault and your economy plugin are also in the `plugins/` folder.

4. **Start Server**:
   - Launch your server with `java -jar spigot-1.21.4.jar`.
   - NBTKeyCard will generate a default `config.yml` in `plugins/NBTKeyCard/`.

5. **Configure**:
   - Edit `config.yml` to define your mines (see [Configuration](#configuration) below).

---

## Configuration

The `config.yml` file is your key to customizing NBTKeyCard. Below is an example configuration:

```yaml
# ╔════════════════════════════════════════════════════╗
# ║          NBTKeyCard Plugin Configuration           ║
# ╚════════════════════════════════════════════════════╝
#
# Welcome to the heart of NBTKeyCard! Configure your mines below with finesse.
# Should you stumble upon any glitches or quirks while exploring this plugin,
# we’d love to hear from you! Please raise an issue on our GitHub:
# ➜ https://github.com/4K1D3V/NBTKeyCard/issues
#
# Crafted with passion by Kite
# Visit us at: https://ks.akii.pro
# Enjoy a seamless mining adventure!

mines:
  mine1:
    world: world
    min:
      x: 0
      y: 0
      z: 0
    max:
      x: 100
      y: 100
      z: 100
    access: open
  mine2:
    world: world
    min:
      x: 101
      y: 0
      z: 0
    max:
      x: 200
      y: 100
      z: 100
    access: restricted
    required_nbt:
      key: "access"
      value: "mine2"
    code: "mine2code"
    shop_price: 100.0
  mine3:
    world: world_nether
    min:
      x: 201
      y: 0
      z: 0
    max:
      x: 300
      y: 100
      z: 100
    access: restricted
    required_nbt:
      key: "access"
      value: "mine3"
    code: "mine3code"
    shop_price: 150.0

messages:
  denial: "&cAccess Denied! You need a key card for this mine!"
```

### Key Fields
- **`mines.<name>`**: Define a mine with a unique name.
  - `world`: The world where the mine exists (e.g., `world`, `world_nether`).
  - `min/max`: X, Y, Z coordinates for the mine’s boundaries (inclusive).
  - `access`: `open` (public) or `restricted` (key card required).
  - `required_nbt`: NBT key-value pair for restricted mines.
  - `code`: Optional chat code to redeem a key card.
  - `shop_price`: Cost in the in-game shop (default: 100.0).
- **`messages.denial`**: Customizable message shown when access is denied.

---

## Commands

| Command                          | Description                              | Permission           |
|----------------------------------|------------------------------------------|----------------------|
| `/nbtkeycard give <player> <mine>` | Gives a key card to a player            | `nbtkeycard.give`   |
| `/nbtkeycard gui`                | Opens the key card selection GUI         | `nbtkeycard.gui`    |
| `/nbtkeycard shop`               | Opens the key card shop                  | `nbtkeycard.shop`   |
| `/nbtkeycard show <mine>`        | Displays a mine’s boundaries for 10s     | `nbtkeycard.admin`  |
| `/nbtkeycard mine create <name> <world> <minX> <minY> <minZ> <maxX> <maxY> <maxZ> <access>` | Creates a new mine | `nbtkeycard.admin`  |

### Chat Codes
- Players can type a mine’s `code` (e.g., `mine2code`) in chat to redeem a key card (requires `nbtkeycard.receive`).

---

## Permissions

| Permission             | Description                              | Default  |
|-----------------------|------------------------------------------|----------|
| `nbtkeycard.give`     | Allows giving key cards to players       | op       |
| `nbtkeycard.gui`      | Allows accessing the key card GUI        | true     |
| `nbtkeycard.shop`     | Allows accessing the key card shop       | true     |
| `nbtkeycard.receive`  | Allows redeeming key cards via chat      | true     |
| `nbtkeycard.admin`    | Allows managing mines and visualization  | op       |

---

## Usage

1. **Configure Mines**: Edit `config.yml` to set up your mines.
2. **Distribute Key Cards**:
   - Use `/nbtkeycard give <player> <mine>` as an admin.
   - Players can use `/nbtkeycard gui` to pick a free key card or `/nbtkeycard shop` to buy one.
   - Players can type a mine’s code in chat (e.g., `mine2code`) to redeem a key card.
3. **Manage Mines**:
   - Create new mines with `/nbtkeycard mine create`.
   - Visualize boundaries with `/nbtkeycard show <mine>`.

---

## Performance & Stability

- **Caching**: Player locations are cached to minimize lookups in `PlayerMoveEvent`.
- **Optimization**: Early exits in event handlers reduce CPU load.
- **Stability**: Robust error handling for invalid configs and missing dependencies.
- **Logging**: Access attempts and redemptions are logged for monitoring.

---

## Contributing

We welcome contributions! Here’s how to get involved:

1. **Fork the Repository**: Clone it to your local machine.
2. **Make Changes**: Add features, fix bugs, or improve documentation.
3. **Submit a Pull Request**: Describe your changes clearly in the PR.

Please follow the [Code of Conduct](CODE_OF_CONDUCT.md) and report issues at [GitHub Issues](https://github.com/4K1D3V/NBTKeyCard/issues).

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

## Credits

- **Author**: Kite  
- **GitHub**: [4K1D3V](https://github.com/4K1D3V)  
- **Support**: [ks.akii.pro](https://ks.akii.pro)

Happy mining with NBTKeyCard! ⛏️