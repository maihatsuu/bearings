# Bearings (working name)

A Fabric mod for Minecraft 1.21.1. Your position and the time aren't free information any more: the
F3 screen only shows coordinates while you carry a compass, and the time while you carry a clock.
Clocks and compasses can be worn on your belt (a Trinkets slot) so they don't take up inventory.

Required on the client (it changes the F3 screen). Install on the server too so the belt slots and
settings are shared.

## Belt slots
- Uses Trinkets' existing `legs/belt` slot (the one Spyglass Improvements already uses for the
  spyglass) and raises it to **3 slots**.
- The belt accepts the clock, compass and recovery compass, alongside the spyglass. Any mix: e.g.
  spyglass + compass + clock.
- Data only:
  - `data/trinkets/slots/legs/belt.json` with `"amount": 3`.
  - `data/trinkets/tags/item/legs/belt.json` adding `minecraft:clock`, `minecraft:compass` and
    `minecraft:recovery_compass` (`"replace": false`).
- Trinkets is a soft dependency: without it there are no belt slots, and only items in your
  inventory count.

## F3 gating
An item "counts" if it's anywhere in your inventory (hotbar, main inventory, offhand) or on your belt.

| Item | Unlocks in F3 |
|---|---|
| Compass | XYZ, block position, chunk, facing direction, targeted block/fluid position |
| Clock | Time of day (and day number) |
| Recovery compass | Last death location (dimension + XYZ) |

Without the item, those lines are hidden (not blanked or replaced with "???"), so F3 just looks
shorter.

Also covered:
- **F3+C** (copy location) only works while you have a compass.
- Vanilla F3 doesn't show the time of day at all, so the clock adds a line rather than revealing
  one. Format TBD (see open questions).
- Works with **BetterF3** (in the pack) as well as vanilla F3. BetterF3 builds its lines from
  modules (coordinates, location, etc.), so gate those modules' lines instead of vanilla's
  `DebugScreenOverlay` lines.

## Config (`config/bearings.json`)
| Option | Default | Description |
|---|---|---|
| `requireCompassForCoordinates` | true | Hide coordinate lines without a compass. |
| `requireClockForTime` | true | Show the time line only with a clock. |
| `showDeathLocation` | true | Recovery compass shows your last death location. |
| `beltSlots` | 3 | Size of the belt slot (written to the Trinkets data on load, or fixed at 3 if that's simpler). |
| `hideOtherInfo` | false | Optional extra: also hide other "cheaty" F3 info (biome, light levels, mob counts…). |

Item lists come from item tags, `bearings:shows_coordinates`, `bearings:shows_time` and
`bearings:shows_death_location`, so datapacks/other mods can add items (e.g. a modded sextant).

## Open questions
- Name: Bearings? Wayfinder? Waypoint?
- Clock format in F3: exact (`18:30`, `Day 12`) or vaguer (`Dusk`)? Exact fits F3; vague fits
  vanilla's style elsewhere.
- Should a worn clock/compass ever show anything outside F3 (e.g. a tiny vanilla-style hint)? Default
  no: no new HUD.
- Server enforcement: settings are client-side, so a modified client could ignore them. Fine for a
  co-op pack; could sync the server's config to clients so servers decide.
- Does Trinkets merge `amount` across data packs (take the highest) or does the last one win? Check
  so Spyglass Improvements and this mod don't fight over the belt size.
- Check other mods in the pack for position leaks (Jade tooltips, etc.).

## Testing
- No compass: F3 shows no XYZ/block/chunk/facing/targeted-block lines; F3+C does nothing.
- Compass in inventory, then on the belt: coordinates appear both ways; remove it, they disappear.
- Clock: time line appears; recovery compass: death location appears after dying.
- Belt holds spyglass + clock + compass at once; Spyglass Improvements' keybind still finds the
  spyglass.
- Same results with BetterF3 installed and with vanilla F3.
- Without Trinkets installed: mod loads, inventory items still work.
