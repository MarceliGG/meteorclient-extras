package pl.marceligg.meteorextras.modules;

import java.util.List;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import pl.marceligg.meteorextras.Addon;

public class AutoMine extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();
  private final SettingGroup sgRender = settings.createGroup("Render");

  public enum ListMode {
    Whitelist,
    Blacklist
  }

  private final Setting<ListMode> listMode = sgGeneral.add(new EnumSetting.Builder<ListMode>()
      .name("list-mode")
      .description("Selection mode.")
      .defaultValue(ListMode.Blacklist)
      .build());

  private final Setting<List<Block>> blacklist = sgGeneral.add(new BlockListSetting.Builder()
      .name("blacklist")
      .description("The blocks you don't want to mine.")
      .visible(() -> listMode.get() == ListMode.Blacklist)
      .build());

  private final Setting<List<Block>> whitelist = sgGeneral.add(new BlockListSetting.Builder()
      .name("whitelist")
      .description("The blocks you want to mine.")
      .visible(() -> listMode.get() == ListMode.Whitelist)
      .build());

  private final Setting<Boolean> antiBreak = sgGeneral.add(new BoolSetting.Builder()
      .name("anti-break")
      .description("Stop when tool has low durability.")
      .defaultValue(true)
      .build());

  private final Setting<Boolean> swingHand = sgRender.add(new BoolSetting.Builder()
      .name("swing-hand")
      .description("Swing hand client-side.")
      .defaultValue(true)
      .build());

  public AutoMine() {
    super(Addon.CATEGORY, "automine", "");
  }

  @EventHandler
  private void onTickPre(TickEvent.Pre event) {
    HitResult hit = mc.hitResult;
    if (hit == null || hit.getType() != HitResult.Type.BLOCK)
      return;
    BlockPos hitPos = ((BlockHitResult) hit).getBlockPos();
    Block block = mc.level.getBlockState(hitPos).getBlock();

    if (listMode.get() == ListMode.Whitelist && !whitelist.get().contains(block))
      return;

    if (listMode.get() == ListMode.Blacklist && blacklist.get().contains(block))
      return;

    if (antiBreak.get()) {
      ItemStack item = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
      if (item.getMaxDamage() > 0) {
        int durability = item.getMaxDamage() - item.getDamageValue();
        if (durability <= 1)
          return;
      }
    }

    BlockUtils.breakBlock(hitPos, swingHand.get());
  }
}
