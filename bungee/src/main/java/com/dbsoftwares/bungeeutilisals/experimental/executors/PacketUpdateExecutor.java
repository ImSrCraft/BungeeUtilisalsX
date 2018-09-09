/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.experimental.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.experimental.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketReceiveEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketSendEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketUpdateEvent;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketUpdateExecutor implements EventExecutor {

    @Event
    public void onPacketUpdate(PacketUpdateEvent event) {
        if (event.getSender() instanceof ServerConnection && event.getReciever() instanceof BungeeConnection) {
            PacketSendEvent packetEvent = new PacketSendEvent(event.getPacket(), event.getPlayer(), event.getSender(), event.getReciever());

            BUCore.getApi().getEventLoader().launchEvent(packetEvent);

            if (packetEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } else if (event.getSender() instanceof ProxiedPlayer && event.getReciever() instanceof BungeeConnection) {
            PacketReceiveEvent packetEvent = new PacketReceiveEvent(event.getPacket(), event.getPlayer(), event.getSender(), event.getReciever());

            BUCore.getApi().getEventLoader().launchEvent(packetEvent);

            if (packetEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @Event
    public void onPacketReceive(PacketReceiveEvent event) {

    }
}