package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendSettingsSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            final FriendSettings settings = user.getFriendSettings();
            user.sendLangMessage( "friends.settings.noargs.header" );

            for ( FriendSetting setting : FriendSetting.getEnabledSettings() )
            {
                user.sendLangMessage(
                        "friends.settings.noargs.format",
                        "{type}", setting.getName( user.getLanguageConfig().getConfig() ),
                        "{unformatted-type}", setting.toString(),
                        "{status}", user.getLanguageConfig().getConfig().getString( "friends.settings.noargs." + ( settings.getSetting( setting ) ? "enabled" : "disabled" ) )
                );
            }

            user.sendLangMessage( "friends.settings.noargs.footer" );
        }
        else if ( args.size() == 2 )
        {
            final FriendSetting type = Utils.valueOfOr( FriendSetting.class, args.get( 0 ).toUpperCase(), null );

            if ( type == null )
            {
                final String settings = Stream.of( FriendSetting.values() )
                        .map( t -> t.toString() )
                        .collect( Collectors.joining() );

                user.sendLangMessage( "friends.settings.invalid", "{settings}", settings );
                return;
            }
            final boolean value = args.get( 1 ).contains( "toggle" )
                    ? !user.getFriendSettings().getSetting( type )
                    : !args.get( 1 ).toLowerCase().contains( "d" );

            user.getFriendSettings().set( type, value );
            BuX.getApi().getStorageManager().getDao().getFriendsDao().setSetting( user.getUuid(), type, value );

            user.sendLangMessage(
                    "friends.settings.updated",
                    "{type}", type.toString().toLowerCase(),
                    "{value}", value
            );
        }
        else
        {
            user.sendLangMessage( "friends.settings.usage" );
        }
    }

    @Override
    public String getDescription()
    {
        return "Updates a setting value for one of the existing setting types.";
    }

    @Override
    public String getUsage()
    {
        return "/friend settings [setting] [value]";
    }
}
