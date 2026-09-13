package cn.cnxice.cobblemonfix.integration;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.ITooltip;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import java.util.Locale;

/** Shows the effective Minecraft armor values of a Pokémon in Jade. */
@WailaPlugin
public final class JadePokemonAttributesPlugin implements IWailaPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(
            "xices_cobblemon_fix", "pokemon_armor");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(new ArmorProvider(), PokemonEntity.class);
    }

    private static final class ArmorProvider implements IEntityComponentProvider {
        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (accessor.getEntity() instanceof PokemonEntity pokemon) {
                double armor = pokemon.getAttributeValue(Attributes.ARMOR);
                double toughness = pokemon.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
                tooltip.add(Component.translatable("jade.xices_cobblemon_fix.armor",
                        String.format(Locale.ROOT, "%.1f", armor),
                        String.format(Locale.ROOT, "%.1f", toughness)), UID);
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}
