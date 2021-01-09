package be.dieterblancke.bungeeutilisalsx.common.api.storage;

import be.dieterblancke.bungeeutilisalsx.common.storage.file.H2StorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.file.SQLiteStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.hikari.MariaDBStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.hikari.MySQLStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.hikari.PostgreSQLStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import lombok.Getter;

import java.util.function.Supplier;

public enum StorageType
{

    MYSQL( MySQLStorageManager::new, "MySQL" ),
    POSTGRESQL( PostgreSQLStorageManager::new, "PostgreSQL" ),
    MARIADB( MariaDBStorageManager::new, "MariaDB" ),
    SQLITE( SQLiteStorageManager::new, "SQLite" ),
    H2( H2StorageManager::new, "H2" ),
    MONGODB( MongoDBStorageManager::new, "MongoDB" );

    @Getter
    private final Supplier<? extends AbstractStorageManager> storageManagerSupplier;
    @Getter
    private final String name;

    StorageType( final Supplier<? extends AbstractStorageManager> storageManagerSupplier, final String name )
    {
        this.storageManagerSupplier = storageManagerSupplier;
        this.name = name;
    }
}
