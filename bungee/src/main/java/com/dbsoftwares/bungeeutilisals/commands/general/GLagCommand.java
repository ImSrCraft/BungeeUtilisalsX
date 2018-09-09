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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.utils.TPSRunnable;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GLagCommand extends Command {

    public GLagCommand() {
        super(
                "glag",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("glag.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("glag.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        Long uptime = ManagementFactory.getRuntimeMXBean().getStartTime();
        SimpleDateFormat df2 = new SimpleDateFormat("kk:mm dd-MM-yyyy");
        String date = df2.format(new Date(uptime));

        double TPS = TPSRunnable.getTPS();

        user.sendLangMessage("general-commands.glag",
                "{tps}", getColor(TPS) + String.valueOf(TPS),
                "{maxmemory}", (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB",
                "{freememory}", (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB",
                "{totalmemory}", (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB",
                "{onlinesince}", date
        );
    }

    private ChatColor getColor(double tps) {
        if (tps >= 18.0) {
            return ChatColor.GREEN;
        } else if (tps >= 14.0) {
            return ChatColor.YELLOW;
        } else if (tps >= 8.0) {
            return ChatColor.RED;
        } else {
            return ChatColor.DARK_RED;
        }
    }
}
