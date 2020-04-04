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

package com.dbsoftwares.bungeeutilisals.api.utils.config;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.configuration.api.IConfiguration;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config
{

    @Getter
    protected IConfiguration config;
    @Getter
    protected String location;

    public Config( final String location )
    {
        this.location = location;
    }

    public void load( final Plugin plugin )
    {
        final File file = new File( plugin.getDataFolder(), location );

        if ( !file.exists() )
        {
            IConfiguration.createDefaultFile( plugin.getResourceAsStream( location ), file );

            this.config = IConfiguration.loadYamlConfiguration( file );
        }
        else
        {
            // update configurations ...
            this.config = IConfiguration.loadYamlConfiguration( file );
            try
            {
                config.copyDefaults(
                        IConfiguration.loadYamlConfiguration( plugin.getResourceAsStream( location ) )
                );
            }
            catch ( IOException e )
            {
                BUCore.getLogger().log( Level.SEVERE, "Could not update configurations: ", e );
            }
        }

        this.setup();
        BUCore.getLogger().info( "Successfully loaded config file: " + location );
    }

    public void reload()
    {
        try
        {
            this.config.reload();
            this.purge();
            this.setup();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    protected void purge()
    {
        // do nothing
    }

    protected void setup()
    {
        // do nothing
    }

    public boolean isEnabled()
    {
        if ( config.exists( "enabled" ) )
        {
            return config.getBoolean( "enabled" );
        }
        return true;
    }

    public boolean isEnabled( final String path )
    {
        if ( config.exists( path + ".enabled" ) )
        {
            return config.getBoolean( path + ".enabled" );
        }
        return true;
    }
}
