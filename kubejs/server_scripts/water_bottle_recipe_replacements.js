// 将指定配方中的“任意/不可合成药水”统一替换为普通水瓶。
ServerEvents.recipes(event => {
  // 1.21.1 使用数据组件；字符串语法同时适用于物品堆和配方材料。
  // 旧版 Item.of(id, nbt) 会把第二个参数当作数量，导致脚本报错。
  const waterBottle = "minecraft:potion[potion_contents={potion:'minecraft:water'}]"
  const waterBottleIngredient = {
    type: 'neoforge:components',
    items: 'minecraft:potion',
    components: {
      'minecraft:potion_contents': {
        potion: 'minecraft:water'
      }
    }
  }

  // Cobblemon 药酿：保留其自定义酿造台配方类型，仅固定瓶子为水瓶。
  // 只移除 Cobblemon 酿药台中的原配方，不能按产物整体移除，
  // 否则会连带删除 Klinks n' Klangs 的 Create 注液配方。
  event.remove({
    type: 'cobblemon:brewing_stand',
    output: 'cobblemon:medicinal_brew'
  })
  event.custom({
    type: 'cobblemon:brewing_stand',
    input: { item: 'cobblemon:medicinal_leek' },
    bottle: waterBottleIngredient,
    result: { id: 'cobblemon:medicinal_brew' }
  }).id('xices_cobblemon_fix:brewing_stand/medicinal_brew_water_bottle')

  // 无序合成：苔藓汤与三种浆果果冻。
  const shapelessRecipes = [
    {
      id: 'twilightforest:moss_soup',
      output: 'twilightforest:moss_soup',
      ingredients: [
        'twilightforest:moss_patch',
        'minecraft:bowl',
        waterBottle
      ]
    },
    // 三种浆果果冻不再提供无序合成，仅保留下方的 Create 搅拌配方。
  ]

  shapelessRecipes.forEach(recipe => {
    // 按产物整体移除，覆盖原配方以及因配方解析失败而回退生成的副本。
    event.remove({ output: recipe.output })
    event.shapeless(recipe.output, recipe.ingredients)
      .id(`xices_cobblemon_fix:crafting/${recipe.id.replace(':', '_')}_water_bottle`)
  })

  // 删除三种浆果果冻的所有无序合成来源，仅保留下方的 Create 搅拌配方。
  ;['betterend:sweet_berry_jelly', 'betterend:shadow_berry_jelly', 'betterend:blossom_berry_jelly']
    .forEach(output => event.remove({ output }))

  // 将三种果冻的 Create 配方改为消耗250mB水。
  const mixingRecipes = [
    {
      id: 'sweet_berry_jelly',
      berry: 'minecraft:sweet_berries',
      output: 'betterend:sweet_berry_jelly'
    },
    {
      id: 'shadow_berry_jelly',
      berry: 'betterend:shadow_berry_cooked',
      output: 'betterend:shadow_berry_jelly'
    },
    {
      id: 'blossom_berry_jelly',
      berry: 'betterend:blossom_berry',
      output: 'betterend:blossom_berry_jelly'
    }
  ]

  mixingRecipes.forEach(recipe => {
    event.remove({ type: 'create:mixing', output: recipe.output })
    event.custom({
      type: 'create:mixing',
      ingredients: [
        { item: recipe.berry },
        { item: 'minecraft:sugar' },
        { item: 'betterend:gelatine' },
        { type: 'neoforge:single', amount: 250, fluid: 'minecraft:water' }
      ],
      results: [
        { id: recipe.output }
      ]
    }).id(`xices_cobblemon_fix:mixing/${recipe.id}_water_fluid`)
  })
})
