package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.BetterFps;
import guichaguri.betterfps.UpdateChecker;
import guichaguri.betterfps.gui.BetterFpsResourcePack;
import guichaguri.betterfps.patchers.MinecraftPatcher;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import guichaguri.betterfps.transformers.annotations.Patcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import org.lwjgl.LWJGLException;

import java.io.IOException;

/**
 * @author Guilherme Chaguri
 */
@Patcher(MinecraftPatcher.class)
public abstract class MinecraftPatch extends Minecraft {
    public MinecraftPatch(GameConfiguration config) {
        super(config);
    }

    @Copy(Mode.PREPEND)
    @Override
    public void init() {
        BetterFps.CLIENT = true;

        defaultResourcePacks.add(new BetterFpsResourcePack());

        UpdateChecker.check();
    }
}
