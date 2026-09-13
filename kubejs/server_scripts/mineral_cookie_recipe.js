ServerEvents.recipes(event => {
  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'cobblemon:oran_berry' },
      { item: 'cobblemon:oran_berry' },
      { item: 'kubejs:carbon_powder' }
    ],
    results: [
      { id: 'kubejs:mineral_cookie', count: 3 }
    ]
  }).id('xices_cobblemon_fix:mixing/mineral_cookie')
})
