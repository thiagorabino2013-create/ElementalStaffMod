
package dev.elementalstaff;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ElementalStaffItem extends Item {

    private static final int COOLDOWN_TICKS = 100;

    public ElementalStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world instanceof ServerWorld serverWorld) {
            Vec3d direction = user.getRotationVec(1.0F);
            Vec3d origin = user.getEntityPos().add(0, 1, 0)
                    .add(direction.multiply(3));

            if (user.isSneaking()) {
                createTsunami(serverWorld, origin, direction);
            } else {
                createMeteor(serverWorld, origin);
            }

            user.getItemCooldownManager().set(stack, COOLDOWN_TICKS);
        }

        return ActionResult.SUCCESS;
    }

    private void createMeteor(ServerWorld world, Vec3d origin) {
        for (int i = 0; i < 12; i++) {
            double y = origin.y + i * 0.8;

            world.spawnParticles(
                    ParticleTypes.FLAME,
                    origin.x, y, origin.z,
                    15, 0.8, 0.5, 0.8, 0.08
            );

            world.spawnParticles(
                    ParticleTypes.SMOKE,
                    origin.x, y, origin.z,
                    8, 0.5, 0.5, 0.5, 0.03
            );
        }
    }

    private void createTsunami(ServerWorld world, Vec3d origin, Vec3d direction) {
        Vec3d side = new Vec3d(-direction.z, 0, direction.x);

        for (int distance = 1; distance <= 12; distance++) {
            Vec3d center = origin.add(direction.multiply(distance));

            for (int width = -5; width <= 5; width++) {
                Vec3d position = center.add(side.multiply(width));

                world.spawnParticles(
                        ParticleTypes.SPLASH,
                        position.x, position.y, position.z,
                        12, 0.3, 1.5, 0.3, 0.15
                );

                world.spawnParticles(
                        ParticleTypes.BUBBLE,
                        position.x, position.y, position.z,
                        5, 0.3, 0.8, 0.3, 0.05
                );
            }
        }
    }
}
