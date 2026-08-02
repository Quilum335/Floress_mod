package com.floressmod.reputation;

import com.floressmod.FloressConfig;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * /reputation get [игрок] — посмотреть
 * /reputation set <значение> [игрок] — выставить (-100..100)
 * /reputation add <дельта> [игрок] — прибавить/убавить
 */
public final class ModCommands {
	private ModCommands() {}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("reputation")
						.then(CommandManager.literal("get")
								.executes(context -> sendGet(context.getSource(), context.getSource().getPlayerOrThrow()))
								.then(CommandManager.argument("target", EntityArgumentType.player())
										.executes(context -> sendGet(context.getSource(),
												EntityArgumentType.getPlayer(context, "target")))))
						.then(CommandManager.literal("set")
								.requires(source -> source.hasPermissionLevel(2))
								.then(CommandManager.argument("value", IntegerArgumentType.integer(
												FloressConfig.REP_MIN, FloressConfig.REP_MAX))
										.executes(context -> doSet(context.getSource(),
												context.getSource().getPlayerOrThrow(),
												IntegerArgumentType.getInteger(context, "value")))
										.then(CommandManager.argument("target", EntityArgumentType.player())
												.executes(context -> doSet(context.getSource(),
														EntityArgumentType.getPlayer(context, "target"),
														IntegerArgumentType.getInteger(context, "value"))))))
						.then(CommandManager.literal("add")
								.requires(source -> source.hasPermissionLevel(2))
								.then(CommandManager.argument("value", IntegerArgumentType.integer())
										.executes(context -> doAdd(context.getSource(),
												context.getSource().getPlayerOrThrow(),
												IntegerArgumentType.getInteger(context, "value")))
										.then(CommandManager.argument("target", EntityArgumentType.player())
												.executes(context -> doAdd(context.getSource(),
														EntityArgumentType.getPlayer(context, "target"),
														IntegerArgumentType.getInteger(context, "value"))))))));
	}

	private static int sendGet(ServerCommandSource source, ServerPlayerEntity target) {
		int value = ReputationManager.get(target);
		source.sendFeedback(() -> Text.literal(
				"Репутация " + target.getName().getString() + ": " + value), false);
		return value;
	}

	private static int doSet(ServerCommandSource source, ServerPlayerEntity target, int value) {
		ReputationManager.set(target, value);
		source.sendFeedback(() -> Text.literal(
				"Репутация " + target.getName().getString() + " выставлена на " + value), true);
		return value;
	}

	private static int doAdd(ServerCommandSource source, ServerPlayerEntity target, int delta) {
		ReputationManager.add(target, delta);
		int value = ReputationManager.get(target);
		source.sendFeedback(() -> Text.literal(
				"Репутация " + target.getName().getString() + ": " + value + " (" + (delta >= 0 ? "+" : "") + delta + ")"), true);
		return value;
	}
}
