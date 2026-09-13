// 使用尸化精华将个体值提升糖果转化为对应的个体值下降糖果。
ServerEvents.recipes(event => {
  const conversions = [
    ['health', 'sickly'],
    ['mighty', 'weak'],
    ['tough', 'brittle'],
    ['smart', 'numb'],
    ['courage', 'coward'],
    ['quick', 'slow']
  ]

  conversions.forEach(([up, down]) => {
    event.custom({
      type: 'create:mixing',
      ingredients: [
        { item: `cobblemon:${up}_candy`, count: 3 },
        { item: 'twilightforest:exanimate_essence' }
      ],
      results: [{ id: `cobblemon:${down}_candy`, count: 3 }]
    }).id(`xices_cobblemon_fix:mixing/${up}_candy_to_${down}_candy`)
  })
})
