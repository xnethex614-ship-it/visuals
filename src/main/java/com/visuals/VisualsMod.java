package com.visuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class VisualsMod implements ClientModInitializer {
	public static KeyBinding openKey;

	@Override
	public void onInitializeClient() {
		Config.load();
		openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.visuals.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.visuals"));

		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			while (openKey.wasPressed()) {
				if (mc.currentScreen == null) mc.setScreen(new VisualsScreen());
			}
		});

		HudRenderCallback.EVENT.register(CrosshairRenderer::render);
		HitboxRenderer.init();
	}
}
