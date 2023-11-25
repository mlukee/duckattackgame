package com.duckattack.game.OOPImplementation.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY =
            new AssetDescriptor<>(AssetPaths.IMAGES, TextureAtlas.class);

    public static final AssetDescriptor<Sound> SHOOT = new AssetDescriptor<>(AssetPaths.SHOOT, Sound.class);
    public static final AssetDescriptor<Sound> HIT = new AssetDescriptor<>(AssetPaths.HIT, Sound.class);
    public static final AssetDescriptor<Sound> QUACK = new AssetDescriptor<>(AssetPaths.QUACK, Sound.class);
    public static final AssetDescriptor<Sound> GAME_OVER = new AssetDescriptor<>(AssetPaths.GAME_OVER, Sound.class);
    public static final AssetDescriptor<Sound> EATING = new AssetDescriptor<>(AssetPaths.EATING, Sound.class);
    public static final AssetDescriptor<Sound> COLLECTED = new AssetDescriptor<>(AssetPaths.COLLECTED, Sound.class);

    public static final AssetDescriptor<ParticleEffect> GOLDEN_APPLE_PE = new AssetDescriptor<>(AssetPaths.GOLDEN_APPLE_PE, ParticleEffect.class);
    public static final AssetDescriptor<ParticleEffect> BULLET_PE = new AssetDescriptor<>(AssetPaths.BULLET_PE, ParticleEffect.class);


    private AssetDescriptors() {
    }
}
