// 机械手使用扳手加工木构件；扳手作为工具保留，不会被消耗。
ServerEvents.recipes(event => {
  event.custom({
    type: 'create:deploying',
    ingredients: [
      { item: 'createages:wooden_mechanism' },
      { item: 'create:wrench' }
    ],
    keep_held_item: true,
    results: [{ id: 'cobblemon_utility:woodencap' }]
  }).id('xices_cobblemon_fix:deploying/wooden_bracket_to_wooden_cap')
})
