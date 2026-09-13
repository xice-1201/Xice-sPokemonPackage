package cn.cnxice.cobblemonfix;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class PokemonBeehiveCreativeTab {
    public static void add(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(PokemonBeehiveRegistry.POKEMON_BEEHIVE_ITEM.get());
        }
    }
    private PokemonBeehiveCreativeTab() {}
}
