package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.utils.ItemUtils;
import be.dieterblancke.configuration.api.ISection;
import com.google.common.collect.Lists;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GuiConfigItemStack
{

    private final int data;
    private final String name;
    private final List<String> lore;
    private final String owner;
    private final List<GuiConfigEnchantment> enchantments;
    private final Integer customModelData;
    private ItemType itemType;

    public GuiConfigItemStack( final ISection section )
    {
        try
        {
            this.itemType = ItemType.valueOf( section.getString( "material" ) );
        }
        catch ( Exception e )
        {
            this.itemType = ItemType.AIR;
            BuX.getLogger().warning( "Could not find a Material for " + section.getString( "material" ) );
        }
        this.data = section.exists( "data" ) ? section.getInteger( "data" ) : 0;
        this.name = section.exists( "name" ) ? section.getString( "name" ) : "";
        this.lore = section.exists( "lores" )
                ? section.isString( "lores" ) ? Lists.newArrayList( section.getString( "lores" ) ) : section.getStringList( "lores" )
                : new ArrayList<>();
        this.owner = section.exists( "owner" ) ? section.getString( "owner" ) : null;
        this.enchantments = section.exists( "enchants" ) ? section.getSectionList( "enchants" )
                .stream()
                .map( GuiConfigEnchantment::new )
                .collect( Collectors.toList() )
                : new ArrayList<>();
        this.customModelData = section.exists( "custom-model-data" ) ? section.getInteger( "custom-model-data" ) : null;
    }

    public ItemStack buildItem( final User user, final Object... placeholders )
    {
        if ( this.itemType == null || this.itemType == ItemType.AIR )
        {
            return new ItemStack( ItemType.AIR );
        }
        final ItemStack itemStack = new ItemStack( this.itemType );

        if ( this.data != 0 )
        {
            itemStack.durability( (short) this.data );
        }
        itemStack.lore(
                this.lore.stream()
                        .map( lore ->
                        {
                            Component loreComponent = Utils.format( user, Utils.replacePlaceHolders( lore, placeholders ) );

                            loreComponent = loreComponent.decoration( TextDecoration.ITALIC, false );

                            return BuX.getInstance().proxyOperations().getMessageComponent( loreComponent );
                        } )
                        .collect( Collectors.toList() ),
                false
        );
        Component displayName = Utils.format( user, Utils.replacePlaceHolders( this.name, placeholders ) );
        displayName = displayName.decoration( TextDecoration.ITALIC, false );
        itemStack.displayName( BuX.getInstance().proxyOperations().getMessageComponent( displayName ) );

        this.enchantments.forEach( enchant -> enchant.addToItem( itemStack ) );

        if ( this.owner != null && !this.owner.trim().isEmpty() )
        {
            ItemUtils.setSkullTexture( itemStack, this.owner.trim() );
        }
        if ( this.customModelData != null )
        {
            itemStack.nbtData().putInt( "CustomModelData", customModelData );
        }
        return itemStack;
    }
}
