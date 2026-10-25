package com.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class VisualsScreen extends Screen {
	private static final String[] STYLES = {"Cross", "Dot", "Square", "Cross + Dot"};
	private static final int W = 150, STEP = 22;

	public VisualsScreen() {
		super(Text.literal("Visuals"));
	}

	@Override
	protected void init() {
		Config c = Config.get();
		int lx = width / 2 - W - 5, rx = width / 2 + 5, y;

		// ---- Hitboxes (left) ----
		y = 42;
		addDrawableChild(toggle(lx, y, "Hitboxes", () -> c.hitboxes, v -> c.hitboxes = v)); y += STEP;
		addDrawableChild(toggle(lx, y, "Eye line", () -> c.eyeLine, v -> c.eyeLine = v)); y += STEP;
		addDrawableChild(new Slider(lx, y, W, "Range", 8, 128, c.range, v -> c.range = v)); y += STEP;
		addDrawableChild(toggle(lx, y, "Players", () -> c.players, v -> c.players = v)); y += STEP;
		addDrawableChild(toggle(lx, y, "Hostile", () -> c.hostile, v -> c.hostile = v)); y += STEP;
		addDrawableChild(toggle(lx, y, "Passive", () -> c.passive, v -> c.passive = v)); y += STEP;
		addDrawableChild(toggle(lx, y, "Other", () -> c.other, v -> c.other = v));

		// ---- Crosshair (right) ----
		y = 42;
		addDrawableChild(toggle(rx, y, "Crosshair", () -> c.crosshair, v -> c.crosshair = v)); y += STEP;
		addDrawableChild(ButtonWidget.builder(Text.literal("Style: " + STYLES[c.style]), b -> {
			c.style = (c.style + 1) % STYLES.length;
			b.setMessage(Text.literal("Style: " + STYLES[c.style]));
		}).dimensions(rx, y, W, 20).build()); y += STEP;
		addDrawableChild(toggle(rx, y, "Red on target", () -> c.dynamic, v -> c.dynamic = v)); y += STEP;
		addDrawableChild(toggle(rx, y, "Outline", () -> c.outline, v -> c.outline = v)); y += STEP;
		addDrawableChild(new Slider(rx, y, W, "Size", 1, 20, c.size, v -> c.size = v)); y += STEP;
		addDrawableChild(new Slider(rx, y, W, "Gap", 0, 15, c.gap, v -> c.gap = v)); y += STEP;
		addDrawableChild(new Slider(rx, y, W, "Thickness", 1, 6, c.thickness, v -> c.thickness = v)); y += STEP;
		addDrawableChild(new Slider(rx, y, W, "Red", 0, 255, c.r, v -> c.r = v)); y += STEP;
		addDrawableChild(new Slider(rx, y, W, "Green", 0, 255, c.g, v -> c.g = v)); y += STEP;
		addDrawableChild(new Slider(rx, y, W, "Blue", 0, 255, c.b, v -> c.b = v));

		addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
				.dimensions(width / 2 - 100, height - 28, 200, 20).build());
	}

	private ButtonWidget toggle(int x, int y, String label, BooleanSupplier get, Consumer<Boolean> set) {
		Supplier<Text> txt = () -> Text.literal(label + ": " + (get.getAsBoolean() ? "ON" : "OFF"));
		return ButtonWidget.builder(txt.get(), b -> {
			set.accept(!get.getAsBoolean());
			b.setMessage(txt.get());
		}).dimensions(x, y, W, 20).build();
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		ctx.drawCenteredTextWithShadow(textRenderer, "Visuals", width / 2, 12, 0xFFFFFF);
		ctx.drawCenteredTextWithShadow(textRenderer, "Hitboxes", width / 2 - W / 2 - 5, 28, 0xFFAAAAAA);
		ctx.drawCenteredTextWithShadow(textRenderer, "Crosshair", width / 2 + W / 2 + 5, 28, 0xFFAAAAAA);
	}

	@Override
	public void removed() {
		Config.save();
	}

	private static class Slider extends SliderWidget {
		private final String label;
		private final double min, max;
		private final IntConsumer setter;

		Slider(int x, int y, int w, String label, double min, double max, double cur, IntConsumer setter) {
			super(x, y, w, 20, Text.empty(), (cur - min) / (max - min));
			this.label = label;
			this.min = min;
			this.max = max;
			this.setter = setter;
			updateMessage();
		}

		private int val() {
			return (int) Math.round(min + value * (max - min));
		}

		@Override
		protected void updateMessage() {
			setMessage(Text.literal(label + ": " + val()));
		}

		@Override
		protected void applyValue() {
			setter.accept(val());
		}
	}
}
