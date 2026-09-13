ServerEvents.recipes(event => {
  const variants = [
    ['clear', 'minecraft:glass', 'minecraft:glass_pane'],
    ['white', 'minecraft:white_stained_glass', 'minecraft:white_stained_glass_pane'],
    ['orange', 'minecraft:orange_stained_glass', 'minecraft:orange_stained_glass_pane'],
    ['magenta', 'minecraft:magenta_stained_glass', 'minecraft:magenta_stained_glass_pane'],
    ['light_blue', 'minecraft:light_blue_stained_glass', 'minecraft:light_blue_stained_glass_pane'],
    ['yellow', 'minecraft:yellow_stained_glass', 'minecraft:yellow_stained_glass_pane'],
    ['lime', 'minecraft:lime_stained_glass', 'minecraft:lime_stained_glass_pane'],
    ['pink', 'minecraft:pink_stained_glass', 'minecraft:pink_stained_glass_pane'],
    ['gray', 'minecraft:gray_stained_glass', 'minecraft:gray_stained_glass_pane'],
    ['light_gray', 'minecraft:light_gray_stained_glass', 'minecraft:light_gray_stained_glass_pane'],
    ['cyan', 'minecraft:cyan_stained_glass', 'minecraft:cyan_stained_glass_pane'],
    ['purple', 'minecraft:purple_stained_glass', 'minecraft:purple_stained_glass_pane'],
    ['blue', 'minecraft:blue_stained_glass', 'minecraft:blue_stained_glass_pane'],
    ['brown', 'minecraft:brown_stained_glass', 'minecraft:brown_stained_glass_pane'],
    ['green', 'minecraft:green_stained_glass', 'minecraft:green_stained_glass_pane'],
    ['red', 'minecraft:red_stained_glass', 'minecraft:red_stained_glass_pane'],
    ['black', 'minecraft:black_stained_glass', 'minecraft:black_stained_glass_pane']
  ]

  variants.forEach(([name, input, output]) => {
    event.custom({
      type: 'create:pressing',
      ingredients: [{ item: input }],
      results: [{ id: output, count: 4 }]
    }).id(`xices_cobblemon_fix:pressing/${name}_glass_panes`)
  })
})
