<p align="center">
  <img src="assets/logo.webp" width="160" alt="ForgeTablist logo">
</p>

<h1 align="center">ForgeTablist</h1>

<p align="center"><i>Animated tablist headers &amp; footers, per-group tab names, and an animated server-list MOTD.</i></p>

<p align="center">
  <img src="https://img.shields.io/badge/version-1.0.0-ff7b2e?style=for-the-badge" alt="version 1.0.0">
  <img src="https://img.shields.io/badge/Paper-26.3-2f9e6e?style=for-the-badge" alt="Paper 26.3">
  <img src="https://img.shields.io/badge/Java-25-f89820?style=for-the-badge" alt="Java 25">
  <img src="https://img.shields.io/badge/animated-2563eb?style=for-the-badge" alt="animated">
  <img src="https://img.shields.io/badge/MiniMessage-everywhere-b565d8?style=for-the-badge" alt="MiniMessage everywhere">
  <img src="https://img.shields.io/badge/dependencies-zero-6b7280?style=for-the-badge" alt="zero dependencies">
</p>

<p align="center"><sub>Not affiliated with <a href="https://minecraftforge.net">MinecraftForge</a> — "Forge" is just a name.</sub></p>

---

Animated tablist headers and footers, per-permission-group tab names, and an animated server-list MOTD for Paper servers. Every string is MiniMessage. An original implementation with zero runtime dependencies beyond Paper itself.

## Features

- **Animated header/footer frames** — define any number of multi-line MiniMessage frames; the plugin cycles through them on a timer. Default: 4 shimmering gradient header frames and 4 rotating footer frames with tips and live stats, changing every 2 seconds.
- **Per-group tab names** — assign tab-list name styles by permission. Groups are checked top to bottom, first match wins; an empty permission acts as a fallback for everyone. Prefix, name, and suffix are parsed as *one* MiniMessage string, so paired tags like `<gray>...</gray>` wrap the name correctly instead of leaking literally.
- **Animated server-list MOTD** — multi-frame MOTD served on `PaperServerListPingEvent`. The frame is derived from wall-clock time (not a scheduler), so it animates smoothly and is safe to call off the main thread. Optional max-players override for the server list.
- **Live placeholders** — `%player%`, `%online%`, `%max%`, and `%tps%` (one decimal, 1-minute average) in any header, footer, or MOTD frame.
- **Fault-tolerant parsing** — malformed MiniMessage tags fall back to literal text instead of throwing, so one bad line can never break the tablist.
- **Live reload** — `/ftablist reload` re-reads the config, restarts the animation timers, and re-applies everything without a restart.

## Requirements

- Paper 26.3 or newer (`api-version: 26.3`)
- Java 25

## Installation

1. Build or download `ForgeTablist-1.0.0.jar`.
2. Drop it into your server's `plugins/` folder.
3. Restart the server. A default `config.yml` is generated — edit it and run `/ftablist reload`.

## Commands

| Command           | Arguments | Description                          | Permission            |
|-------------------|-----------|--------------------------------------|-----------------------|
| `/ftablist`       | `reload`  | Reload config and re-apply tablist   | `forgetablist.admin`  |

## Permissions

| Permission             | Description                                | Default |
|------------------------|--------------------------------------------|---------|
| `forgetablist.admin`   | Admin access (`/ftablist reload`)          | op      |

The `groups` section of the config references permission nodes (e.g. `forgetablist.group.admin`, `forgetablist.group.vip`) purely as config-driven matchers — they are not registered permissions and can be any node your permission plugin grants.

## Configuration

Default `config.yml` (all text is MiniMessage):

| Key                    | Type          | Default | Description |
|------------------------|---------------|---------|-------------|
| `header-frames`        | list of frames| 4 shimmer gradient frames | Animated tablist header. Each frame is a list of MiniMessage lines. |
| `footer-frames`        | list of frames| 4 rotating tip/stat frames | Animated tablist footer. Each frame is a list of MiniMessage lines. |
| `frame-interval-seconds` | long        | `2`     | Seconds between tablist frame changes. |
| `groups`               | list of maps  | admin / vip / fallback | Tab-name styles. Each entry: `permission` (empty = matches everyone, first match wins), `prefix`, `suffix`. Parsed as a single MiniMessage string with the player name in between. |
| `motd-frames`          | list of frames| 3 gradient frames | Animated server-list MOTD frames. `%player%` renders empty in MOTD context. |
| `motd-interval-seconds` | long        | `5`     | Seconds between MOTD frame changes. |
| `motd-max-players`     | int           | `100`   | Max-players value shown in the server list. `-1` = use the server default. |

Placeholders available in header, footer, and MOTD frames: `%player%`, `%online%`, `%max%`, `%tps%`.

## Building from source

```bash
bash build.sh
```

Compiles with JDK 25 (`~/workspace/.toolchains/jdk-25.0.4.1+1`) via direct `javac` against the Paper 26.3 API. Build flags: `-Werror -Xlint` — zero warnings tolerated, no deprecated APIs used.

## Code quality

- Nullness is declared package-wide with `@NotNullByDefault`; every parameter and return value that can be null is explicitly annotated `@Nullable` (e.g. the nullable `playerName` in MOTD rendering context).
- No deprecated Bukkit/Paper APIs are used anywhere in the codebase.

---

<p align="center"><i>Part of the <a href="https://github.com/ForgePluginsMC">Forge</a> plugin suite — original implementations, zero dependencies.</i></p>
