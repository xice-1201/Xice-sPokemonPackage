ServerEvents.recipes(event => {
  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'cobblemon:oran_berry' },
      { item: 'cobblemon:oran_berry' },
      { item: 'minecraft:bone_meal' }
    ],
    results: [
      { id: 'kubejs:growth_granules', count: 3 }
    ]
  }).id('xices_cobblemon_fix:mixing/growth_granules')
})
