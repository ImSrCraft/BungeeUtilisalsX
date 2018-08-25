package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;

import java.util.Arrays;
import java.util.List;

public class IPBanCommand extends Command {

    public IPBanCommand() {
        super("ipban", Arrays.asList(FileLocation.PUNISHMENTS_CONFIG.getConfiguration()
                        .getString("commands.ipban.aliases").split(", ")),
                FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("commands.ipban.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.ipban.usage");
            return;
        }
        Dao dao = BUCore.getApi().getStorageManager().getDao();
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        if (!dao.getUserDao().exists(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = dao.getUserDao().getUserData(args[0]);
        if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.IPBAN, null, storage.getIp(), true)) {
            user.sendLangMessage("punishments.ipban.already-banned");
            return;
        }

        UserPunishEvent event = new UserPunishEvent(PunishmentType.IPBAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null);
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();

        PunishmentInfo info = dao.getPunishmentDao().insertPunishment(
                PunishmentType.IPBAN, storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, 0L, user.getServerName(), true, user.getName()
        );

        api.getUser(storage.getUserName()).ifPresent(banned -> {
            String kick = Utils.formatList(banned.getLanguageConfig().getStringList("punishments.ipban.kick"), "\n");
            kick = executor.setPlaceHolders(kick, info);

            banned.kick(kick);
        });

        user.sendLangMessage("punishments.ipban.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.ipban.broadcast",
                FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("commands.ipban.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}