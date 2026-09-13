ServerEvents.recipes(event => {
  event.custom({
    // Create registers fan washing recipes under the splashing serializer.
    type: 'create:splashing',
    ingredients: [
      { item: 'create:limestone' }
    ],
    results: [
      { chance: 0.10, id: 'create:crushed_raw_zinc' },
      { chance: 0.02, id: 'cobblemon:tumblestone' },
      { chance: 0.02, id: 'cobblemon:black_tumblestone' },
      { chance: 0.02, id: 'cobblemon:sky_tumblestone' }
    ]
  }).id('xices_cobblemon_fix:washing/limestone')
})
