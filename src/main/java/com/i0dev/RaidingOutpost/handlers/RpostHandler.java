package com.i0dev.RaidingOutpost.handlers;

import com.i0dev.RaidingOutpost.Heart;
import com.i0dev.RaidingOutpost.hooks.MCoreFactionsHook;
import com.i0dev.RaidingOutpost.managers.RpostManager;
import com.i0dev.RaidingOutpost.templates.AbstractListener;
import com.i0dev.RaidingOutpost.utility.Utility;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RpostHandler extends AbstractListener {
    public RpostHandler(Heart heart) {
        super(heart);
    }

    long lastRPostDisabledMessage = 0;
    long lastAlreadyOwnRPostMessage = 0;

    /*
    This method will handle the raiding outpost getting captured by a raiding faction.
     */
    @EventHandler
    public void onExplodeEvent(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof TNTPrimed)) return;
        if (!Utility.withinCuboid(heart.cnf().getBreachRegion(), e.getLocation())) return;
        if (heart.storage().isWorldBeingReset()) return;
        TNTPrimed tnt = (TNTPrimed) e.getEntity();
        Location origin = tnt.getOrigin();
        if (heart.isUsingMCoreFactions()) {
            if (MCoreFactionsHook.isSystemFaction(origin)) return;
            if (MCoreFactionsHook.isWilderness(origin)) return;
            Object fac = MCoreFactionsHook.getFactionFromLocation(origin);
            if (!heart.cnf().isRaidingOutpostEnabled()) {
                if (System.currentTimeMillis() > (lastRPostDisabledMessage + 3000L)) {
                    MCoreFactionsHook.msgFaction(fac, heart.msger().everything(heart.msg().getRPostDisabled()));
                    lastRPostDisabledMessage = System.currentTimeMillis();
                }
                return;
            }
            if (MCoreFactionsHook.getFactionID(fac).equalsIgnoreCase(heart.storage().getRaidingOutpostOwnerID())) {
                if (System.currentTimeMillis() > (lastAlreadyOwnRPostMessage + 3000L)) {
                    MCoreFactionsHook.msgFaction(fac, heart.msger().everything(heart.msg().getAlreadyOwnRPost()));
                    lastAlreadyOwnRPostMessage = System.currentTimeMillis();
                }
                return;
            }
            heart.getManager(RpostManager.class).setNewOwner(fac);
            heart.getManager(RpostManager.class).resetWorld(false);
        }
    }

    /*
    This method will prevent players from entering the rPost world if it is disabled or resetting
     */
    @EventHandler
    public void onTeleportEvent(PlayerTeleportEvent e) {
        if (heart.cnf().getWorldsToDenyEntryWhenDisabled().contains(e.getTo().getWorld().getName())) {
            if (heart.storage().isWorldBeingReset()) {
                heart.msger().msg(e.getPlayer(), heart.msg().getCantEnterRPostWorldResetting());
                e.setCancelled(true);
                return;
            }
            if (heart.cnf().isRaidingOutpostEnabled()) return;
            if (e.getPlayer().hasPermission("raidingoutpost.allowTeleportWhenDisabled")) return;
            heart.msger().msg(e.getPlayer(), heart.msg().getCantEnterRPostWorldDisabled());
            e.setCancelled(true);
        }
    }

    /*
    The following methods will handle the raid timer aspect of the raiding outpost.
     */

    // COMING SOON

}
