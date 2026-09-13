ServerEvents.recipes(event => {
  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'minecraft:rotten_flesh' },
      { item: 'minecraft:rotten_flesh' },
      { item: 'betterend:gelatine' },
      { item: 'minecraft:bone_meal' }
    ],
    results: [{ id: 'kubejs:inactive_essence' }]
  }).id('xices_cobblemon_fix:mixing/inactive_essence')

  event.custom({
    type: 'create:haunting',
    ingredients: [{ item: 'kubejs:inactive_essence' }],
    results: [{ id: 'twilightforest:exanimate_essence' }]
  }).id('xices_cobblemon_fix:haunting/inactive_essence_to_exanimate_essence')
})
