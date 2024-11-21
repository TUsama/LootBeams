package com.lootbeams.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public enum Config {

    ITEMS_GLOW(Boolean.class),
    ALL_ITEMS(Boolean.class),
    ONLY_EQUIPMENT(Boolean.class),
    ONLY_RARE(Boolean.class),

    WHITELIST(new ClassToken(List.class, String.class)),
    BLACKLIST(new ClassToken(List.class, String.class)),
    COLOR_OVERRIDES(new ClassToken(List.class, String.class)),



    COLOR_APPLY_ORDER(new ClassToken(List.class, String.class)),



    ENABLE_BEAM(Boolean.class),
    RENDER_NAME_COLOR(Boolean.class),
    RENDER_RARITY_COLOR(Boolean.class),
    BEAM_RADIUS(Double.class),
    BEAM_HEIGHT(Double.class),
    COMMON_SHORTER_BEAM(Boolean.class),
    BEAM_Y_OFFSET(Double.class),
    BEAM_ALPHA(Double.class),
    BEAM_FADE_DISTANCE(Double.class),

    SOLID_BEAM(Boolean.class),
    RENDER_DISTANCE(Double.class),
    REQUIRE_ON_GROUND(Boolean.class),

    GLOW_EFFECT(Boolean.class),
    GLOW_EFFECT_RADIUS(Double.class),
    ANIMATE_GLOW(Boolean.class),

    PARTICLES(Boolean.class),

    ADVANCED_TOOLTIPS(Boolean.class),
    WORLDSPACE_TOOLTIPS(Boolean.class),
    BORDERS(Boolean.class),
    RENDER_NAMETAGS(Boolean.class),
    RENDER_NAMETAGS_ONLOOK(Boolean.class),
    RENDER_STACKCOUNT(Boolean.class),
    NAMETAG_LOOK_SENSITIVITY(Double.class),
    NAMETAG_TEXT_ALPHA(Double.class),
    NAMETAG_BACKGROUND_ALPHA(Double.class),
    NAMETAG_SCALE(Double.class),
    NAMETAG_Y_OFFSET(Double.class),
    DMCLOOT_COMPAT_RARITY(Boolean.class),
    CUSTOM_RARITIES(new ClassToken(List.class, String.class)),
    WHITE_RARITIES(Boolean.class),
    SCREEN_TOOLTIPS_REQUIRE_CROUCH(Boolean.class),
    COMBINE_NAME_AND_RARITY(Boolean.class),

    GLOWING_BEAM(Boolean.class),

    VANILLA_RARITIES(Boolean.class),
    WHITE_CENTER(Boolean.class),
    PARTICLE_SIZE(Double.class),
    PARTICLE_SPEED(Double.class),
    PARTICLE_RADIUS(Double.class),
    PARTICLE_COUNT(Double.class),
    PARTICLE_LIFETIME(Integer.class),
    RANDOMNESS_INTENSITY(Double.class),
    PARTICLE_RARE_ONLY(Boolean.class),
    PARTICLE_DIRECTION_X(Double.class),
    PARTICLE_DIRECTION_Y(Double.class),
    PARTICLE_DIRECTION_Z(Double.class),
    SPIN_AROUND_BEAM(Boolean.class),
    TRAILS(Boolean.class),
    TRAIL_CHANCE(Double.class),
    TRAIL_PARTICLES_INVISIBLE(Boolean.class),
    TRAIL_WIDTH(Double.class),
    TRAIL_LENGTH(Integer.class),
    TRAIL_FREQUENCY(Integer.class),

    SOUND(Boolean.class),
    SOUND_VOLUME(Double.class),
    SOUND_ONLY_RARE(Boolean.class),
    SOUND_ONLY_EQUIPMENT(Boolean.class),
    SOUND_ONLY_WHITELIST(new ClassToken(List.class, String.class)),
    SOUND_ONLY_BLACKLIST(new ClassToken(List.class, String.class)),
    SOUND_ALL_ITEMS(Boolean.class);

    private final Type type;

    Config(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    private static class ClassToken implements ParameterizedType {
        private final Class<?> raw;
        private final Type[] args;

        ClassToken(Class<?> raw, Type... args) {
            this.raw = raw;
            this.args = args;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(raw.getTypeName());
            if (args.length > 0) {
                sb.append("<");
                for (int i = 0; i < args.length; i++) {
                    sb.append(args[i].getTypeName());
                    if (i < args.length - 1) sb.append(", ");
                }
                sb.append(">");
            }
            return sb.toString();
        }
    }
}
