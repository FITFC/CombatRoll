package net.rolling.network;

import com.google.common.collect.Iterables;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.rolling.Rolling;
import net.rolling.api.event.Event;
import net.rolling.api.event.ServerSideRollEvents;

public class ServerNetwork {
    private static PacketByteBuf configSerialized = PacketByteBufs.create();

    public static void initializeHandlers() {
        configSerialized = Packets.ConfigSync.write(Rolling.config);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(Packets.ConfigSync.ID, configSerialized);
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.RollPublish.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null || world.isClient) {
                return;
            }
            final var packet = Packets.RollPublish.read(buf);
            final var velocity = packet.velocity();
            final var forwardBuffer = new Packets.RollAnimation(player.getId(), packet.visuals(), packet.velocity()).write();
            PlayerLookup.tracking(player).forEach(serverPlayer -> {
                try {
                    if (serverPlayer.getId() != player.getId() && ServerPlayNetworking.canSend(serverPlayer, Packets.RollAnimation.ID)) {
                        ServerPlayNetworking.send(serverPlayer, Packets.RollAnimation.ID, forwardBuffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            world.getServer().executeSync(() -> {
                var proxy = (Event.Proxy<ServerSideRollEvents.PlayerStartRolling>)ServerSideRollEvents.PLAYER_START_ROLLING;
                proxy.handlers.forEach(hander -> { hander.onPlayerStartedRolling(player, velocity);});
            });
        });
    }
}
