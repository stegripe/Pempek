package org.stegripe.pempek.task;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.stegripe.pempek.PempekConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TPSBarTask extends BossBarTask {
    private static TPSBarTask instance;
    private double tps = 20.0D;
    private double mspt = 0.0D;
    private int tick = 0;

    public static TPSBarTask instance() {
        if (instance == null) {
            instance = new TPSBarTask();
        }
        return instance;
    }

    @Override
    BossBar createBossBar() {
        return BossBar.bossBar(Component.text(""), 0.0F, instance().getBossBarColor(), PempekConfig.commandTPSBarProgressOverlay);
    }

    @Override
    void updateBossBar(BossBar bossbar, Player player) {
        bossbar.progress(getBossBarProgress());
        bossbar.color(getBossBarColor());
        bossbar.name(MiniMessage.miniMessage().deserialize(PempekConfig.commandTPSBarTitle,
                Placeholder.component("tps", getTPSColor()),
                Placeholder.component("mspt", getMSPTColor()),
                Placeholder.component("ping", getPingColor(player.getPing()))
        ));
    }

    @Override
    public void run() {
        if (++tick < PempekConfig.commandTPSBarTickInterval) {
            return;
        }
        tick = 0;

        this.tps = Math.max(Math.min(Bukkit.getTPS()[0], 20.0D), 0.0D);
        this.mspt = Bukkit.getAverageTickTime();

        super.run();
    }

    private float getBossBarProgress() {
        if (PempekConfig.commandTPSBarProgressFillMode == FillMode.MSPT) {
            return Math.max(Math.min((float) mspt / 50.0F, 1.0F), 0.0F);
        } else {
            return Math.max(Math.min((float) tps / 20.0F, 1.0F), 0.0F);
        }
    }

    private BossBar.Color getBossBarColor() {
        if (isGood(PempekConfig.commandTPSBarProgressFillMode)) {
            return PempekConfig.commandTPSBarProgressColorGood;
        } else if (isMedium(PempekConfig.commandTPSBarProgressFillMode)) {
            return PempekConfig.commandTPSBarProgressColorMedium;
        } else {
            return PempekConfig.commandTPSBarProgressColorLow;
        }
    }

    private boolean isGood(FillMode mode) {
        return isGood(mode, 0);
    }

    private boolean isGood(FillMode mode, int ping) {
        if (mode == FillMode.MSPT) {
            return mspt < 40;
        } else if (mode == FillMode.TPS) {
            return tps >= 19;
        } else if (mode == FillMode.PING) {
            return ping < 100;
        } else {
            return false;
        }
    }

    private boolean isMedium(FillMode mode) {
        return isMedium(mode, 0);
    }

    private boolean isMedium(FillMode mode, int ping) {
        if (mode == FillMode.MSPT) {
            return mspt < 50;
        } else if (mode == FillMode.TPS) {
            return tps >= 15;
        } else if (mode == FillMode.PING) {
            return ping < 200;
        } else {
            return false;
        }
    }

    private Component getTPSColor() {
        String color;
        if (isGood(FillMode.TPS)) {
            color = PempekConfig.commandTPSBarTextColorGood;
        } else if (isMedium(FillMode.TPS)) {
            color = PempekConfig.commandTPSBarTextColorMedium;
        } else {
            color = PempekConfig.commandTPSBarTextColorLow;
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%.2f", tps)));
    }

    private Component getMSPTColor() {
        String color;
        if (isGood(FillMode.MSPT)) {
            color = PempekConfig.commandTPSBarTextColorGood;
        } else if (isMedium(FillMode.MSPT)) {
            color = PempekConfig.commandTPSBarTextColorMedium;
        } else {
            color = PempekConfig.commandTPSBarTextColorLow;
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%.2f", mspt)));
    }

    private Component getPingColor(int ping) {
        String color;
        if (isGood(FillMode.PING, ping)) {
            color = PempekConfig.commandTPSBarTextColorGood;
        } else if (isMedium(FillMode.PING, ping)) {
            color = PempekConfig.commandTPSBarTextColorMedium;
        } else {
            color = PempekConfig.commandTPSBarTextColorLow;
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%s", ping)));
    }

    public enum FillMode {
        TPS, MSPT, PING
    }
}
