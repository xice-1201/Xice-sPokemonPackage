ServerEvents.recipes(event => {
  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'cobblemon:oran_berry' },
      { item: 'cobblemon:oran_berry' },
      { item: 'minecraft:iron_nugget' }
    ],
    results: [
      { id: 'kubejs:mechanical_nutrient_block', count: 3 }
    ]
  }).id('kubejs:mixing/mechanical_nutrient_block')
})
