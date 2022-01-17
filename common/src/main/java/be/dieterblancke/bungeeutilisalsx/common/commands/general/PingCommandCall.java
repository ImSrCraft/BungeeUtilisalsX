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

package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserGetPingJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class PingCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            user.sendLangMessage( "general-commands.ping.message" );
        }
        else
        {
            final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "ping.permission-other" );
            if ( permission != null
                    && !permission.isEmpty()
                    && !user.hasPermission( permission ) )
            {
                user.sendLangMessage( "no-permission", "%permission%", permission );
                return;
            }

            final String name = args.get( 0 );

            if ( BuX.getApi().getPlayerUtils().isOnline( name ) )
            {
                BuX.getInstance().getJobManager().executeJob(
                        new UserGetPingJob( user.getUuid(), user.getName(), name )
                );
            }
            else
            {
                user.sendLangMessage( "offline" );
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "Shows your (or someone else's) current ping towards the current proxy.";
    }

    @Override
    public String getUsage()
    {
        return "/ping [user]";
    }
}