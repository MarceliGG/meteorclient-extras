package pl.marceligg.meteorextras;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import pl.marceligg.meteorextras.commands.*;
import pl.marceligg.meteorextras.modules.AutoMine;

import org.slf4j.Logger;

public class Addon extends MeteorAddon {
  public static final Logger LOG = LogUtils.getLogger();
  public static final Category CATEGORY = new Category("Meteor Extras");

  @Override
  public void onInitialize() {
    // Modules
    Modules.get().add(new AutoMine());

    // Commands
    Commands.add(new Search());

    // HUD
    // Hud.get().register(HudExample.INFO);
  }

  @Override
  public void onRegisterCategories() {
    Modules.registerCategory(CATEGORY);
  }

  @Override
  public String getPackage() {
    return "pl.marceligg.meteo";
  }

  @Override
  public GithubRepo getRepo() {
    return new GithubRepo("MarceliGG", "meteorclient-extas");
  }
}
