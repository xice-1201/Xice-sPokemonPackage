// 六种努力值黏糕的动力搅拌配方。
// 配方材料与 Cobblemon 营火锅配方对应，但蜂蜜改为 250 mB 流体输入。
ServerEvents.recipes(event => {
  const recipes = [
    ['health', 'cobblemon:pomeg_berry', 'cobblemon:health_mochi'],
    ['muscle', 'cobblemon:kelpsy_berry', 'cobblemon:muscle_mochi'],
    ['resist', 'cobblemon:qualot_berry', 'cobblemon:resist_mochi'],
    ['genius', 'cobblemon:hondew_berry', 'cobblemon:genius_mochi'],
    ['clever', 'cobblemon:grepa_berry', 'cobblemon:clever_mochi'],
    ['swift', 'cobblemon:tamato_berry', 'cobblemon:swift_mochi'],
    ['fresh_start', 'cobblemon:enigma_berry', 'cobblemon:fresh_start_mochi']
  ]

  recipes.forEach(([name, berry, output]) => {
    event.custom({
      type: 'create:mixing',
      heat_requirement: 'heated',
      ingredients: [
        { item: 'cobblemon:hearty_grains' },
        { type: 'neoforge:single', amount: 250, fluid: 'create:honey' },
        { item: berry }
      ],
      results: [
        { id: output, count: 3 }
      ]
    }).id(`xices_cobblemon_fix:mixing/${name}_mochi`)
  })
})
