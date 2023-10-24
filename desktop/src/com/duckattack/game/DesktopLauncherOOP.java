package com.duckattack.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.duckattack.game.OOPImplementation.DuckAttackOOP;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncherOOP {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("DuckAttack");
		config.setWindowedMode(720,720);
		new Lwjgl3Application(new DuckAttackOOP(), config);
	}
}
