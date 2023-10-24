package com.duckattack.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import bouncingball.BouncingBall;
import wheelsrotating.RollingWheelSimulation;


// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncherRollingWheelSimulation {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Bouncing Ball");
        config.setWindowedMode(720,720);
        new Lwjgl3Application(new RollingWheelSimulation(), config);
    }
}
