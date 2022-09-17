package net.rolling.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.rolling.client.animation.AnimatablePlayer;

import java.util.Random;

public record RollEffect(Visuals visuals, String soundId) {
    public record Visuals(String animationName, Particles particles) { }
    public enum Particles {
        PUFF
    }

    private static Random random = new Random();
    public static void playVisuals(Visuals visuals, PlayerEntity player, Vec3d direction) {
        ((AnimatablePlayer)player).playRollAnimation(visuals.animationName(), direction);
        var sound = Registry.SOUND_EVENT.get(new Identifier("rolling:roll"));
        if (sound != null) {
            player.world.playSound(player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 1, 1, true);
        }
        switch (visuals.particles()) {
            case PUFF -> {
                for(int i = 0; i < 15; ++i) {
                    double d = random.nextGaussian() * 0.02;
                    double e = random.nextGaussian() * 0.02;
                    double f = random.nextGaussian() * 0.02;
                    player.world.addParticle(ParticleTypes.POOF,
                            player.getParticleX(1.5),
                            player.getBodyY(random.nextGaussian() * 0.3),
                            player.getParticleZ(1.5), d, e, f);
                }
            }
        }
    }
}
