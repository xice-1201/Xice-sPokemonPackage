package cn.cnxice.cobblemonfix.mixin;
import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService;
import com.cobblemon.mod.common.battles.ShowdownThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.nio.charset.StandardCharsets;
@Mixin(value=ShowdownThread.class, remap=false)
public abstract class WardenNativeAbilityMixin {
 private static final org.slf4j.Logger LOG=org.slf4j.LoggerFactory.getLogger("Xice Warden");
 @Inject(method="run",at=@At(value="INVOKE",target="Lcom/cobblemon/mod/common/battles/runner/ShowdownService;openConnection()V",shift=org.spongepowered.asm.mixin.injection.At.Shift.AFTER),remap=false)
 private void xice$register(CallbackInfo ci){
  LOG.info("[Warden debug] ShowdownThread reached post-openConnection hook thread={}", Thread.currentThread().getName());
  if (Thread.currentThread().getName().contains("Cobblemon Showdown")) {
   var service=com.cobblemon.mod.common.battles.runner.ShowdownService.Companion.getService();
   LOG.info("[Warden debug] Showdown service implementation={}", service == null ? "null" : service.getClass().getName());
   if (service instanceof GraalShowdownService graal) cn.cnxice.cobblemonfix.combat.WardenAbilityRegistration.register(graal);
   else LOG.warn("[Warden debug] Showdown service is not GraalShowdownService; native ability not registered");
  }
 }
}
