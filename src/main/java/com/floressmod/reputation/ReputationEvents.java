package com.floressmod.reputation;

import com.floressmod.FloressConfig;
import com.floressmod.entity.LivingMushroomEntity;
import com.floressmod.registry.ModTags;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

/**
 * Действия игрока, меняющие репутацию дерева.
 * Какие блоки куда относятся — решается тегами (см. ModTags и data/floress_mod/tags).
 */
public final class ReputationEvents {
	private ReputationEvents() {}

	public static void register() {
		registerBlockBreaking();
		registerBoneMealOnLogs();
		registerWaterBottleOnLogs();
		registerLivingMushroomKill();
	}

	/** Слом блоков: сильная потеря / небольшая потеря / рост (мёртвая листва). */
	private static void registerBlockBreaking() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (world.isClient || player.isCreative()) {
				return;
			}
			if (!(player instanceof ServerPlayerEntity serverPlayer)) {
				return;
			}
			if (state.isIn(ModTags.REP_HIGH_LOSS)) {
				ReputationManager.add(serverPlayer, FloressConfig.REP_HIGH_LOSS);
			} else if (state.isIn(ModTags.REP_LOW_LOSS)) {
				ReputationManager.add(serverPlayer, FloressConfig.REP_LOW_LOSS);
			} else if (state.isIn(ModTags.REP_GAIN_BREAK)) {
				ReputationManager.add(serverPlayer, FloressConfig.REP_GAIN_BREAK_DEAD_LEAVES);
			}
		});
	}

	/** ПКМ костной мукой по бревну: мука тратится, репутация растёт. */
	private static void registerBoneMealOnLogs() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			ItemStack stack = player.getStackInHand(hand);
			if (!stack.isOf(Items.BONE_MEAL)) {
				return ActionResult.PASS;
			}
			if (!world.getBlockState(hitResult.getBlockPos()).isIn(BlockTags.LOGS)) {
				return ActionResult.PASS;
			}
			if (!world.isClient) {
				if (!player.isCreative()) {
					stack.decrement(1);
				}
				if (player instanceof ServerPlayerEntity serverPlayer) {
					ReputationManager.add(serverPlayer, FloressConfig.REP_GAIN_BONEMEAL_LOG);
				}
				spawnHappyParticles((ServerWorld) world, hitResult.getBlockPos());
				world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_BONE_MEAL_USE,
						SoundCategory.BLOCKS, 1.0f, 1.0f);
			}
			return ActionResult.SUCCESS;
		});
	}

	/** ПКМ бутылкой воды по бревну: бутылка становится пустой, репутация растёт. */
	private static void registerWaterBottleOnLogs() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			ItemStack stack = player.getStackInHand(hand);
			if (!isWaterBottle(stack)) {
				return ActionResult.PASS;
			}
			if (!world.getBlockState(hitResult.getBlockPos()).isIn(BlockTags.LOGS)) {
				return ActionResult.PASS;
			}
			if (!world.isClient) {
				if (!player.isCreative()) {
					ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
					if (stack.getCount() == 1) {
						player.setStackInHand(hand, emptyBottle);
					} else {
						stack.decrement(1);
						player.getInventory().offerOrDrop(emptyBottle);
					}
				}
				if (player instanceof ServerPlayerEntity serverPlayer) {
					ReputationManager.add(serverPlayer, FloressConfig.REP_GAIN_WATER_LOG);
				}
				spawnHappyParticles((ServerWorld) world, hitResult.getBlockPos());
				world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_BOTTLE_EMPTY,
						SoundCategory.BLOCKS, 1.0f, 1.0f);
			}
			return ActionResult.SUCCESS;
		});
	}

	/** Убийство живого гриба — небольшая потеря репутации. */
	private static void registerLivingMushroomKill() {
		net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(
				(world, entity, killedEntity) -> {
					if (entity instanceof ServerPlayerEntity serverPlayer && killedEntity instanceof LivingMushroomEntity) {
						ReputationManager.add(serverPlayer, FloressConfig.REP_LOSS_LIVING_MUSHROOM_KILL);
					}
				});
	}

	private static boolean isWaterBottle(ItemStack stack) {
		if (!stack.isOf(Items.POTION)) {
			return false;
		}
		PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
		return contents != null && contents.matches(Potions.WATER);
	}

	private static void spawnHappyParticles(ServerWorld world, net.minecraft.util.math.BlockPos pos) {
		world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
				pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
				10, 0.4, 0.4, 0.4, 0.05);
	}
}
