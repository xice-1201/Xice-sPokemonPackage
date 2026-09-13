package cn.cnxice.cobblemonfix.spawn;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

/** Adds the Torchberry by-product to Volbeat and Illumise drops. */
public final class VolbeatIllumiseDropHandler {
    private static final ResourceLocation TORCHBERRIES =
            ResourceLocation.fromNamespaceAndPath("twilightforest", "torchberries");

    private VolbeatIllumiseDropHandler() {
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof PokemonEntity pokemonEntity)
                || event.getEntity().level().isClientSide()) {
            return;
        }

        String species = pokemonEntity.getPokemon().getSpecies().getResourceIdentifier().getPath();
        if (!species.equals("volbeat") && !species.equals("illumise")) {
            return;
        }

        var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(TORCHBERRIES);
        if (item == net.minecraft.world.item.Items.AIR) {
            return;
        }
        int count = 1 + event.getEntity().getRandom().nextInt(2);
        Level level = event.getEntity().level();
        event.getDrops().add(new ItemEntity(
                level,
                event.getEntity().getX(),
                event.getEntity().getY(),
                event.getEntity().getZ(),
                new ItemStack(item, count)
        ));
    }
}
