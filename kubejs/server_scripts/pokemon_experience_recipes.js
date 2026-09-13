// 宝可梦经验：经验糖果生产线
ServerEvents.recipes(event => {
  // 经验糖果 XS 只允许通过本章节指定的液态经验注液路线获取。
  // 经验糖果 S 的配方来源与后续升级/拆分路径也由整合包单独设计。
  // 宝可梦掉落来源属于战利品表，不在这里移除。
  event.remove({ output: 'cobblemon:exp_candy_xs' })
  event.remove({ input: 'cobblemon:exp_candy_xs' })
  event.remove({ output: 'cobblemon:exp_candy_s' })
  event.remove({ input: 'cobblemon:exp_candy_s' })
  event.remove({ output: 'cobblemon:exp_candy_m' })
  event.remove({ input: 'cobblemon:exp_candy_m' })
  event.remove({ output: 'cobblemon:exp_candy_l' })
  event.remove({ input: 'cobblemon:exp_candy_l' })
  event.remove({ output: 'cobblemon:exp_candy_xl' })
  event.remove({ input: 'cobblemon:exp_candy_xl' })

  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { item: 'twilightforest:essence_berry' }
    ],
    results: [
      { amount: 27, id: 'create_enchantment_industry:experience' }
    ]
  }).id('xices_cobblemon_fix:mixing/experience_from_essence_berry')

  event.custom({
    type: 'create:filling',
    ingredients: [
      { item: 'minecraft:honeycomb' },
      { type: 'neoforge:single', amount: 27, fluid: 'create_enchantment_industry:experience' }
    ],
    results: [
      { id: 'cobblemon:exp_candy_xs', count: 9 }
    ]
  }).id('xices_cobblemon_fix:filling/exp_candy_xs')

  event.custom({
    type: 'create:filling',
    ingredients: [
      { tag: 'xices_cobblemon_fix:twilight_forest_nether_berries' },
      { type: 'neoforge:single', amount: 1000, fluid: 'create_enchantment_industry:experience' }
    ],
    results: [
      { id: 'twilightforest:essence_berry' }
    ]
  }).id('xices_cobblemon_fix:filling/essence_berry_from_twilight_nether_berry')

  event.custom({
    type: 'create:filling',
    ingredients: [
      { tag: 'xices_cobblemon_fix:twilight_forest_overworld_berries' },
      { type: 'neoforge:single', amount: 1000, fluid: 'create_enchantment_industry:experience' }
    ],
    results: [
      { id: 'twilightforest:essence_berry' }
    ]
  }).id('xices_cobblemon_fix:filling/essence_berry_from_twilight_overworld_berry')

  event.custom({
    type: 'create:filling',
    ingredients: [
      { tag: 'cobblemon:berries' },
      { type: 'neoforge:single', amount: 1000, fluid: 'create_enchantment_industry:experience' }
    ],
    results: [
      { id: 'twilightforest:essence_berry' }
    ]
  }).id('xices_cobblemon_fix:filling/essence_berry_from_cobblemon_berry')

  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'cobblemon:exp_candy_xs' },
      { item: 'cobblemon:exp_candy_xs' },
      { item: 'cobblemon:exp_candy_xs' },
      { item: 'cobblemon:exp_candy_xs' },
      {
        type: 'fluid_stack',
        fluid: 'create:potion',
        components: {
          'minecraft:potion_contents': { potion: 'minecraft:strength' }
        },
        amount: 250
      }
    ],
    results: [
      { id: 'cobblemon:exp_candy_s' }
    ]
  }).id('xices_cobblemon_fix:mixing/exp_candy_s')

  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { item: 'cobblemon:exp_candy_s' },
      { item: 'cobblemon:exp_candy_s' },
      { item: 'minecraft:sugar' },
      { item: 'minecraft:sugar' },
      { item: 'minecraft:sugar' },
      { item: 'minecraft:sugar' },
      { type: 'neoforge:single', fluid: 'create:chocolate', amount: 500 }
    ],
    results: [
      { id: 'cobblemon:exp_candy_m' }
    ]
  }).id('xices_cobblemon_fix:mixing/exp_candy_m')

  event.custom({
    type: 'create:mixing',
    ingredients: [
      { item: 'kubejs:calm_cream' }
    ],
    results: [
      { id: 'minecraft:sugar', count: 16 }
    ]
  }).id('xices_cobblemon_fix:mixing/sugar_from_calm_cream')

  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { item: 'cobblemon:exp_candy_m' },
      { item: 'cobblemon:exp_candy_m' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:custap_berry' },
      { item: 'cobblemon:energy_root' },
      { item: 'cobblemon:energy_root' },
      { item: 'cobblemon:energy_root' },
      { item: 'cobblemon:energy_root' }
    ],
    results: [
      { id: 'cobblemon:exp_candy_l' }
    ]
  }).id('xices_cobblemon_fix:mixing/exp_candy_l')

  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: {
      item: 'cobblemon:exp_candy_l'
    },
    loops: 1,
    results: [
      { chance: 50.0, id: 'cobblemon:exp_candy_xl', count: 3 },
      { chance: 50.0, id: 'cobblemon:exp_candy_xl', count: 1 }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:exp_candy_l' },
          { item: 'cobblemon:exp_candy_l' }
        ],
        results: [
          { id: 'cobblemon:exp_candy_l' }
        ]
      },
      {
        type: 'create:filling',
        ingredients: [
          { item: 'cobblemon:exp_candy_l' },
          { type: 'neoforge:single', amount: 200, fluid: 'kubejs:torchberry_syrup' }
        ],
        results: [
          { id: 'cobblemon:exp_candy_l' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:exp_candy_l' },
          { item: 'cobblemon:exp_candy_l' }
        ],
        results: [
          { id: 'cobblemon:exp_candy_l' }
        ]
      },
      {
        type: 'create:pressing',
        ingredients: [
          { item: 'cobblemon:exp_candy_l' }
        ],
        results: [
          { id: 'cobblemon:exp_candy_l' }
        ]
      }
    ],
    transitional_item: {
      id: 'cobblemon:exp_candy_l'
    }
  }).id('xices_cobblemon_fix:sequenced_assembly/exp_candy_xl')

  event.shapeless(
    Item.of('create_klinks_n_klangs:wrapper', 2),
    [
      Item.of('minecraft:paper', 2),
      Item.of('minecraft:sugar', 2)
    ]
  ).id('xices_cobblemon_fix:crafting/wrapper')

  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: {
      item: 'cobblemon:exp_candy_xl'
    },
    loops: 1,
    results: [
      { id: 'cobblemon:rare_candy', count: 2 }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:exp_candy_xl' },
          { item: 'create_enchantment_industry:super_experience_block' }
        ],
        keep_held_item: true,
        results: [
          { id: 'cobblemon:exp_candy_xl' }
        ]
      },
      {
        type: 'create:cutting',
        ingredients: [
          { item: 'cobblemon:exp_candy_xl' }
        ],
        processing_time: 50,
        results: [
          { id: 'cobblemon:exp_candy_xl' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:exp_candy_xl' },
          { item: 'create_klinks_n_klangs:wrapper' }
        ],
        results: [
          { id: 'cobblemon:exp_candy_xl' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:exp_candy_xl' },
          { item: 'create_klinks_n_klangs:wrapper' }
        ],
        results: [
          { id: 'cobblemon:exp_candy_xl' }
        ]
      },
      {
        type: 'create:pressing',
        ingredients: [
          { item: 'cobblemon:exp_candy_xl' }
        ],
        results: [
          { id: 'cobblemon:exp_candy_xl' }
        ]
      }
    ],
    transitional_item: {
      id: 'cobblemon:exp_candy_xl'
    }
  }).id('xices_cobblemon_fix:sequenced_assembly/rare_candy')

  // 神奇糖果可通过机械手使用荆棘玫瑰，转化为普通糖果。
  event.custom({
    type: 'create:deploying',
    ingredients: [
      { item: 'cobblemon:rare_candy' },
      { item: 'twilightforest:thorn_rose' }
    ],
    results: [
      { id: 'cobblemon_utility:commoncandy' }
    ]
  }).id('xices_cobblemon_fix:deploying/rare_candy_to_common_candy')

  event.custom({
    type: 'create:sequenced_assembly',
    ingredient: { item: 'cobblemon:rare_candy' },
    loops: 1,
    results: [
      { chance: 50.0, id: 'cobblemon_utility:mastercandy' },
      { chance: 49.0, id: 'cobblemon:rare_candy' },
      { chance: 1.0, id: 'cobblemon_utility:commoncandy' }
    ],
    sequence: [
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:rare_candy' },
          { item: 'betterend:sweet_berry_jelly' }
        ],
        results: [
          { id: 'cobblemon:rare_candy' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:rare_candy' },
          { item: 'betterend:shadow_berry_jelly' }
        ],
        results: [
          { id: 'cobblemon:rare_candy' }
        ]
      },
      {
        type: 'create:deploying',
        ingredients: [
          { item: 'cobblemon:rare_candy' },
          { item: 'betterend:blossom_berry_jelly' }
        ],
        results: [
          { id: 'cobblemon:rare_candy' }
        ]
      }
    ],
    transitional_item: { id: 'cobblemon:rare_candy' }
  }).id('xices_cobblemon_fix:sequenced_assembly/rare_candy_to_utility_candies')
})
