package com.aeternum.config;
import net.neoforged.neoforge.common.ModConfigSpec;
public class AeternumConfig {
    public static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
}
