({
  num: -10005,
  name: "Sonic Lock",
  type: "Dark",
  category: "Status",
  basePower: 0,
  target: "normal",
  accuracy: 100,
  pp: 20,
  priority: 0,
  flags: { protect: 1, mirror: 1, metronome: 1 },
  onHit: function(target, source) {
    for (var i = 0; i < target.side.active.length; i++) {
      var active = target.side.active[i];
      if (active && active !== target && active.volatiles["xicesoundlock"]) {
        active.removeVolatile("xicesoundlock");
      }
    }
    target.addVolatile("xicesoundlock", source);
  }
})
