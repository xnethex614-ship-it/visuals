package com.visuals;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HitboxRenderer {
	public static void init() {
		WorldRenderEvents.AFTER_ENTITIES.register(HitboxRenderer::render);
	}

	private static void render(WorldRenderContext ctx) {
		Config c = Config.get();
		if (!c.hitboxes) return;
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.world == null || mc.player == null) return;

		MatrixStack matrices = ctx.matrixStack();
		VertexConsumerProvider consumers = ctx.consumers();
		if (matrices == null || consumers == null) return;

		Vec3d cam = ctx.camera().getPos();
		float td = ctx.tickCounter().getTickDelta(false);
		VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());
		double maxSq = (double) c.range * c.range;

		for (Entity e : mc.world.getEntities()) {
			if (e == mc.player && mc.options.getPerspective().isFirstPerson()) continue;
			if (e.squaredDistanceTo(mc.player) > maxSq) continue;

			float[] col;
			if (e instanceof PlayerEntity) { if (!c.players) continue; col = new float[]{1f, 0.25f, 0.25f}; }
			else if (e instanceof HostileEntity) { if (!c.hostile) continue; col = new float[]{1f, 0.6f, 0f}; }
			else if (e instanceof PassiveEntity) { if (!c.passive) continue; col = new float[]{0.3f, 1f, 0.3f}; }
			else { if (!c.other) continue; col = new float[]{0.4f, 0.8f, 1f}; }

			double dx = MathHelper.lerp(td, e.lastRenderX, e.getX()) - e.getX();
			double dy = MathHelper.lerp(td, e.lastRenderY, e.getY()) - e.getY();
			double dz = MathHelper.lerp(td, e.lastRenderZ, e.getZ()) - e.getZ();
			Box box = e.getBoundingBox().offset(dx - cam.x, dy - cam.y, dz - cam.z);

			VertexRendering.drawBox(matrices, lines, box, col[0], col[1], col[2], 1f);

			if (c.eyeLine) {
				double eye = box.minY + e.getStandingEyeHeight();
				VertexRendering.drawBox(matrices, lines, box.minX, eye - 0.01, box.minZ,
						box.maxX, eye + 0.01, box.maxZ, 1f, 0.1f, 0.1f, 1f);
			}
		}
	}
}
