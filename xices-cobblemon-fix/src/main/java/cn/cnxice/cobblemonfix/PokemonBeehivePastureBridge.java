package cn.cnxice.cobblemonfix;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pasture.PastureLink;
import com.cobblemon.mod.common.api.pasture.PastureLinkManager;
import com.cobblemon.mod.common.api.pasture.PasturePermissions;
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket;
import com.cobblemon.mod.common.net.messages.server.pasture.PasturePokemonPacket;
import com.cobblemon.mod.common.net.messages.server.pasture.UnpastureAllPokemonPacket;
import com.cobblemon.mod.common.net.messages.server.pasture.UnpasturePokemonPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public final class PokemonBeehivePastureBridge {
    public static void open(ServerPlayer player, BlockPos pos, PokemonBeehiveBlockEntity be) {
        var pc = Cobblemon.INSTANCE.getStorage().getPC(player);
        UUID linkId = UUID.randomUUID();
        PasturePermissions permissions = permissions(be);
        new OpenPasturePacket(pc.getUuid(), linkId, 1, be.toPastureDtos(player.serverLevel()), permissions).sendToPlayer(player);
        PastureLinkManager.createLink(player.getUUID(), new PastureLink(linkId, pc.getUuid(),
                player.level().dimension().location(), pos, permissions));
    }

    public static boolean handle(PasturePokemonPacket packet, MinecraftServer server, ServerPlayer player) {
        PastureLink link = PastureLinkManager.getLinkByPlayer(player);
        if (link == null || !link.getLinkId().equals(packet.getPastureId())) return false;
        if (!(player.level().getBlockEntity(link.getPos()) instanceof PokemonBeehiveBlockEntity be)) return false;
        var pc = Cobblemon.INSTANCE.getStorage().getPC(link.getPcId(), player.registryAccess());
        Pokemon pokemon = pc.get(packet.getPokemonId());
        if (pokemon == null || pokemon.isFainted() || !be.canAddPokemon(pokemon)) return true;
        be.addPokemon(player, pokemon, pc.getUuid(), link.getLinkId());
        sync(player, link, be);
        return true;
    }

    public static boolean handle(UnpasturePokemonPacket packet, MinecraftServer server, ServerPlayer player) {
        PastureLink link = PastureLinkManager.getLinkByPlayer(player);
        if (link == null || !link.getLinkId().equals(packet.getPastureId())) return false;
        if (!(player.level() instanceof ServerLevel level)
                || !(level.getBlockEntity(link.getPos()) instanceof PokemonBeehiveBlockEntity be)) return false;
        be.removePokemon(packet.getPokemonId(), level);
        sync(player, link, be);
        return true;
    }

    public static boolean handle(UnpastureAllPokemonPacket packet, MinecraftServer server, ServerPlayer player) {
        PastureLink link = PastureLinkManager.getLinkByPlayer(player);
        if (link == null || !link.getLinkId().equals(packet.getPastureId())) return false;
        if (!(player.level() instanceof ServerLevel level)
                || !(level.getBlockEntity(link.getPos()) instanceof PokemonBeehiveBlockEntity be)) return false;
        be.removeAllPokemon(level);
        sync(player, link, be);
        return true;
    }

    private static void sync(ServerPlayer player, PastureLink link, PokemonBeehiveBlockEntity be) {
        PasturePermissions permissions = permissions(be);
        new OpenPasturePacket(link.getPcId(), link.getLinkId(), 1,
                be.toPastureDtos(player.serverLevel()), permissions).sendToPlayer(player);
    }

    private static PasturePermissions permissions(PokemonBeehiveBlockEntity be) {
        return new PasturePermissions(false, true, 4);
    }

    private PokemonBeehivePastureBridge() {}
}
