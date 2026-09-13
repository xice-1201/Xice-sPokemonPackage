ServerEvents.recipes(event => {
  // Create: Ages replaces these two vanilla Create recipes with copper-template
  // smithing recipes. Remove every acquisition/use of that template and restore
  // the recipes shipped by Create 6.
  event.remove({ input: 'createages:copper_template' })
  event.remove({ output: 'createages:copper_template' })

  event.shaped('create:hose_pulley', [
    'B',
    'C',
    'I'
  ], {
    B: 'create:copper_casing',
    C: 'minecraft:dried_kelp_block',
    I: '#c:plates/copper'
  }).id('create:crafting/kinetics/hose_pulley')

  event.shaped('create:spout', [
    'T',
    'P'
  ], {
    T: 'create:copper_casing',
    P: 'minecraft:dried_kelp'
  }).id('create:crafting/kinetics/spout')

  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'create:crushed_raw_copper' },
      { type: 'neoforge:single', amount: 250, fluid: 'create:honey' }
    ],
    results: [
      { id: 'create:veridium', count: 4 }
    ]
  }).id('xices_cobblemon_fix:veridium_by_mixing')

  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'minecraft:cobblestone' },
      { item: 'create:crushed_raw_zinc' },
      { type: 'neoforge:single', amount: 250, fluid: 'createdieselgenerators:light_blue_cement' }
    ],
    results: [
      { id: 'create:asurine', count: 16 }
    ]
  }).id('xices_cobblemon_fix:blue_stone_by_mixing')

  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: {
      item: 'create:copper_sheet'
    },
    loops: 3,
    results: [
      { id: 'createages:copper_mechanism' }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_copper_mechanism' },
          { item: 'create:copper_nugget' }
        ],
        results: [
          { id: 'createages:incomplete_copper_mechanism' }
        ]
      },
      {
        type: 'create:filling',
        ingredients: [
          { item: 'createages:incomplete_copper_mechanism' },
          { type: 'neoforge:single', amount: 100, fluid: 'kubejs:torchberry_syrup' }
        ],
        results: [
          { id: 'createages:incomplete_copper_mechanism' }
        ]
      },
      {
        type: 'create:pressing',
        ingredients: [
          { item: 'createages:incomplete_copper_mechanism' }
        ],
        results: [
          { id: 'createages:incomplete_copper_mechanism' }
        ]
      }
    ],
    transitional_item: {
      id: 'createages:incomplete_copper_mechanism'
    }
  }).id('xices_cobblemon_fix:copper_mechanism_sequenced_assembly')

  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: {
      item: 'create:copper_casing'
    },
    results: [
      { chance: 80.0, id: 'createages:copper_machine' },
      { chance: 10.0, id: 'createages:copper_mechanism' },
      { chance: 10.0, id: 'create:copper_casing' }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_copper_machine' },
          { item: 'createages:copper_mechanism' }
        ],
        results: [
          { id: 'createages:incomplete_copper_machine' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_copper_machine' },
          { item: 'minecraft:glass_pane' }
        ],
        results: [
          { id: 'createages:incomplete_copper_machine' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_copper_machine' },
          { item: 'minecraft:dried_kelp' }
        ],
        results: [
          { id: 'createages:incomplete_copper_machine' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_copper_machine' },
          { item: 'minecraft:honeycomb' }
        ],
        results: [
          { id: 'createages:incomplete_copper_machine' }
        ]
      },
      {
        type: 'create:pressing',
        ingredients: [
          { item: 'createages:incomplete_copper_machine' }
        ],
        results: [
          { id: 'createages:incomplete_copper_machine' }
        ]
      }
    ],
    transitional_item: {
      id: 'createages:incomplete_copper_machine'
    }
  }).id('xices_cobblemon_fix:copper_machine_sequenced_assembly')
})
