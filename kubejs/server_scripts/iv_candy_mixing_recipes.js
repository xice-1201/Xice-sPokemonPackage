// 六种个体值糖果：对应黏糕 + 火炬浆果稠液 + 植物油。
ServerEvents.recipes(event => {
  const candies = [
    ['health', 'cobblemon:health_mochi', 'cobblemon:health_candy'],
    ['mighty', 'cobblemon:muscle_mochi', 'cobblemon:mighty_candy'],
    ['tough', 'cobblemon:resist_mochi', 'cobblemon:tough_candy'],
    ['smart', 'cobblemon:genius_mochi', 'cobblemon:smart_candy'],
    ['courage', 'cobblemon:clever_mochi', 'cobblemon:courage_candy'],
    ['quick', 'cobblemon:swift_mochi', 'cobblemon:quick_candy']
  ]

  candies.forEach(([name, mochi, candy]) => {
    event.custom({
      type: 'create:mixing',
      heat_requirement: 'heated',
      ingredients: [
        { item: mochi },
        { type: 'neoforge:single', amount: 500, fluid: 'kubejs:torchberry_syrup' },
        { type: 'neoforge:single', amount: 50, fluid: 'createdieselgenerators:plant_oil' }
      ],
      results: [{ id: candy, count: 2 }]
    }).id(`xices_cobblemon_fix:mixing/${name}_candy_from_mochi`)
  })
})
