package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.BossBarAnnouncement;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.BossBarMessage;
import com.google.common.collect.Lists;

import java.util.List;

public class BossBarAnnouncer extends Announcer {

    public BossBarAnnouncer() {
        super(AnnouncementType.BOSSBAR);
    }

    @Override
    public void loadAnnouncements() {
        for (ISection section : configuration.getSectionList("announcements")) {
            ServerGroup group = FileLocation.SERVERGROUPS.getData(section.getString("server"));

            if (group == null) {
                BUCore.log("Could not find a servergroup or -name for " + section.getString("server") + "!");
                return;
            }
            List<BossBarMessage> messages = Lists.newArrayList();
            TimeUnit unit = TimeUnit.valueOfOrElse(section.getString("stay.unit"), TimeUnit.SECONDS);
            int stay = section.getInteger("stay.time");
            String permission = section.getString("permission");

            for (ISection message : section.getSectionList("messages")) {
                messages.add(
                        new BossBarMessage(
                                BarColor.valueOf(message.getString("color")),
                                BarStyle.valueOf(message.getString("style")),
                                message.getFloat("progress"),
                                message.getBoolean("language"),
                                message.getString("text")
                        )
                );
            }
            addAnnouncement(new BossBarAnnouncement(messages, unit, stay, group, permission));
        }
    }
}