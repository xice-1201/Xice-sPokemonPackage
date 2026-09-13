// 铜与流体工程：蜂蜜与蜜脾的机械动力加工配方
ServerEvents.recipes(event => {
  event.custom({
    // Create 的 pressing 配方不接受流体输入；在工作盆中由动力冲压机
    // 执行“流体压制”时使用 compacting 类型，避免配方加载报错。
    type: 'create:compacting',
    ingredients: [
      { type: 'neoforge:single', amount: 100, fluid: 'create:honey' }
    ],
    results: [
      { id: 'minecraft:honeycomb', count: 1 }
    ]
  }).id('xices_cobblemon_fix:pressing/honeycomb_from_honey')

  event.custom({
    type: 'create:mixing',
    heat_requirement: 'heated',
    ingredients: [
      { item: 'minecraft:honeycomb' }
    ],
    results: [
      { amount: 100, id: 'create:honey' }
    ]
  }).id('xices_cobblemon_fix:mixing/honey_from_honeycomb')
})
