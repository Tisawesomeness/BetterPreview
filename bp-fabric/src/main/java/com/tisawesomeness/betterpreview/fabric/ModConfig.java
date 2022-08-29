package com.tisawesomeness.betterpreview.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

@Config(name = "betterpreview")
@Environment(EnvType.CLIENT)
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    private static @Nullable ModConfig INSTANCE;
    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("ModConfig not initialized");
        }
        return INSTANCE;
    }

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Comment("Whether to display the chat preview")
    public boolean displayPreviews = true;

}
