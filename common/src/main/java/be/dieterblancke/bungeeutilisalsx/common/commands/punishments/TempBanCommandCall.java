package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class TempBanCommandCall extends PunishmentCommand
{

    public TempBanCommandCall()
    {
        super( "punishments.tempban", true );
    }

    @Override
    public void onPunishmentExecute( final User user, final List<String> args, final List<String> parameters, final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        final long time = punishmentArgs.getTime();

        if ( time == 0L )
        {
            user.sendLangMessage( "punishments.tempban.non-valid" );
            return;
        }
        if ( dao().getPunishmentDao().getBansDao().isBanned( storage.getUuid(), punishmentArgs.getServerOrAll() ).join() )
        {
            user.sendLangMessage( "punishments.tempban.already-banned" );
            return;
        }
        if ( punishmentArgs.launchEvent( PunishmentType.TEMPBAN ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getBansDao().insertTempBan(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                true,
                user.getName(),
                time
        ).thenAccept( info ->
        {
            // Attempting to kick if player is online. If briding is enabled and player is not online, it will attempt to kick on other bungee's.
            super.attemptKick( storage, "punishments.tempban.kick", info );

            user.sendLangMessage( "punishments.tempban.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

            if ( !parameters.contains( "-s" ) )
            {
                if ( parameters.contains( "-nbp" ) )
                {
                    BuX.getApi().langBroadcast(
                            "punishments.tempban.broadcast",
                            executor.getPlaceHolders( info ).toArray( new Object[]{} )
                    );
                }
                else
                {
                    BuX.getApi().langPermissionBroadcast(
                            "punishments.tempban.broadcast",
                            ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.tempban.broadcast" ),
                            executor.getPlaceHolders( info ).toArray( new Object[]{} )
                    );
                }
            }

            punishmentArgs.launchPunishmentFinishEvent( PunishmentType.TEMPBAN );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Temporarily bans a given user from the network (or given server if per-server punishments are enabled).";
    }

    @Override
    public String getUsage()
    {
        return "/tempban (user) (time) <server> (reason)";
    }
}