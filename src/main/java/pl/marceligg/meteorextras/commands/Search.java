package pl.marceligg.meteorextras.commands;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.BlockESP;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class Search extends Command {

  public Search() {
    super("search", "Search for a block using Block Esp.");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
    builder.then(literal("add").then(
        argument("target", BlockStateArgument.block(REGISTRY_ACCESS)).executes(context -> {
          Block block = context.getArgument("target", BlockInput.class).getState().getBlock();
          @Nullable
          Module module = Modules.get().get(BlockESP.class);
          List<Block> setting = ((Setting<List<Block>>) module.settings.get("blocks")).get();
          if (setting.contains(block)) {
            info("Block already on the list.");
          } else {
            setting.add(block);
            info("Added " + block.getName().getString() + " to search list.");
          }
          module.enable();
          return SINGLE_SUCCESS;
        })));

    builder.then(literal("remove").then(
        argument("target", BlockStateArgument.block(REGISTRY_ACCESS)).executes(context -> {
          Block block = context.getArgument("target", BlockInput.class).getState().getBlock();
          @Nullable
          Module module = Modules.get().get(BlockESP.class);
          List<Block> setting = ((Setting<List<Block>>) module.settings.get("blocks")).get();
          setting.remove(block);
          info("Removed " + block.getName().getString() + " to search list.");
          return SINGLE_SUCCESS;
        })));

    builder.then(literal("only").then(
        argument("target", BlockStateArgument.block(REGISTRY_ACCESS)).executes(context -> {
          Block block = context.getArgument("target", BlockInput.class).getState().getBlock();
          String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
          @Nullable
          Module module = Modules.get().get(BlockESP.class);
          module.settings.get("blocks").parse(blockId);
          module.enable();
          info("Searching for " + block.getName().getString() + ".");
          return SINGLE_SUCCESS;
        })));

    builder.then(literal("on").executes(context -> {
      Modules.get().get(BlockESP.class).enable();
      info("Enabled block search.");
      return SINGLE_SUCCESS;
    }));

    builder.then(literal("off").executes(context -> {
      Modules.get().get(BlockESP.class).disable();
      info("Disabled block search.");
      return SINGLE_SUCCESS;
    }));
  }
}
