StartupEvents.registry('fluid', event => {
  event.create('torchberry_syrup', 'thick')
    .displayName('火炬浆果稠液')
    .tint(0xFFFFA52A)
    // Match vanilla lava's horizontal search, level drop and update interval.
    .slopeFindDistance(2)
    .levelDecreasePerBlock(2)
    .tickRate(30)
    .type(type => {
      type.descriptionId('fluid_type.kubejs.torchberry_syrup')
        .lightLevel(7)
        .density(3000)
        .viscosity(6000)
        .motionScale(0.0023333333333333335)
        .canSwim(false)
        .canDrown(false)
    })
})

StartupEvents.registry('item', event => {
  event.create('torchberry_syrup_bottle')
    .displayName('火炬浆果稠液瓶')
    .color(0, 0xFFE2E0D8)
    .maxStackSize(16)
})
