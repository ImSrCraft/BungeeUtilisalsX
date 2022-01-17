package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.PluginInfo;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.List;
import java.util.Optional;

public interface ProxyOperationsApi
{

    void registerCommand( Command command );

    void unregisterCommand( Command command );

    List<IProxyServer> getServers();

    IProxyServer getServerInfo( String serverName );

    List<PluginInfo> getPlugins();

    Optional<PluginInfo> getPlugin( String pluginName );

    long getMaxPlayers();

    Object getMessageComponent( final BaseComponent... components );

}