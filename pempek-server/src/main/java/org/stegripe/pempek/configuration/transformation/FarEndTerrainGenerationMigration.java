package org.stegripe.pempek.configuration.transformation;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.stegripe.pempek.PempekConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import static org.spongepowered.configurate.NodePath.path;

public class FarEndTerrainGenerationMigration implements TransformAction {

    public static boolean HAS_BEEN_REGISTERED = false;

    public static final String MISC_KEY = "misc";
    public static final String FIX_FAR_END_TERRAIN_GENERATION_KEY = "fix-far-end-terrain-generation";

    @Override
    public Object @Nullable [] visitPath(final NodePath path, final ConfigurationNode value) throws ConfigurateException {
        String pempekGenerateEndVoidRingsPath = "settings.generate-end-void-rings";
        ConfigurationNode fixFarEndTerrainGenerationNode = value.node(MISC_KEY, FIX_FAR_END_TERRAIN_GENERATION_KEY);
        if (PempekConfig.config.contains(pempekGenerateEndVoidRingsPath)) {
            boolean pempekGenerateEndVoidRings = PempekConfig.config.getBoolean(pempekGenerateEndVoidRingsPath);
            if (pempekGenerateEndVoidRings) {
                fixFarEndTerrainGenerationNode.set(false);
            }
            PempekConfig.config.set(pempekGenerateEndVoidRingsPath, null);
        }

        return null;
    }

    public static void apply(final ConfigurationTransformation.Builder builder) {
        if (PempekConfig.version < 46) {
            HAS_BEEN_REGISTERED = true;
            builder.addAction(path(), new FarEndTerrainGenerationMigration());
        }
    }
}
