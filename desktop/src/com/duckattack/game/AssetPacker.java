package com.duckattack.game;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    private static final boolean DRAW_DEBUG_OUTLINE = false;

    private static final String RAW_ASSETS_PATH = "desktop/assets-raw";
    private static final String ASSETS_PATH = "assets";

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.debug = DRAW_DEBUG_OUTLINE;
        settings.maxHeight = 2048;
        settings.maxWidth = 2048;

        TexturePacker.process(settings,
                RAW_ASSETS_PATH + "/images",   // the directory containing individual images to be packed
                ASSETS_PATH + "/images",   // the directory where the pack file will be written
                "images"   // the name of the pack file / atlas name
        );

//        TexturePacker.process(settings,
//                RAW_ASSETS_PATH + "/skin",
//                ASSETS_PATH + "/ui",
//                "uiskin"
//        );
    }
}
