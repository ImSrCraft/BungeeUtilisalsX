package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserRemoveFriendJob;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;
import java.util.Optional;

public class FriendRemoveSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.remove.usage" );
            return;
        }
        final String name = args.get( 0 );
        final Dao dao = BuX.getApi().getStorageManager().getDao();
        final Optional<User> optionalTarget = BuX.getApi().getUser( name );
        final UserStorage storage = Utils.getUserStorageIfUserExists( optionalTarget.orElse( null ), name );

        if ( storage == null )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            if ( dao.getFriendsDao().hasOutgoingFriendRequest( user.getUuid(), storage.getUuid() ).join() )
            {
                FriendRemoveRequestSubCommandCall.removeFriendRequest( storage, user, optionalTarget.orElse( null ) );
                return;
            }
            if ( dao.getFriendsDao().hasIncomingFriendRequest( user.getUuid(), storage.getUuid() ).join() )
            {
                FriendDenySubCommandCall.removeFriendRequest( storage, user, optionalTarget.orElse( null ) );
                return;
            }

            user.sendLangMessage( "friends.remove.no-friend", "{user}", name );
            return;
        }

        dao.getFriendsDao().removeFriend( user.getUuid(), storage.getUuid() );
        dao.getFriendsDao().removeFriend( storage.getUuid(), user.getUuid() );

        user.getFriends().removeIf( data -> data.getFriend().equalsIgnoreCase( name ) );
        user.sendLangMessage( "friends.remove.removed", "{user}", name );

        if ( optionalTarget.isPresent() )
        {
            final User target = optionalTarget.get();

            target.getFriends().removeIf( data -> data.getFriend().equalsIgnoreCase( user.getName() ) );
            target.sendLangMessage( "friends.remove.friend-removed", "{user}", user.getName() );
        }
        else
        {
            BuX.getInstance().getJobManager().executeJob( new UserRemoveFriendJob(
                    storage.getUuid(),
                    storage.getUserName(),
                    user.getName()
            ) );
        }
    }

    @Override
    public String getDescription()
    {
        return "Removes a friend from your friends list. If this user has an outstanding friend request towards you, this request will be denied.";
    }

    @Override
    public String getUsage()
    {
        return "/friend remove (user)";
    }
}
