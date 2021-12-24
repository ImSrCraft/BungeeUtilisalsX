package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import lombok.Data;

import java.util.UUID;

@Data
public class PartySetOwnerJob implements MultiProxyJob
{

    private final Party party;
    private final UUID uuid;
    private final boolean owner;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
