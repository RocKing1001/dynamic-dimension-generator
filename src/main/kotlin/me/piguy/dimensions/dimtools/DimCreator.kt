package me.piguy.dimensions.dimtools

import me.piguy.dimensions.DimensionsMod
import me.piguy.dimensions.DimensionsMod.MOD_ID
import net.minecraft.SharedConstants
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

class DimCreator {
  fun createNewVoid(world: ServerWorld, name: String) {
    SharedConstants.isDevelopment = true


    if (world.isClient) {
      return
    }

    val id: RegistryKey<World> = RegistryKey.of(Registry.WORLD_KEY, Identifier(MOD_ID, name))

    if (world.server.getWorld(id) != null) {
      world.server.sendMessage(Text.literal("Dimension already exists"))
      DimensionsMod.logger.warn("Dimension already exists")
      return
    }

    val newWorld = DimManager.getOrCreate(world.server, id)
  }
}