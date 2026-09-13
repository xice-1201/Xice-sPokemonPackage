ServerEvents.recipes(event => {
  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'cobblemon:oran_berry' },
      { item: 'cobblemon:oran_berry' },
      { tag: 'cobblemon:mint_seeds' }
    ],
    results: [
      { id: 'kubejs:insect_feed', count: 3 }
    ]
  }).id('xices_cobblemon_fix:mixing/insect_feed')
})
