ServerEvents.tags('item', event => {
  event.remove('c:nuggets/copper', 'twilightforest:copper_nugget')
})

ServerEvents.recipes(event => {
  // All recipe production and consumption uses Create's copper nugget.
  event.replaceInput({}, 'twilightforest:copper_nugget', 'create:copper_nugget')
  event.replaceOutput({}, 'twilightforest:copper_nugget', 'create:copper_nugget')

  // Preserve old-world stacks by providing a one-way migration recipe.
  event.shapeless('create:copper_nugget', [
    'twilightforest:copper_nugget'
  ]).id('xices_cobblemon_fix:convert_twilight_copper_nugget')
})

// JEI may already remove Twilight Forest's duplicate copper nugget before
// KubeJS receives its runtime-removal callback.  Calling removeEntriesCompletely
// for an ingredient that is no longer present makes JEI throw
// "ingredients must not be empty" during startup, so the duplicate is unified
// through tags and recipes above without a second runtime JEI removal call.
