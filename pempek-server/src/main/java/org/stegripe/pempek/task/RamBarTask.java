package org.stegripe.pempek.task;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.stegripe.pempek.PempekConfig;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class RamBarTask extends BossBarTask {
    private static RamBarTask instance;
    private long allocated = 0L;
    private long used = 0L;
    private long xmx = 0L;
    private long xms = 0L;
    private float percent = 0F;
    private int tick = 0;

    public static RamBarTask instance() {
        if (instance == null) {
            instance = new RamBarTask();
        }
        return instance;
    }

    @Override
    BossBar createBossBar() {
        return BossBar.bossBar(Component.text(""), 0.0F, instance().getBossBarColor(), PempekConfig.commandRamBarProgressOverlay);
    }

    @Override
    void updateBossBar(BossBar bossbar, Player player) {
        bossbar.progress(getBossBarProgress());
        bossbar.color(getBossBarColor());
        bossbar.name(MiniMessage.miniMessage().deserialize(PempekConfig.commandRamBarTitle,
                Placeholder.component("allocated", format(this.allocated)),
                Placeholder.component("used", format(this.used)),
                Placeholder.component("xmx", format(this.xmx)),
                Placeholder.component("xms", format(this.xms)),
                Placeholder.unparsed("percent", ((int) (this.percent * 100)) + "%")
        ));
    }

    @Override
    public void run() {
        if (++this.tick < PempekConfig.commandRamBarTickInterval) {
            return;
        }
        this.tick = 0;

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        this.allocated = heap.getCommitted();
        this.used = heap.getUsed();
        this.xmx = heap.getMax();
        this.xms = heap.getInit();
        this.percent = Math.max(Math.min((float) this.used / this.xmx, 1.0F), 0.0F);

        super.run();
    }

    private float getBossBarProgress() {
        return this.percent;
    }

    private BossBar.Color getBossBarColor() {
        if (this.percent < 0.5F) {
            return PempekConfig.commandRamBarProgressColorGood;
        } else if (this.percent < 0.75F) {
            return PempekConfig.commandRamBarProgressColorMedium;
        } else {
            return PempekConfig.commandRamBarProgressColorLow;
        }
    }

    public Component format(long v) {
        String color;
        if (this.percent < 0.60F) {
            color = PempekConfig.commandRamBarTextColorGood;
        } else if (this.percent < 0.85F) {
            color = PempekConfig.commandRamBarTextColorMedium;
        } else {
            color = PempekConfig.commandRamBarTextColorLow;
        }
        String value;
        if (v < 1024) {
            value = v + "B";
        } else {
            int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
            value = String.format("%.1f%s", (double) v / (1L << (z * 10)), "BKMGTPE".charAt(z));
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.unparsed("text", value));
    }

    public long getAllocated() {
        return this.allocated;
    }

    public long getUsed() {
        return this.used;
    }

    public long getXmx() {
        return this.xmx;
    }

    public long getXms() {
        return this.xms;
    }

    public float getPercent() {
        return this.percent;
    }
}
