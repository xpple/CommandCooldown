package dev.xpple.commandcooldown.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.brigadier.ParseResults;
import dev.xpple.commandcooldown.CommandCooldown;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    @WrapWithCondition(method = "performChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)I"))
    private boolean command_cooldown$getColor(Commands instance, ParseResults<CommandSourceStack> parseResults, String command) {
        if (this.player.hasPermissions(2)) {
            return true;
        }

        UUID uuid = this.player.getUUID();
        String rootCommand = command.split(" ")[0];
        if (!CommandCooldown.checkCooldown(uuid, rootCommand)) {
            parseResults.getContext().getSource().sendFailure(Component.literal("Please wait before executing this command"));
            return false;
        }
        return true;
    }
}
