package cn.cnxice.cobblemonfix.mixin;
import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.LoggerFactory;
@Mixin(value=GraalShowdownService.class, remap=false)
public abstract class GraalStartBattleDebugMixin {
 @Inject(method="startBattle", at=@At("HEAD"), remap=false)
 private void xice$inspect(Object battle, String[] messages, CallbackInfo ci) {
  var l=LoggerFactory.getLogger("Xice Warden");
  l.info("[Warden debug] startBattle invoked battle={} messageCount={}", battle, messages == null ? -1 : messages.length);
  if(messages!=null) for(String m:messages) if(m!=null) l.info("[Warden debug] showdown startBattle message: {}",m);
 }
}
