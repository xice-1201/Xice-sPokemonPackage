package cn.cnxice.cobblemonfix;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

/** Registration for the independent Beehive block; logic is supplied by its block entity. */
public final class PokemonBeehiveRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(XicesCobblemonFix.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(XicesCobblemonFix.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, XicesCobblemonFix.MOD_ID);
    public static final DeferredBlock<PokemonBeehiveBlock> POKEMON_BEEHIVE = BLOCKS.register("pokemon_beehive",
            () -> new PokemonBeehiveBlock(BlockBehaviour.Properties.of().strength(0.6f).sound(SoundType.WOOD)));
    public static final DeferredItem<BlockItem> POKEMON_BEEHIVE_ITEM = ITEMS.register("pokemon_beehive",
            () -> new BlockItem(POKEMON_BEEHIVE.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<PokemonBeehiveBlockEntity>> POKEMON_BEEHIVE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "pokemon_beehive", () -> BlockEntityType.Builder.of(PokemonBeehiveBlockEntity::new, POKEMON_BEEHIVE.get()).build(null));

    private PokemonBeehiveRegistry() {}
}
