const xice$removeTwilightCopperNugget = event => {
  event.remove('twilightforest:copper_nugget')
}

StartupEvents.modifyCreativeTab('twilightforest:items', xice$removeTwilightCopperNugget)
StartupEvents.modifyCreativeTab('minecraft:ingredients', xice$removeTwilightCopperNugget)
