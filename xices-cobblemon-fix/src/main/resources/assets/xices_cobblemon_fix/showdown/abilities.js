"use strict";
// Cobblemon Showdown extension registry. The runtime registration uses the
// same behavior from warden/dark_shroud.js.
const Abilities = {
  xicedarkshroud: {
    num: -10001,
    name: "xicedarkshroud",
    onStart(holder) {
      this.effectState.xiceHolder = holder;
      this.effectState.xiceSeen = new Set();
      let activated = false;
      for (const target of holder.side.foe.active) {
        if (!target || target.fainted) continue;
        this.effectState.xiceSeen.add(target);
        if (!activated) {
          this.add("-ability", holder, "xicedarkshroud", "boost");
          activated = true;
        }
        this.boost({accuracy: -2}, target, holder, null, true);
      }
    },
    onAnySwitchIn(target) {
      const holder = this.effectState.xiceHolder;
      const seen = this.effectState.xiceSeen;
      if (!holder || !seen || !target || target.side === holder.side || target.fainted || seen.has(target)) return;
      seen.add(target);
      this.add("-ability", holder, "xicedarkshroud", "boost");
      this.boost({accuracy: -2}, target, holder, null, true);
    },
    onAnySwitchOut(target) {
      if (this.effectState.xiceSeen && target) this.effectState.xiceSeen.delete(target);
    },
    onSetStatus(status, target) {
      const holder = this.effectState.xiceHolder;
      if (holder && target === holder && status && (status.id === "par" || status.id === "slp")) {
        this.add("-immune", target, "[from] ability: xicedarkshroud");
        return false;
      }
    },
    onDamage(damage, target, source, effect) {
      if (target === this.effectState.xiceHolder) return this.chainModify(0.1);
    }
  }
};
module.exports = { Abilities };
