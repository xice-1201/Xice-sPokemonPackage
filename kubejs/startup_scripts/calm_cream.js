const XiceCalmCreamItem = Java.loadClass('net.minecraft.world.item.Item')
const XiceCalmCreamProperties = Java.loadClass('net.minecraft.world.item.Item$Properties')

StartupEvents.registry('item', event => {
  event.createCustom('calm_cream', () =>
    new XiceCalmCreamItem(new XiceCalmCreamProperties())
  )
})
