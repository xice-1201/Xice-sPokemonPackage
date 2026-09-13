// Create: Klinks n' Klangs 中文本地化
ClientEvents.lang('zh_cn', event => {
  const ns = 'create_klinks_n_klangs'
  const add = (key, value) => event.add(key, value)

  const fluids = {
    liquid_medicinal_brew: '药酿',
    liquid_potion: '伤药',
    liquid_super_potion: '好伤药',
    liquid_hyper_potion: '厉害伤药',
    liquid_max_potion: '全满药',
    liquid_antidote: '解毒药',
    liquid_burn_heal: '灼伤药',
    liquid_ice_heal: '解冻药',
    liquid_awakening: '解眠药',
    liquid_paralyze_heal: '解麻药',
    liquid_full_heal: '万灵药',
    liquid_full_restore: '全复药',
    liquid_ether: 'PP单项小补剂',
    liquid_max_ether: 'PP单项全补剂',
    liquid_elixir: 'PP多项小补剂',
    liquid_max_elixir: 'PP多项全补剂',
    calcium: '特攻增强剂',
    protein: '攻击增强剂',
    iron: '防御增强剂',
    zinc: '特防增强剂',
    carbos: '速度增强剂',
    hp_up: 'HP增强剂',
    pp_up: 'PP提升剂',
    pp_max: 'PP极限提升剂',
    molten_rare_candy: '神奇糖果',
    black_paint: '宝可梦黑色色剂',
    blue_paint: '宝可梦蓝色色剂',
    green_paint: '宝可梦绿色色剂',
    pink_paint: '宝可梦粉色色剂',
    red_paint: '宝可梦红色色剂',
    white_paint: '宝可梦白色色剂',
    yellow_paint: '宝可梦黄色色剂'
  }

  for (const [id, name] of Object.entries(fluids)) {
    add(`fluid.${ns}.${id}`, name)
    add(`fluid_type.${ns}.${id}`, name)
    add(`block.${ns}.${id}`, name)
  }

  const paintBuckets = {
    black: '宝可梦黑色色剂桶',
    blue: '宝可梦蓝色色剂桶',
    green: '宝可梦绿色色剂桶',
    pink: '宝可梦粉色色剂桶',
    red: '宝可梦红色色剂桶',
    white: '宝可梦白色色剂桶',
    yellow: '宝可梦黄色色剂桶'
  }
  for (const [id, name] of Object.entries(paintBuckets)) {
    add(`item.${ns}.${id}_paint_bucket`, name)
  }

  const balls = {
    park: '公园球',
    luxury: '豪华球',
    ultra: '究极球',
    nest: '巢穴球',
    net: '捕网球',
    moon: '月亮球',
    love: '爱心球',
    lure: '诱饵球',
    level: '等级球',
    heavy: '沉重球',
    friend: '友友球',
    fast: '速度球',
    dusk: '黑暗球',
    dream: '梦境球',
    dive: '潜水球',
    great: '超级球',
    heal: '治愈球',
    quick: '快速球',
    repeat: '重复球',
    safari: '狩猎球',
    sport: '竞赛球',
    timer: '计时球'
  }
  for (const [id, name] of Object.entries(balls)) {
    add(`item.${ns}.${id}_ball_stencil`, `${name}模板`)
    add(`item.${ns}.${id}_ball_stencil.description_0`, `用于制作${name}的模板。`)
    add(`item.${ns}.unfinished_${id}_ball`, `未完成的${name}`)
    add(`item.${ns}.unfinished_${id}_ball.description_0`, '还需要添加颜料才能完成。')
    add(`item.${ns}.unpainted_${id}_ball`, `未上色的${name}`)
  }

  const misc = {
    ball_base: '精灵球底座',
    'ball_base.description_0': '尚未组装完成的精灵球下半部分，还需要安装球盖和按钮。',
    ball_lid: '空白球盖',
    unfinished_ball: '未完成的精灵球',
    'unfinished_ball.description_0': '这是已经安装按钮的球体底座，还需要安装球盖才能完成。',
    blank_ball: '空白精灵球',
    'blank_ball.description_0': '完成模板印刷和上色后，这个精灵球才能发挥作用。',
    wrapper: '包装纸',
    'wrapper.description_0': '用于包装神奇糖果。',
    poke_ball_button: '精灵球按钮',
    'poke_ball_button.description_0': '用于制作',
    'poke_ball_button.description_1': '精灵球。',
    washed_apricorn: '洗净的球果',
    'washed_apricorn.description_0': '已经准备好锯开并用于精灵球制造。',
    liquid_medicinal_brew_bucket: '药酿桶'
  }
  for (const [id, name] of Object.entries(misc)) {
    add(`item.${ns}.${id}`, name)
  }

  add(`item_group.${ns}.klinksn_klangs`, "Klinks n' Klangs")
})
