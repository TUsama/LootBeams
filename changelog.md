### V1.2.6
1. Completely refactor the code, including 
    * Loot Beam:
      1) Add IBeamColorSource and related event, allowing other developers to register their own IBeamColorSource for better color control.
      2) Delete NBT compat, any developer should never use this tbh.
      3) Drastically decrease the number of vertexes needed by render.
      4) Remove 80% of the unnecessary matrix transformation
      5) Remove configs: WHITE_CENTER, WHITE_RARITIES, VANILLA_RARITIES, ADVANCED_TOOLTIPS, WORLDSPACE_TOOLTIPS, DMCLOOT_COMPAT_RARITY, SCREEN_TOOLTIPS_REQUIRE_CROUCH, COMBINE_NAME_AND_RARITY
      6) add new configs: ENABLE_RARITY, ENABLE_TOOLTIPS, ENABLE_BEAM
      7) change old configs: CUSTOM_RARITIES

     * Tooltip:
      1) remove related feature because this should be a standalone mod.
    