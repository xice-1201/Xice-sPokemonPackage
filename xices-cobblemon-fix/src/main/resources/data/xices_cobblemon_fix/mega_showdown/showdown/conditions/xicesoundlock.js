({
  name: "xicesoundlock",
  noCopy: true,
  duration: 4,
  onStart: function(target, source) {
    this.add("-activate", source, "move: Sonic Lock", "[of] " + target);
  },
  onEnd: function(target) {},
  onSwitchOut: function(target) {
    target.removeVolatile("xicesoundlock");
  },
  onFoeModifyPriority: function(priority, attacker, source, move) {
    if (move && move.category !== "Status" && move.target === "normal") return priority + 1;
  },
  onEffectiveness: function(typeMod, target, type, move) {
    if (typeMod === -2 && move && move.category !== "Status" && move.target === "normal") return -1;
  }
})
