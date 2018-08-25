package com.dbsoftwares.bungeeutilisals.api.utils.server;

/*
 * Created by DBSoftwares on 24 juni 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.google.common.collect.Lists;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.LinkedList;
import java.util.List;

@Data
public class ServerGroup {

    private String name;
    private boolean global;
    private List<String> servers;

    public ServerGroup(String name, boolean global, List<String> servers) {
        this.name = name;
        this.global = global;
        this.servers = searchServers(servers);
    }

    private List<String> searchServers(List<String> servers) {
        LinkedList<String> foundServers = Lists.newLinkedList();

        servers.forEach(server -> {
            for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
                String name = info.getName().toLowerCase();

                if (server.startsWith("*")) {
                    if (name.endsWith(server.substring(1, server.length()).toLowerCase())) {
                        foundServers.add(info.getName());
                    }
                } else if (server.endsWith("*")) {
                    if (name.startsWith(server.substring(0, server.length() - 1).toLowerCase())) {
                        foundServers.add(info.getName());
                    }
                } else {
                    if (name.equalsIgnoreCase(server)) {
                        foundServers.add(info.getName());
                    }
                }
            }
        });

        return foundServers;
    }

    public int getPlayers() {
        int players = 0;

        if (global) {
            return BUCore.getApi().getPlayerUtils().getTotalCount();
        }

        for (String server : servers) {
            players += BUCore.getApi().getPlayerUtils().getPlayerCount(server);
        }

        return players;
    }

    public List<String> getPlayerList() {
        List<String> players = Lists.newArrayList();

        if (global) {
            return BUCore.getApi().getPlayerUtils().getPlayers();
        }

        for (String server : servers) {
            players.addAll(BUCore.getApi().getPlayerUtils().getPlayers(server));
        }

        return players;
    }

    public List<ServerInfo> getServerInfos() {
        List<ServerInfo> servers = Lists.newArrayList();

        this.servers.forEach(serverName -> {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);

            if (info != null) {
                servers.add(info);
            }
        });

        return servers;
    }
}