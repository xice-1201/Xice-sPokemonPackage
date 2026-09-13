// 禁用原版用剪刀从满层蜂巢/蜂箱获取蜜脾。
BlockEvents.rightClicked(event => {
  const blockId = String(event.block.id)
  if (event.item.id !== 'minecraft:shears') return
  if (blockId !== 'minecraft:bee_nest' && blockId !== 'minecraft:beehive') return
  if (Number(event.block.properties.honey_level ?? 0) < 5) return

  event.cancel()
})
