ServerEvents.recipes(event => {
  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { item: 'twilightforest:torchberries' }
    ],
    results: [
      { amount: 100, id: 'kubejs:torchberry_syrup' }
    ]
  }).id('kubejs:mixing/torchberry_syrup')

  event.custom({
    type: 'create:filling',
    ingredients: [
      { item: 'minecraft:glass_bottle' },
      { type: 'neoforge:single', amount: 250, fluid: 'kubejs:torchberry_syrup' }
    ],
    results: [
      { id: 'kubejs:torchberry_syrup_bottle' }
    ]
  }).id('kubejs:filling/torchberry_syrup_bottle')

  event.custom({
    type: 'create:emptying',
    ingredients: [
      { item: 'kubejs:torchberry_syrup_bottle' }
    ],
    results: [
      { id: 'minecraft:glass_bottle' },
      { amount: 250, id: 'kubejs:torchberry_syrup' }
    ]
  }).id('kubejs:emptying/torchberry_syrup_bottle')
})
