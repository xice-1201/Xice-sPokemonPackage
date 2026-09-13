ServerEvents.recipes(event => {
  // Any plank can be processed into two Wooden Mechanisms.  The one-percent
  // byproduct branch produces eight Wood Chips instead.
  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: {
      tag: 'minecraft:planks'
    },
    loops: 2,
    results: [
      { chance: 99.0, id: 'createages:wooden_mechanism', count: 2 },
      { chance: 1.0, id: 'createdieselgenerators:wood_chip', count: 8 }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_wooden_mechanism' },
          { item: 'create:cogwheel' }
        ],
        results: [
          { id: 'createages:incomplete_wooden_mechanism' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_wooden_mechanism' },
          { tag: 'minecraft:wooden_buttons' }
        ],
        results: [
          { id: 'createages:incomplete_wooden_mechanism' }
        ]
      },
      {
        type: 'create:cutting',
        ingredients: [
          { item: 'createages:incomplete_wooden_mechanism' }
        ],
        processing_time: 50,
        results: [
          { id: 'createages:incomplete_wooden_mechanism' }
        ]
      }
    ],
    transitional_item: {
      id: 'createages:incomplete_wooden_mechanism'
    }
  }).id('xices_cobblemon_fix:wooden_mechanism_sequenced_assembly')

  // Zinc Mechanisms use zinc ingots as the base and consume iron nuggets and
  // two Wooden Mechanisms during the sequenced assembly.
  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: {
      item: 'create:zinc_ingot'
    },
    loops: 1,
    results: [
      { chance: 100.0, id: 'createages:zinc_mechanism' }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_mechanism' },
          { item: 'minecraft:iron_nugget' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_mechanism' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_mechanism' },
          { item: 'createages:wooden_mechanism' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_mechanism' }
        ]
      },
      {
        type: 'create:pressing',
        ingredients: [
          { item: 'createages:incomplete_zinc_mechanism' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_mechanism' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_mechanism' },
          { item: 'createages:wooden_mechanism' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_mechanism' }
        ]
      }
    ],
    transitional_item: {
      id: 'createages:incomplete_zinc_mechanism'
    }
  }).id('xices_cobblemon_fix:zinc_mechanism_sequenced_assembly')

  event.custom({
    type: 'create:crushing',
    ingredients: [
      { item: 'minecraft:iron_ingot' }
    ],
    processing_time: 100,
    results: [
      { id: 'cobblemon_workforce:iron_dust', count: 2 }
    ]
  }).id('xices_cobblemon_fix:crushing/iron_dust')

  for (const [id, name] of [
    ['minecraft:coal', 'coal'],
    ['minecraft:charcoal', 'charcoal']
  ]) {
    event.custom({
      type: 'create:crushing',
      ingredients: [
        { item: id }
      ],
      processing_time: 100,
      results: [
        { id: 'kubejs:carbon_powder', count: 4 }
      ]
    }).id(`xices_cobblemon_fix:crushing/carbon_powder_from_${name}`)
  }

  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { item: 'cobblemon_workforce:iron_dust' },
      { item: 'kubejs:carbon_powder' }
    ],
    results: [
      { id: 'twilightforest:wrought_iron_bar', count: 1 }
    ]
  }).id('xices_cobblemon_fix:mixing/wrought_iron')

  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: {
      item: 'createages:zinc_casing'
    },
    loops: 1,
    results: [
      { chance: 95.0, id: 'createages:zinc_machine', count: 2 },
      { chance: 4.0, id: 'createages:zinc_casing', count: 1 },
      { chance: 1.0, id: 'createages:zinc_machine', count: 5 }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_machine' },
          { item: 'createages:zinc_mechanism' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_machine' }
        ]
      },
      {
        type: 'create:cutting',
        ingredients: [
          { item: 'createages:incomplete_zinc_machine' }
        ],
        processing_time: 50,
        results: [
          { id: 'createages:incomplete_zinc_machine' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_machine' },
          { item: 'twilightforest:wrought_iron_bar' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_machine' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_machine' },
          { item: 'kubejs:carbon_powder' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_machine' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_machine' },
          { item: 'create:iron_sheet' }
        ],
        results: [
          { id: 'createages:incomplete_zinc_machine' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'createages:incomplete_zinc_machine' },
          { item: 'createages:zinc_hand' }
        ],
        keep_held_item: true,
        results: [
          { id: 'createages:incomplete_zinc_machine' }
        ]
      }
    ],
    transitional_item: {
      id: 'createages:incomplete_zinc_machine'
    }
  }).id('xices_cobblemon_fix:zinc_machine_sequenced_assembly')
})
