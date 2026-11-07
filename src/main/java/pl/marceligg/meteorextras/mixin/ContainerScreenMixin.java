package pl.marceligg.meteorextras.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.InputConstants;

import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenMixin extends Screen {
  @Shadow
  protected int leftPos;
  @Shadow
  protected int topPos;
  @Shadow
  protected int imageWidth;

  @Unique
  private EditBox me$input;

  protected ContainerScreenMixin(Component title) {
    super(title);
  }

  @Unique
  private void me$send() {
    String typed = me$input.getValue();

    if (typed.isBlank())
      return;

    ChatUtils.sendPlayerMsg(typed);
  }

  @Inject(method = "init", at = @At("TAIL"))
  private void me$addButton(CallbackInfo ci) {
    if (!((Object) this instanceof ContainerScreen))
      return;

    me$input = new EditBox(
        this.font,
        leftPos + imageWidth + 4, topPos, // position (x, y)
        160, 16, // size (w, h)
        Component.literal("Chat"));

    me$input.setMaxLength(256);
    me$input.setHint(Component.literal("Chat"));
    me$input.setValue("/");

    addRenderableWidget(me$input);

    addRenderableWidget(
        Button.builder(Component.literal("Send"), b -> {
          me$send();
        }).bounds(
            leftPos + imageWidth + 4, topPos + 20, // position (x, y)
            34, 16 // size (w, h)
        ).build());
  }

  @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
  private void me$keyPressed(net.minecraft.client.input.KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
    if (me$input != null && me$input.isFocused()) {
      if (event.key() == InputConstants.KEYCODE_RETURN)
        me$send();
      if (event.isEscape())
        return;
      cir.setReturnValue(me$input.keyPressed(event));
    }
  }
}
