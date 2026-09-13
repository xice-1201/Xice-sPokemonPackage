package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.OriginalTrainerType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Gender;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

/** Allows any player or Create deployer to bucket lava from supported owned Pokémon. */
@Mixin(value = PokemonEntity.class, remap = false)
public abstract class OwnedPokemonLavaBucketMixin {
    private static final ResourceLocation XICE_LAVA_BUCKET_COOLDOWN =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "lava_bucket");
    private static final ResourceLocation XICE_MILK_BUCKET_COOLDOWN =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "milk_bucket");
    private static final ResourceLocation XICE_PRESSING_COOLDOWN =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "tinkatink_pressing");
    private static final ResourceLocation XICE_SAND_BRUSHING_COOLDOWN =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "sand_brushing");
    private static final ResourceLocation XICE_MECHANICAL_NUTRIENT_BLOCK =
            ResourceLocation.fromNamespaceAndPath("kubejs", "mechanical_nutrient_block");
    private static final ResourceLocation XICE_MAGNETIC_FEED_COOLDOWN =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "magnemite_feed");
    private static final ResourceLocation XICE_INSECT_FEED =
            ResourceLocation.fromNamespaceAndPath("kubejs", "insect_feed");
    private static final ResourceLocation XICE_GROWTH_GRANULES =
            ResourceLocation.fromNamespaceAndPath("kubejs", "growth_granules");
    private static final ResourceLocation XICE_MINERAL_COOKIE =
            ResourceLocation.fromNamespaceAndPath("kubejs", "mineral_cookie");
    private static final ResourceLocation XICE_TORKOAL_COOKIE_COOLDOWN =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "torkoal_mineral_cookie");
    private static final ResourceLocation XICE_TORCHBERRIES =
            ResourceLocation.fromNamespaceAndPath("twilightforest", "torchberries");
    private static final ResourceLocation XICE_CALM_CREAM =
            ResourceLocation.fromNamespaceAndPath("kubejs", "calm_cream");
    private static final ResourceLocation XICE_MILCERY_CREAM_COOLDOWN =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "milcery_calm_cream");
    private static final ResourceLocation XICE_GELATINE =
            ResourceLocation.fromNamespaceAndPath("betterend", "gelatine");
    private static final ResourceLocation XICE_CALCITE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "calcite");
    private static final TagKey<Item> XICE_BRUSHES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "brushes")
    );

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$brushCalciteFromNacliFamily(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(XICE_BRUSHES)) return;
        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) return;

        int count = switch (pokemon.getSpecies().getResourceIdentifier().getPath()) {
            case "nacli" -> 2;
            case "naclstack" -> 5;
            case "garganacl" -> 12;
            default -> 0;
        };
        Item calcite = BuiltInRegistries.ITEM.get(XICE_CALCITE);
        if (count == 0 || calcite == Items.AIR) return;

        if (!entity.level().isClientSide) {
            held.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            xice$giveInteractionResults(player, hand, held, List.of(new ItemStack(calcite, count)));
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.BRUSH_SAND_COMPLETED,
                    SoundSource.PLAYERS, 0.8F, 1.0F);
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$brushSandFromOwnedPokemon(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(XICE_BRUSHES)) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) {
            return;
        }

        String species = pokemon.getSpecies().getResourceIdentifier().getPath();
        if ("milcery".equals(species) || "alcremie".equals(species)) {
            int cooldownTicks = "milcery".equals(species) ? 100 : 0;
            if (cooldownTicks > 0 && pokemon.isOnInteractionCooldown(XICE_MILCERY_CREAM_COOLDOWN)) {
                cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
                return;
            }

            if (!entity.level().isClientSide) {
                held.hurtAndBreak(
                        1,
                        player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
                );
                xice$giveInteractionResults(
                        player,
                        hand,
                        held,
                        List.of(new ItemStack(BuiltInRegistries.ITEM.get(XICE_CALM_CREAM), 2))
                );
                if (cooldownTicks > 0) {
                    pokemon.getInteractionCooldowns().put(XICE_MILCERY_CREAM_COOLDOWN, cooldownTicks);
                }
                entity.level().playSound(
                        null,
                        entity.blockPosition(),
                        SoundEvents.BUBBLE_COLUMN_BUBBLE_POP,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.1F
                );
            }

            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        if ("volcarona".equals(species)) {
            if (!entity.level().isClientSide) {
                held.hurtAndBreak(
                        1,
                        player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
                );
                xice$giveInteractionResults(
                        player,
                        hand,
                        held,
                        List.of(new ItemStack(Items.BLAZE_POWDER, 2 + entity.getRandom().nextInt(2)))
                );

                entity.level().playSound(
                        null,
                        entity.blockPosition(),
                        SoundEvents.BRUSH_SAND_COMPLETED,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.25F
                );
            }

            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        int sandCount = switch (species) {
            case "hippopotas" -> 16;
            case "hippowdon" -> 48;
            case "silicobra" -> 1;
            case "sandaconda" -> 3;
            default -> 0;
        };
        if (sandCount == 0) {
            return;
        }

        int cooldownTicks = switch (species) {
            case "hippopotas", "hippowdon" -> 1200;
            default -> 0;
        };
        if (cooldownTicks > 0 && pokemon.isOnInteractionCooldown(XICE_SAND_BRUSHING_COOLDOWN)) {
            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        if (!entity.level().isClientSide) {
            held.hurtAndBreak(
                    1,
                    player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
            );
            xice$giveInteractionResults(
                    player,
                    hand,
                    held,
                    List.of(new ItemStack(Items.SAND, sandCount))
            );

            if (cooldownTicks > 0) {
                pokemon.getInteractionCooldowns().put(XICE_SAND_BRUSHING_COOLDOWN, cooldownTicks);
            }

            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.BRUSH_SAND_COMPLETED,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$brushGelatineFromGoomyFamily(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(XICE_BRUSHES)) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) {
            return;
        }

        int gelatineCount = switch (pokemon.getSpecies().getResourceIdentifier().getPath()) {
            case "goomy" -> 1;
            case "sliggoo" -> 2;
            case "goodra" -> 2 + entity.getRandom().nextInt(3);
            default -> 0;
        };
        Item gelatine = BuiltInRegistries.ITEM.get(XICE_GELATINE);
        if (gelatineCount == 0 || gelatine == Items.AIR) {
            return;
        }

        if (!entity.level().isClientSide) {
            held.hurtAndBreak(
                    1,
                    player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
            );
            xice$giveInteractionResults(
                    player,
                    hand,
                    held,
                    List.of(new ItemStack(gelatine, gelatineCount))
            );
            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.HONEY_BLOCK_BREAK,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.15F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$feedOwnedKlinkFamily(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!BuiltInRegistries.ITEM.getKey(held.getItem()).equals(XICE_MECHANICAL_NUTRIENT_BLOCK)) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) {
            return;
        }

        List<ItemStack> results = switch (pokemon.getSpecies().getResourceIdentifier().getPath()) {
            case "klink" -> List.of(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "cogwheel")), 2));
            case "klang" -> List.of(
                    new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "cogwheel")), 2),
                    new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "large_cogwheel")), 1)
            );
            case "klinklang" -> List.of(
                    new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "cogwheel")), 3),
                    new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "large_cogwheel")), 2)
            );
            default -> List.of();
        };
        if (results.isEmpty()) {
            return;
        }

        if (!entity.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            xice$giveInteractionResults(player, hand, held, results);
            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.IRON_GOLEM_REPAIR,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.3F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$feedMagnemiteFamily(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!BuiltInRegistries.ITEM.getKey(held.getItem()).equals(XICE_MECHANICAL_NUTRIENT_BLOCK)) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) {
            return;
        }

        String species = pokemon.getSpecies().getResourceIdentifier().getPath();
        int cooldownTicks = switch (species) {
            case "magnemite", "magneton" -> 40;
            case "magnezone" -> 0;
            default -> -1;
        };
        if (cooldownTicks < 0) {
            return;
        }
        if (cooldownTicks > 0 && pokemon.isOnInteractionCooldown(XICE_MAGNETIC_FEED_COOLDOWN)) {
            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        Item crushedZinc = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("create", "crushed_raw_zinc")
        );
        if (crushedZinc == Items.AIR) {
            return;
        }

        List<ItemStack> results = switch (species) {
            case "magnemite" -> List.of(new ItemStack(crushedZinc, 1));
            case "magneton" -> List.of(new ItemStack(crushedZinc, 3));
            case "magnezone" -> {
                List<ItemStack> drops = new java.util.ArrayList<>();
                drops.add(new ItemStack(crushedZinc, 3));
                if (entity.level().random.nextFloat() < 0.15F) {
                    drops.add(new ItemStack(Items.LAPIS_LAZULI, 1));
                }
                if (entity.level().random.nextFloat() < 0.15F) {
                    drops.add(new ItemStack(Items.REDSTONE, 1));
                }
                yield drops;
            }
            default -> List.of();
        };
        if (results.isEmpty()) {
            return;
        }

        if (!entity.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            xice$giveInteractionResults(player, hand, held, results);
            if (cooldownTicks > 0) {
                pokemon.getInteractionCooldowns().put(XICE_MAGNETIC_FEED_COOLDOWN, cooldownTicks);
            }
            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.IRON_GOLEM_REPAIR,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.1F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$feedCoalPokemon(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!BuiltInRegistries.ITEM.getKey(held.getItem()).equals(XICE_MINERAL_COOKIE)) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) {
            return;
        }

        String species = pokemon.getSpecies().getResourceIdentifier().getPath();
        int coalCount = switch (species) {
            case "rolycoly" -> 1;
            case "carkol" -> 3;
            case "coalossal" -> 5;
            case "torkoal" -> 64;
            default -> 0;
        };
        if (coalCount == 0) {
            return;
        }

        if (species.equals("torkoal") && pokemon.isOnInteractionCooldown(XICE_TORKOAL_COOKIE_COOLDOWN)) {
            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        if (!entity.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            xice$giveInteractionResults(
                    player,
                    hand,
                    held,
                    List.of(new ItemStack(Items.COAL, coalCount))
            );

            if (species.equals("torkoal")) {
                pokemon.getInteractionCooldowns().put(XICE_TORKOAL_COOKIE_COOLDOWN, 2400);
            }

            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.PLAYERS,
                    0.8F,
                    species.equals("torkoal") ? 0.75F : 1.15F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$feedVolbeatAndIllumise(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!BuiltInRegistries.ITEM.getKey(held.getItem()).equals(XICE_INSECT_FEED)) {
            return;
        }

        String species = entity.getPokemon().getSpecies().getResourceIdentifier().getPath();
        if (!species.equals("volbeat") && !species.equals("illumise")) {
            return;
        }

        Item torchberries = BuiltInRegistries.ITEM.get(XICE_TORCHBERRIES);
        if (torchberries == Items.AIR) {
            return;
        }

        if (!entity.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            xice$giveInteractionResults(
                    player,
                    hand,
                    held,
                    List.of(new ItemStack(torchberries, 2))
            );
            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.HONEY_BLOCK_BREAK,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.25F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$feedDhelmise(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!BuiltInRegistries.ITEM.getKey(held.getItem()).equals(XICE_GROWTH_GRANULES)) {
            return;
        }

        if (!entity.getPokemon().getSpecies().getResourceIdentifier().getPath().equals("dhelmise")) {
            return;
        }

        Item kelp = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", "kelp"));
        if (kelp == Items.AIR) {
            return;
        }

        if (!entity.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            xice$giveInteractionResults(
                    player,
                    hand,
                    held,
                    List.of(new ItemStack(kelp, 3))
            );
            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.HONEY_BLOCK_BREAK,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.1F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$pressWithOwnedTinkatinkFamily(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) {
            return;
        }

        String species = pokemon.getSpecies().getResourceIdentifier().getPath();
        int cooldownTicks = switch (species) {
            case "tinkatink" -> 100;
            case "tinkatuff" -> 40;
            case "tinkaton" -> 0;
            default -> -1;
        };
        if (cooldownTicks < 0) {
            return;
        }

        SingleRecipeInput input = new SingleRecipeInput(held.copyWithCount(1));
        Optional<RecipeHolder<PressingRecipe>> recipe = SequencedAssemblyRecipe.getRecipe(
                entity.level(),
                input,
                AllRecipeTypes.PRESSING.getType(),
                PressingRecipe.class
        );
        if (recipe.isEmpty()) {
            return;
        }

        if (cooldownTicks > 0 && pokemon.isOnInteractionCooldown(XICE_PRESSING_COOLDOWN)) {
            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        if (!entity.level().isClientSide) {
            List<ItemStack> results = recipe.get().value().rollResults(entity.level().random);
            if (results.isEmpty()) {
                return;
            }

            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            xice$giveInteractionResults(player, hand, held, results);

            if (cooldownTicks > 0) {
                pokemon.getInteractionCooldowns().put(XICE_PRESSING_COOLDOWN, cooldownTicks);
            }

            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.ANVIL_USE,
                    SoundSource.PLAYERS,
                    0.65F,
                    1.15F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$bucketLavaFromOwnedPokemon(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(Items.BUCKET)) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        String species = pokemon.getSpecies().getResourceIdentifier().getPath();
        int cooldownTicks = switch (species) {
            case "slugma" -> 140;
            case "numel" -> 60;
            case "magcargo", "camerupt" -> 0;
            default -> -1;
        };
        if (cooldownTicks < 0) {
            return;
        }

        if (cooldownTicks > 0 && pokemon.isOnInteractionCooldown(XICE_LAVA_BUCKET_COOLDOWN)) {
            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        if (!entity.level().isClientSide) {
            ItemStack lavaBucket = new ItemStack(Items.LAVA_BUCKET);
            if (player.getAbilities().instabuild) {
                if (!player.getInventory().add(lavaBucket)) {
                    player.drop(lavaBucket, false);
                }
            } else {
                held.shrink(1);
                if (held.isEmpty()) {
                    player.setItemInHand(hand, lavaBucket);
                } else if (!player.getInventory().add(lavaBucket)) {
                    player.drop(lavaBucket, false);
                }
            }

            if (cooldownTicks > 0) {
                pokemon.getInteractionCooldowns().put(XICE_LAVA_BUCKET_COOLDOWN, cooldownTicks);
            }

            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.BUCKET_FILL_LAVA,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void xice$bucketMilkFromOwnedPokemon(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(Items.BUCKET)) {
            return;
        }

        Pokemon pokemon = entity.getPokemon();
        if (!xice$isPlayerOwnedPokemon(entity, pokemon)) {
            return;
        }

        String species = pokemon.getSpecies().getResourceIdentifier().getPath();
        if (("skiddo".equals(species) || "gogoat".equals(species))
                && pokemon.getGender() != Gender.FEMALE) {
            return;
        }
        int cooldownTicks = switch (species) {
            case "skiddo" -> 100;
            case "miltank", "gogoat" -> 0;
            default -> -1;
        };
        if (cooldownTicks < 0) {
            return;
        }

        if (cooldownTicks > 0 && pokemon.isOnInteractionCooldown(XICE_MILK_BUCKET_COOLDOWN)) {
            cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
            return;
        }

        if (!entity.level().isClientSide) {
            ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET);
            if (player.getAbilities().instabuild) {
                if (!player.getInventory().add(milkBucket)) {
                    player.drop(milkBucket, false);
                }
            } else {
                held.shrink(1);
                if (held.isEmpty()) {
                    player.setItemInHand(hand, milkBucket);
                } else if (!player.getInventory().add(milkBucket)) {
                    player.drop(milkBucket, false);
                }
            }

            if (cooldownTicks > 0) {
                pokemon.getInteractionCooldowns().put(XICE_MILK_BUCKET_COOLDOWN, cooldownTicks);
            }

            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.BUCKET_FILL,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(entity.level().isClientSide));
    }

    private static boolean xice$isPlayerOwnedPokemon(PokemonEntity entity, Pokemon pokemon) {
        return pokemon.getOwnerUUID() != null
                && pokemon.getOriginalTrainerType() == OriginalTrainerType.PLAYER
                && !entity.isBattleClone();
    }

    private static void xice$giveInteractionResults(
            Player player,
            InteractionHand hand,
            ItemStack remainingHeldStack,
            List<ItemStack> results
    ) {
        int firstResult = 0;
        if (!player.getAbilities().instabuild && remainingHeldStack.isEmpty()) {
            player.setItemInHand(hand, results.getFirst().copy());
            firstResult = 1;
        }

        for (int i = firstResult; i < results.size(); i++) {
            ItemStack result = results.get(i).copy();
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
        }
    }
}
