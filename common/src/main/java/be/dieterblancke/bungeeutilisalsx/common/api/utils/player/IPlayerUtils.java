package be.dieterblancke.bungeeutilisalsx.common.api.utils.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MojangUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.SneakyThrows;

import java.util.List;
import java.util.UUID;

public interface IPlayerUtils
{

    int getPlayerCount( String server );

    List<String> getPlayers( String server );

    int getTotalCount();

    List<String> getPlayers();

    IProxyServer findPlayer( String name );

    boolean isOnline( String name );

    UUID getUuidNoFallback( String targetName );

    @SneakyThrows
    default UUID getUuid( final String targetName )
    {
        UUID uuid = this.getUuidNoFallback( targetName );

        if ( uuid != null )
        {
            return uuid;
        }
        uuid = BuX.getApi().getStorageManager().getDao().getUserDao().getUuidFromName( targetName ).get();

        if ( uuid != null )
        {
            return uuid;
        }

        try
        {
            return Utils.readUUIDFromString( MojangUtils.getUuid( targetName ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}