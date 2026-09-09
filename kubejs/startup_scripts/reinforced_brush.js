const XiceBrushItem = Java.loadClass('net.minecraft.world.item.BrushItem')
const XiceItemProperties = Java.loadClass('net.minecraft.world.item.Item$Properties')

StartupEvents.registry('item', event => {
  event.createCustom('reinforced_brush', () =>
    new XiceBrushItem(new XiceItemProperties().durability(436))
  )
})
