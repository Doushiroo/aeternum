package com.aeternum.registry;
import com.aeternum.AeternumMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
public class ModParticles {
    public static final DeferredRegister<?> Particles =
        DeferredRegister.create(BuiltInRegistries.BLOCK, AeternumMod.MODID);
    @SuppressWarnings("unchecked")
    public static void register() {}
}
