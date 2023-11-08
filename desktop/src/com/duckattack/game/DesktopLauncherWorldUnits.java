package com.duckattack.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.duckattack.game.OOPImplementation.DuckAttackOOP;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncherWorldUnits {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("DuckAttack World Units");
		config.setWindowedMode(800,1000);
		new Lwjgl3Application(new DuckAttackWorldUnits(), config);
	}
}
