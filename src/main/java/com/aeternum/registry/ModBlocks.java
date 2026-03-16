package com.aeternum.registry;
import com.aeternum.AeternumMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
public class ModBlocks {
    public static final DeferredRegister<?> Blocks =
        DeferredRegister.create(BuiltInRegistries.BLOCK, AeternumMod.MODID);
    @SuppressWarnings("unchecked")
    public static void register() {}
}
