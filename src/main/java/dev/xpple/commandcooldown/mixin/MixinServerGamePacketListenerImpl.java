package dev.xpple.commandcooldown.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import dev.xpple.commandcooldown.CommandCooldown;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;
import java.util.function.IntSupplier;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    @Shadow @Final private MinecraftServer server;

    @WrapWithCondition(method = "performChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)I"))
    private boolean command_cooldown$getColor(Commands instance, ParseResults<CommandSourceStack> parseResults, String command, @Local(argsOnly = true) ServerboundChatCommandPacket packet) {
        if (this.player.hasPermissions(2)) {
            this.server.getCommands().performCommand(parseResults, packet.command());
            return false;
        }

        UUID uuid = this.player.getUUID();
        String rootCommand = command.split(" ")[0];
        IntSupplier commandOutput = () -> this.server.getCommands().performCommand(parseResults, packet.command());

        if (!CommandCooldown.checkCooldown(uuid, rootCommand, commandOutput)) {
            parseResults.getContext().getSource().sendFailure(Component.literal("Please wait before executing this command"));
        }
        return false;
    }
}
