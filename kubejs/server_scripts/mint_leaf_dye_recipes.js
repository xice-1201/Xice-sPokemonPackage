// 薄荷叶染色：任意薄荷叶可用染料转换为指定颜色。
ServerEvents.recipes(event => {
  const recipes = [
    ['red', 'minecraft:red_dye', 'cobblemon:red_mint_leaf'],
    ['blue', 'minecraft:blue_dye', 'cobblemon:blue_mint_leaf'],
    ['white', 'minecraft:white_dye', 'cobblemon:white_mint_leaf'],
    ['cyan', 'minecraft:cyan_dye', 'cobblemon:cyan_mint_leaf'],
    ['pink', 'minecraft:pink_dye', 'cobblemon:pink_mint_leaf'],
    ['green', 'minecraft:green_dye', 'cobblemon:green_mint_leaf']
  ]

  recipes.forEach(([name, dye, output]) => {
    event.shapeless(output, [
      { tag: 'cobblemon:mint_leaves' },
      dye
    ]).id(`xices_cobblemon_fix:crafting/${name}_mint_leaf_from_dye`)
  })
})
