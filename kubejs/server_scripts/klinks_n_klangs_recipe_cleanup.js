// Klinks n' Klangs 配方整理：
// 仅保留“物品/桶瓶 ↔ 对应流体”的分液、注液，以及宝可梦色剂制取。
ServerEvents.recipes(event => {
  const keep = [
    // Cobblemon 药剂、治疗物品、属性增长剂和 PP 物品的桶瓶互转
    'antidote_empty', 'antidote_fill',
    'awakening_empty', 'awakening_fill',
    'burn_heal_empty', 'burn_heal_fill',
    'calcium_empty', 'calcium_fill',
    'carbos_empty', 'carbos_fill',
    'elixir_empty', 'elixir_fill',
    'ether_empty', 'ether_fill',
    'full_heal_empty', 'full_heal_fill',
    'full_restore_empty', 'full_restore_fill',
    'hp_up_empty', 'hp_up_fill',
    'hyper_potion_empty', 'hyper_potion_fill',
    'ice_heal_empty', 'ice_heal_fill',
    'iron_empty', 'iron_fill',
    'max_elixir_empty', 'max_elixer_fill',
    'max_ether_empty', 'max_ether_fill',
    'max_potion_empty', 'max_potion_fill',
    'medicinal_brew_empty', 'medicinal_brew_fill',
    'paralyze_heal_empty', 'paralyze_heal_fill',
    'potion_empty', 'potion_fill',
    'pp_max_empty', 'pp_max_fill',
    'pp_up_empty', 'pp_up_fill',
    'protein_empty', 'protein_fill',
    'super_potion_empty', 'super_potion_fill',
    'zinc_empty', 'zinc_fill',

    // 宝可梦色剂：色剂生产与色剂桶互转
    'black_fill', 'black_paint_empty', 'black_paint_mix',
    'blue_fill', 'blue_paint_empty', 'blue_paint_mix',
    'green_fill', 'green_paint_empty', 'greenpaint_mix',
    'pink_fill', 'pink_paint_empty', 'pink_paint_mix',
    'red_fill', 'red_paint_empty', 'red_paint_mix',
    'white_fill', 'white_paint_empty', 'white_paint_mix',
    'yellow_fill', 'yellow_paint_empty', 'yellow_paint_mix'
  ]

  // 只清理 Klinks n' Klangs 自己的非白名单配方，不影响 Cobblemon
  // 或其它模组，也不会删除下面这些保留的桶瓶互转/色剂配方。
  const keepPattern = keep.join('|')
  event.remove({
    id: new RegExp(`^create_klinks_n_klangs:(?!(?:${keepPattern})$).*$`)
  })
})
