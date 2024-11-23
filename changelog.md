### V1.2.6
1. Completely refactor the code, including 
    * Loot Beam:
      1) Add IBeamColorSource and related event, allowing other developers to register their own IBeamColorSource for better color control.
      2) Delete NBT compat, any developer should never use this tbh.
      3) Drastically decrease the number of vertexes needed by render.
      4) Remove 80% of the unnecessary matrix transformation
      5) Remove config: WHITE_CENTER, WHITE_RARITIES, VANILLA_RARITIES, VANILLA_RARITIES
      6) add new config: SHOULD_ADD_RARITY
        
    * Tooltip:
      1) remove related feature because this should be a standalone mod.
    