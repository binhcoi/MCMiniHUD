package com.binhcoi.mcmods.binhcoiminihud;

import java.util.ArrayList;
import java.util.List;

import com.binhcoi.mcmods.binhcoiminihud.mixin.MinecraftClientMixin;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class MiniHud {

    private static final int POS_X = 5;
    private static final int POS_Y = 5;
    private static final int LINE_HEIGHT = 10;
    private static final int TEXT_COLOR = 0x00ffffff;
    private static final int BG_COLOR = 0x90505050;
    private static final int HOUR_OFFSET = 6;
    private static final int HOUR_IN_TICKS = 1000;
    private static final int MINUTE_IN_TICKS = 17;

    public static void render(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity cameraEntity = client.getCameraEntity();
        if (!client.options.debugEnabled && cameraEntity != null) {
            List<String> list = new ArrayList<String>();
            RenderSystem.pushMatrix();
            list.add(String.format("%d fps", ((MinecraftClientMixin) client).getCurrentFps()));

            list.add(String.format("%.0f %.0f %.0f | %s", cameraEntity.getX(), cameraEntity.getY(), cameraEntity.getZ(),
                    cameraEntity.getHorizontalFacing()));
            BlockPos blockPos = cameraEntity.getBlockPos();
            if (client.world != null) {
                if (client.world.isChunkLoaded(blockPos)) {
                    list.add(String.format("Light: %d",
                            client.world.getChunkManager().getLightingProvider().getLight(blockPos, 0)));
                }
                int timeOfDay = (int) (client.world.getTimeOfDay() % 24000);
                list.add(convertTimeFromTick(timeOfDay));
            }
            int posY = POS_Y;
            for (int i = 0; i < list.size(); i++) {
                int width = client.textRenderer.getWidth(list.get(i));
                DrawableHelper.fill(matrices, POS_X - 1, posY - 1, POS_X + width + 1, posY + LINE_HEIGHT, BG_COLOR);

                client.textRenderer.draw(matrices, list.get(i), POS_X, posY, TEXT_COLOR);
                posY += LINE_HEIGHT;
            }
            RenderSystem.popMatrix();
        }
    }

    private static String convertTimeFromTick(int timeinTicks) {
        int hour = timeinTicks / HOUR_IN_TICKS;
        hour += HOUR_OFFSET;
        hour %= 24;
        Boolean pm = false;
        if (hour >= 12) {
            pm = true;
        }
        if (hour > 12) {
            hour -= 12;
        }
        int minute = timeinTicks % HOUR_IN_TICKS;
        minute += (minute / 50);
        minute /= MINUTE_IN_TICKS;
        String time = String.format("%02d:%02d ", hour, minute);
        if (pm)
            time += "PM";
        else
            time += "AM";
        return time;
    }
}