package com.visuals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
	// hitboxes
	public boolean hitboxes = true, eyeLine = true;
	public boolean players = true, hostile = true, passive = true, other = true;
	public int range = 64;
	// crosshair
	public boolean crosshair = true, dynamic = true, outline = true;
	public int style = 0; // 0 cross, 1 dot, 2 square, 3 cross+dot
	public int size = 6, gap = 3, thickness = 2;
	public int r = 0, g = 255, b = 140;

	private static Config inst;
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("visuals.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static Config get() {
		if (inst == null) load();
		return inst;
	}

	public static void load() {
		try {
			if (Files.exists(PATH)) inst = GSON.fromJson(Files.readString(PATH), Config.class);
		} catch (Exception ignored) {}
		if (inst == null) inst = new Config();
	}

	public static void save() {
		try {
			Files.writeString(PATH, GSON.toJson(get()));
		} catch (IOException ignored) {}
	}

	public int color() {
		return 0xFF000000 | (r << 16) | (g << 8) | b;
	}
}
