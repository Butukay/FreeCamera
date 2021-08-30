package com.butukay.freecam.config;

import com.butukay.freecam.Freecam;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("title.freecam.config"));

            ConfigCategory settings = builder.getOrCreateCategory(new TranslatableText("category.freecam.settings"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            settings.addEntry(entryBuilder.startIntSlider(new TranslatableText("option.freecam.fly-speed"), Freecam.getConfig().getFlySpeed(), 1 , 30)
                    .setDefaultValue(10)
                    .setSaveConsumer(newValue -> Freecam.getConfig().setFlySpeed(newValue))
                    .build());

            settings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("option.freecam.enable-command"), Freecam.getConfig().isEnableCommand())
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> Freecam.getConfig().setEnableCommand(newValue))
                    .build());

            settings.addEntry(entryBuilder.startStrField(new TranslatableText("option.freecam.command-name"), Freecam.getConfig().getCommandName())
                    .setDefaultValue(".freecam")
                    .setSaveConsumer(newValue -> Freecam.getConfig().setCommandName(newValue))
                    .build());

            settings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("option.freecam.action-bar"), Freecam.getConfig().isActionBar())
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> Freecam.getConfig().setActionBar(newValue))
                    .build());

            builder.setSavingRunnable(FreecamConfigUtil::saveConfig);

            return builder.build();
        };
    }
}
