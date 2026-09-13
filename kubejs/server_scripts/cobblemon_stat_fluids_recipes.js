// 将 Cobblemon 药剂转化为可在机械动力中输送的液体，并制作六种属性提升剂。
ServerEvents.recipes(event => {
  // 移除营火锅药酿配方，统一改用动力搅拌生产。
  event.remove({ id: 'cobblemon:campfire_pot/medicinal_brew_in_campfire_pot' })

  // 药酿：1000mB 水 + 疗草 →（加热搅拌）1000mB 药酿流体。
  event.remove({ id: 'create_klinks_n_klangs:medicinal_brew' })
  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { type: 'neoforge:single', amount: 1000, fluid: 'minecraft:water' },
      { item: 'cobblemon:medicinal_leek' }
    ],
    results: [
      { amount: 1000, id: 'create_klinks_n_klangs:liquid_medicinal_brew' }
    ]
  }).id('xices_cobblemon_fix:mixing/medicinal_brew')

  const fluidItems = [
    ['pp_up', 'cobblemon:pp_up'],
    ['pp_max', 'cobblemon:pp_max'],
    ['hp_up', 'cobblemon:hp_up'],
    ['protein', 'cobblemon:protein'],
    ['iron', 'cobblemon:iron'],
    ['calcium', 'cobblemon:calcium'],
    ['zinc', 'cobblemon:zinc'],
    ['carbos', 'cobblemon:carbos']
  ]

  // Klinks 原配方按 1000mB/瓶，替换为 250mB/瓶的注液与分液。
  fluidItems.forEach(([name, item]) => {
    event.remove({ id: `create_klinks_n_klangs:${name}_fill` })
    event.remove({ id: `create_klinks_n_klangs:${name}_empty` })

    event.custom({
      type: 'create:filling',
      ingredients: [
        { item: 'minecraft:glass_bottle' },
        {
          type: 'fluid_stack',
          amount: 250,
          fluid: `create_klinks_n_klangs:${name}`
        }
      ],
      results: [{ id: item }]
    }).id(`xices_cobblemon_fix:filling/${name}_250mb`)

    event.custom({
      type: 'create:emptying',
      ingredients: [{ item }],
      results: [
        {
          amount: 250,
          id: `create_klinks_n_klangs:${name}`
        },
        { id: 'minecraft:glass_bottle' }
      ]
    }).id(`xices_cobblemon_fix:emptying/${name}_250mb`)
  })

  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { type: 'neoforge:single', amount: 1000, fluid: 'create_klinks_n_klangs:pp_up' },
      { item: 'cobblemon:energy_root' }
    ],
    results: [
      { amount: 1000, id: 'create_klinks_n_klangs:pp_max' }
    ]
  }).id('xices_cobblemon_fix:mixing/pp_max')

  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      {
        type: 'neoforge:single',
        amount: 1000,
        fluid: 'create_klinks_n_klangs:liquid_medicinal_brew'
      },
      { item: 'cobblemon:vivichoke' }
    ],
    results: [
      { amount: 1000, id: 'create_klinks_n_klangs:pp_up' }
    ]
  }).id('xices_cobblemon_fix:mixing/pp_up')

  const statBoosters = [
    ['hp_up', 'cobblemon:pomeg_berry'],
    ['protein', 'cobblemon:kelpsy_berry'],
    ['iron', 'cobblemon:qualot_berry'],
    ['calcium', 'cobblemon:hondew_berry'],
    ['zinc', 'cobblemon:grepa_berry'],
    ['carbos', 'cobblemon:tamato_berry']
  ]

  statBoosters.forEach(([name, berry]) => {
    event.custom({
      type: 'create:mixing',
      heat_requirement: 'heated',
      ingredients: [
        {
          type: 'neoforge:single',
          amount: 1000,
          fluid: 'create_klinks_n_klangs:pp_up'
        },
        { item: berry }
      ],
      results: [
        { amount: 1000, id: `create_klinks_n_klangs:${name}` }
      ]
    }).id(`xices_cobblemon_fix:mixing/${name}_from_pp_up`)
  })
})
