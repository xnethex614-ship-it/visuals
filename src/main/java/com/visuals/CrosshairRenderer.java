package com.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.List;

public class CrosshairRenderer {
	public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
		Config c = Config.get();
		if (!c.crosshair) return;
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.options.hudHidden || !mc.options.getPerspective().isFirstPerson()) return;

		int cx = ctx.getScaledWindowWidth() / 2;
		int cy = ctx.getScaledWindowHeight() / 2;
		int color = (c.dynamic && mc.targetedEntity != null) ? 0xFFFF3030 : c.color();

		int t = Math.max(1, c.thickness);
		int lo = -t / 2, hi = lo + t;
		int s = c.size, g = c.gap;
		List<int[]> rects = new ArrayList<>();

		if (c.style == 0 || c.style == 3) {
			rects.add(new int[]{cx - g - s, cy + lo, cx - g, cy + hi});     // left
			rects.add(new int[]{cx + g, cy + lo, cx + g + s, cy + hi});     // right
			rects.add(new int[]{cx + lo, cy - g - s, cx + hi, cy - g});     // up
			rects.add(new int[]{cx + lo, cy + g, cx + hi, cy + g + s});     // down
		}
		if (c.style == 1 || c.style == 3) {
			int d = Math.max(1, t);
			rects.add(new int[]{cx - d / 2, cy - d / 2, cx - d / 2 + d, cy - d / 2 + d});
		}
		if (c.style == 2) {
			int h = Math.max(2, s);
			rects.add(new int[]{cx - h, cy - h, cx + h, cy - h + t}); // top
			rects.add(new int[]{cx - h, cy + h - t, cx + h, cy + h}); // bottom
			rects.add(new int[]{cx - h, cy - h, cx - h + t, cy + h}); // left
			rects.add(new int[]{cx + h - t, cy - h, cx + h, cy + h}); // right
		}

		if (c.outline) {
			for (int[] r : rects) ctx.fill(r[0] - 1, r[1] - 1, r[2] + 1, r[3] + 1, 0xFF000000);
		}
		for (int[] r : rects) ctx.fill(r[0], r[1], r[2], r[3], color);
	}
}
