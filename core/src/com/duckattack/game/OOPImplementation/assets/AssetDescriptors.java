package com.duckattack.game.OOPImplementation.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY =
            new AssetDescriptor<>(AssetPaths.IMAGES, TextureAtlas.class);

    public static final AssetDescriptor<Sound> SHOOT = new AssetDescriptor<Sound>(AssetPaths.SHOOT, Sound.class);

    private AssetDescriptors() {
    }
}
