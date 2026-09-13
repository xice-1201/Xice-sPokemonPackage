// 调整肉类标签，并用发酵将腐肉增殖。
ServerEvents.tags('item', event => {
  event.remove('minecraft:meat', 'minecraft:rotten_flesh')
  event.remove('minecraft:meat', 'twilightforest:monster_jerky')

  event.add('minecraft:meat', [
    'minecraft:cod',
    'minecraft:salmon',
    'minecraft:tropical_fish',
    'minecraft:pufferfish'
  ])
})

ServerEvents.recipes(event => {
  // 任意肉类经鼓风机缠魂可获得腐肉。
  event.custom({
    type: 'create:haunting',
    ingredients: [{ tag: 'minecraft:meat' }],
    results: [{ id: 'minecraft:rotten_flesh' }]
  }).id('xices_cobblemon_fix:haunting/meat_to_rotten_flesh')

  // 腐肉与增长颗粒在发酵盆中增殖。
  event.custom({
    type: 'createdieselgenerators:basin_fermenting',
    ingredients: [
      { item: 'minecraft:rotten_flesh' },
      { item: 'kubejs:growth_granules' }
    ],
    processing_time: 400,
    results: [{ id: 'minecraft:rotten_flesh', count: 2 }]
  }).id('xices_cobblemon_fix:basin_fermenting/rotten_flesh')
})
