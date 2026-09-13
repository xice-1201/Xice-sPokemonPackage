package cn.cnxice.cobblemonfix.combat;

import cn.cnxice.cobblemonfix.XicesCobblemonFix;
import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService;
import net.neoforged.fml.ModList;
import java.nio.file.Files;
import org.slf4j.LoggerFactory;

public final class WardenAbilityRegistration {
    private WardenAbilityRegistration() {}
    public static void register(GraalShowdownService service) {
        var log = LoggerFactory.getLogger("Xice Warden");
        try {
            log.info("[Warden debug] Native ability registration begin service={} thread={}", service.getClass().getName(), Thread.currentThread().getName());
            var path = ModList.get().getModFileById(XicesCobblemonFix.MOD_ID)
                    .getFile().findResource("warden", "dark_shroud.js");
            log.info("[Warden debug] Native ability source={} exists={} size={}", path, Files.exists(path), Files.exists(path) ? Files.size(path) : -1);
            service.sendRegistryEntry(Files.readString(path), "ability");
            log.info("[Warden debug] Native ability registry entry sent id=xicedarkshroud");
            String result = service.getContext().eval("js", """
                (() => {
                  const a = require('./sim/cobblemon/cobblemon').Cobblemon.registries.ability.get('xicedarkshroud');
                  if (!a || a.id !== 'xicedarkshroud' || typeof a.onStart !== 'function' ||
                      typeof a.onAnySwitchIn !== 'function' || typeof a.onSetStatus !== 'function')
                    throw new Error('Warden ability registry verification failed');
                  return a.id;
                })()
                """).asString();
            if (cn.cnxice.cobblemonfix.BattleAttributeConfig.WARDEN_BATTLE_DEBUG_LOG.get())
                log.info("[Warden debug] Native ability verified: {} (entry, switch, status callbacks present)", result);
        } catch (Exception e) {
            log.error("Warden native ability registration failed; effect is NOT available", e);
        }
    }
}
