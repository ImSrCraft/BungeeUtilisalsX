package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserAddFriendJob;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.FriendsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FriendAcceptSubCommandCall implements CommandCall
{

    public static void acceptFriendRequest( final User user, final UserStorage storage, final User target )
    {
        final FriendsDao dao = BuX.getApi().getStorageManager().getDao().getFriendsDao();

        dao.removeFriendRequest( user.getUuid(), storage.getUuid() );
        dao.addFriend( user.getUuid(), storage.getUuid() );
        dao.addFriend( storage.getUuid(), user.getUuid() );

        user.getFriends().add( new FriendData( storage.getUuid(), storage.getUserName(), new Date(), storage.getLastLogout() ) );
        user.sendLangMessage( "friends.accept.accepted", "{user}", storage.getUserName() );

        if ( target != null )
        {
            target.sendLangMessage( "friends.accept.request-accepted", "{user}", user.getName() );
            target.getFriends().add( new FriendData( user.getUuid(), user.getName(), new Date(), user.getStorage().getLastLogout() ) );
        }
        else if ( BuX.getApi().getPlayerUtils().isOnline( storage.getUserName() ) )
        {
            BuX.getInstance().getJobManager().executeJob( new UserAddFriendJob(
                    storage.getUuid(),
                    storage.getUserName(),
                    user.getUuid(),
                    user.getName(),
                    new Date(),
                    user.getStorage().getLastLogout()
            ) );
        }
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.accept.usage" );
            return;
        }
        final int friendLimit = FriendUtils.getFriendLimit( user );

        if ( user.getFriends().size() >= friendLimit )
        {
            user.sendLangMessage( "friends.accept.limited", "{limit}", friendLimit );
            return;
        }
        final String name = args.get( 0 );
        final Dao dao = BuX.getApi().getStorageManager().getDao();

        if ( user.getFriends().stream().anyMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.accept.already-friend", "{friend}", name );
            return;
        }

        final Optional<User> optionalTarget = BuX.getApi().getUser( name );
        final UserStorage storage = Utils.getUserStorageIfUserExists( optionalTarget.orElse( null ), name );

        if ( storage == null )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        if ( !dao.getFriendsDao().hasIncomingFriendRequest( user.getUuid(), storage.getUuid() ).join() )
        {
            user.sendLangMessage( "friends.accept.no-request", "{user}", name );
            return;
        }

        acceptFriendRequest( user, storage, optionalTarget.orElse( null ) );
    }

    @Override
    public String getDescription()
    {
        return "Accepts an outstanding incoming friend request.";
    }

    @Override
    public String getUsage()
    {
        return "/friend accept (user)";
    }
}
