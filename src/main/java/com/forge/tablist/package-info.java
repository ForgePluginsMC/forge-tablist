/**
 * ForgeTablist — animated tablist header/footer, per-group tab names,
 * and an animated server-list MOTD.
 *
 * <p>Nullability convention: everything in this package is non-null by
 * default. The only deliberately nullable values are the {@code playerName}
 * parameters of {@link com.forge.tablist.TextUtil#parseLines} and its
 * placeholder helper, which accept {@code null} in MOTD context where no
 * player exists (server-list pings).</p>
 */
@org.jetbrains.annotations.NotNullByDefault
package com.forge.tablist;
