package me.guichaguri.betterfps.fml;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.util.Arrays;
import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsContainer extends DummyModContainer {

    private static ModMetadata createMetadata() {
        ModMetadata meta = new ModMetadata();
        meta.modId = BetterFpsHelper.MODID;
        meta.name = "BetterFps";
        meta.version = BetterFpsHelper.VERSION;
        meta.authorList = Arrays.asList("Guichaguri");
        meta.description = "Performance Improvements";
        meta.url = "http://minecraft.curseforge.com/mc-mods/229876-betterfps";
        return meta;
    }

    public static Configuration CONFIG;
    public static Property CONFIG_ALGORITHM;

    public BetterFpsContainer() {
        super(createMetadata());
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);

        BetterFpsHelper.init();

        // IMPORTING OLD CONFIG - WILL BE REMOVED IN THE FUTURE
        File oldConfig = new File("config", "betterfps.cfg");
        boolean b = oldConfig.exists();

        CONFIG = b ? new Configuration(oldConfig) : new Configuration();
        CONFIG_ALGORITHM = CONFIG.get("betterfps", "algorithm", "rivens-full");
        if(b) {
            BetterFpsHelper.ALGORITHM_NAME = CONFIG_ALGORITHM.getString();
            BetterFpsHelper.saveConfig();
            oldConfig.deleteOnExit();
        }
        CONFIG_ALGORITHM.set(BetterFpsHelper.ALGORITHM_NAME);
        CONFIG_ALGORITHM.setRequiresMcRestart(true);
        String v = "";
        for(String s : BetterFpsHelper.helpers.keySet()) v += ", " + s;
        CONFIG_ALGORITHM.comment = "The algorithm to be used.\nValues: " + v.substring(2);
        String[] validValues = new String[BetterFpsHelper.displayHelpers.size()];
        int i = 0;
        for(String s : BetterFpsHelper.displayHelpers.values()) {
            validValues[i] = s; i++;
        }
        CONFIG_ALGORITHM.setValidValues(validValues);
    }

    @SubscribeEvent
    public void OnConfigChangedEvent(OnConfigChangedEvent event) {
        if(event.modID.equals(BetterFpsHelper.MODID)) {
            BetterFpsHelper.CONFIG.setProperty("algorithm", CONFIG_ALGORITHM.getString());
            BetterFpsHelper.saveConfig();
        }
    }

    @Override
    public String getGuiClassName() {
        return "me.guichaguri.betterfps.client.ConfigFactory";
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        try {
            return VersionRange.createFromVersionSpec("*");
        } catch(Exception ex) {
            return super.acceptableMinecraftVersionRange();
        }
    }

}
