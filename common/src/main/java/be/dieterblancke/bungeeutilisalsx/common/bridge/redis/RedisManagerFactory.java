package be.dieterblancke.bungeeutilisalsx.common.bridge.redis;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.RedisManager;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;

public final class RedisManagerFactory
{

    private RedisManagerFactory()
    {
    }

    public static RedisManager create()
    {
        final IConfiguration config = ConfigFiles.CONFIG.getConfig();
        final ISection section = config.getSection( "multi-proxy.redis" );

        if ( section.getBoolean( "clustered" ) )
        {
            BuX.getLogger().info( "Clustered uri was set to true, using ClusteredRedisManager." );
            return new ClusteredRedisManager( section );
        }

        BuX.getLogger().info( "Clustered uri was set to false, using StandaloneRedisManager." );
        return new StandardRedisManager( section );
    }
}
