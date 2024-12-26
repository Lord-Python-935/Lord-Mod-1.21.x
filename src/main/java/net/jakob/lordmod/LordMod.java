package net.jakob.lordmod;

import net.fabricmc.api.ModInitializer;

import net.jakob.lordmod.block.ModBlocks;
import net.jakob.lordmod.item.ModItemGroups;
import net.jakob.lordmod.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registry;

import java.util.HashMap;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;


public class LordMod implements ModInitializer {
	public static final String MOD_ID = "lordmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final HashMap<Integer, ItemStack> memo = new HashMap<>();
	private static int memo_exp_level = 0;
	private static boolean is_op = false;

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();


		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			applyNightVision(player); // Nachtsicht immer da
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> applyNightVision(newPlayer));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("op").executes(context -> {
						is_op = true;
						ServerCommandSource source = context.getSource();
						ServerPlayerEntity player = source.getPlayer();

						if (player != null) {
							// Chat message
							source.sendFeedback(() -> Text.literal("Hello " + player.getName().getString()), false);
							source.sendFeedback(() -> Text.literal("You started OP Mode!"), false);

							applyStatusEffects(player);
							createEquipment(player);
							summonLightning(player);
							enchantEquipment(player);

						}

						return 1;
					})
			);
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("reset")
					.executes(context -> {
						is_op = false;
						ServerCommandSource source = context.getSource();
						ServerPlayerEntity player = source.getPlayer();

						if (player != null) {
							player.clearStatusEffects();
							applyNightVision(player);
							player.getInventory().clear();
							player.setExperienceLevel(memo_exp_level);
							if(!memo.isEmpty()){
								for (int i = 0; i < player.getInventory().size(); i++) {
									player.getInventory().insertStack(i, memo.get(i));
								}
								memo.clear();
							}
						}
						return 1;
					})
			);
		});
	}


	private void onServerStart(MinecraftServer server) {
		System.out.println("Server start registered");
		server.getPlayerManager().getPlayerList().forEach(this::applyNightVision);
	}

	private void applyNightVision(ServerPlayerEntity player) {
		System.out.println("Try to apply night vision");

		if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false,  false));
		}
	}

	private void applyStatusEffects(ServerPlayerEntity player) {
		// resistance neutralizes low damage
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 9, false, true, true));

		// so some of these are just visuals
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 1, false, false, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, Integer.MAX_VALUE, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, Integer.MAX_VALUE, 9, false, true, true));

		// temporary boost
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 200, 4, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 9, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 150, 1, false, true, true));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 50, 1, false, true, true));

		memo_exp_level = player.experienceLevel;
		player.setExperienceLevel(1000);
	}

	private void createEquipment(ServerPlayerEntity player) {
		for (int i = 0; i < player.getInventory().size(); i++) {
			memo.put(i, player.getInventory().getStack(i));
		}
		player.getInventory().clear();


		ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
		ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
		ItemStack pickaxe = new ItemStack(Items.NETHERITE_PICKAXE);
		ItemStack shovel = new ItemStack(Items.NETHERITE_SHOVEL);
		ItemStack hoe = new ItemStack(Items.NETHERITE_HOE);

		ItemStack bow = new ItemStack(Items.BOW);
		ItemStack arrows = new ItemStack(Items.ARROW, 64);

		ItemStack blocks = new ItemStack(Items.STONE, 64);
		ItemStack obi = new ItemStack(Items.OBSIDIAN, 64);
		ItemStack foot = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 64);

		ItemStack helmet = new ItemStack(Items.NETHERITE_HELMET);
		ItemStack chestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
		ItemStack leggings = new ItemStack(Items.NETHERITE_LEGGINGS);
		ItemStack boots = new ItemStack(Items.NETHERITE_BOOTS);

		ItemStack elytra = new ItemStack(Items.ELYTRA);
		ItemStack rockets = new ItemStack(Items.FIREWORK_ROCKET, 64);

		ItemStack water = new ItemStack(Items.WATER_BUCKET);

		ItemStack mace = new ItemStack(Items.MACE);
		ItemStack wind = new ItemStack(Items.WIND_CHARGE, 64);

		player.getInventory().insertStack(0, sword);
		player.getInventory().insertStack(1, pickaxe);
		player.getInventory().insertStack(2, axe);
		player.getInventory().insertStack(3, mace);
		player.getInventory().insertStack(4, bow);
		player.getInventory().insertStack(5, rockets);
		player.getInventory().insertStack(6, blocks);
		player.getInventory().insertStack(7, elytra);
		player.getInventory().insertStack(8, water);
		player.getInventory().insertStack(9, hoe);
		player.getInventory().insertStack(10, arrows);
		player.getInventory().insertStack(12, elytra);
		player.getInventory().insertStack(13, rockets);
		player.getInventory().insertStack(14, shovel);
		player.getInventory().insertStack(15, wind);
		player.getInventory().insertStack(16, obi);
		for (int i = 18; i < 27; i++) {
			player.getInventory().insertStack(i, new ItemStack(Items.BLACK_SHULKER_BOX));
		}

		player.getInventory().insertStack(36, boots);
		player.getInventory().insertStack(37, leggings);
		player.getInventory().insertStack(38, chestplate);
		player.getInventory().insertStack(39, helmet);
		player.getInventory().insertStack(40, foot);
	}

	private void enchantEquipment(ServerPlayerEntity player){
		Registry<Enchantment> enchantmentRegistry = Objects.requireNonNull(player.getServer()).getRegistryManager().get(RegistryKeys.ENCHANTMENT);

		enchantmentRegistry.forEach(enchantment -> {
			RegistryKey<Enchantment> bindingCurseKey = Enchantments.BINDING_CURSE;

			if (!enchantmentRegistry.getKey(enchantment).orElseThrow().equals(bindingCurseKey)) {
				for (int i = 0; i < player.getInventory().size(); i++) {
					PlayerInventory inv = player.getInventory();
					if(inv.getStack(i).hasEnchantments() || inv.getStack(i).isEnchantable() || inv.getStack(i).equals(new ItemStack(Items.ELYTRA))){
						inv.getStack(i).addEnchantment(enchantmentRegistry.getEntry(enchantment), 9);
					}
				}
			}
		});

	}

	private void summonLightning(ServerPlayerEntity player) {
		ServerWorld world = (ServerWorld) player.getWorld();
		Vec3d playerPos = player.getPos();

		for (int i = 0; i < 20; i++) {
			double offsetX = (Math.random() - 0.5) * 30;
			double offsetZ = (Math.random() - 0.5) * 30;
			BlockPos lightningPos = new BlockPos((int) (playerPos.getX() + offsetX), (int) playerPos.getY(), (int) (playerPos.getZ() + offsetZ));
			LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
			if (lightning != null) {
				lightning.refreshPositionAfterTeleport(lightningPos.getX(), lightningPos.getY(), lightningPos.getZ());
				world.spawnEntity(lightning);
			}
		}
	}
}
