package be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations;

import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

public class v6_api_keys extends FileMigration
{

    public v6_api_keys()
    {
        super( "migrations/v6_api_keys.sql" );
    }

    @Override
    public boolean shouldRun()
    {
        return true;
    }
}
