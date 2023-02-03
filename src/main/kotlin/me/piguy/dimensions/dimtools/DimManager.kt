package me.piguy.dimensions.dimtools

import com.google.common.collect.ImmutableList
import com.mojang.serialization.Lifecycle
import me.piguy.dimensions.DimensionsMod.MOD_ID
import me.piguy.dimensions.DimensionsMod.MY_CHANNEL
import me.piguy.dimensions.DimensionsMod.logger
import me.piguy.dimensions.packets.DimensionListChange
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.WorldGenerationProgressListener
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.registry.MutableRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.SaveProperties
import net.minecraft.world.World
import net.minecraft.world.border.WorldBorderListener
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.gen.GeneratorOptions
import net.minecraft.world.level.UnmodifiableLevelProperties
import net.minecraft.world.level.storage.LevelStorage
import java.util.concurrent.Executor

object DimManager {

  fun getOrCreate(
    server: MinecraftServer, worldKey: RegistryKey<World>
  ): ServerWorld {
    val worldKeys = server.worldRegistryKeys
    val map = server.worlds

    if (worldKeys.contains(worldKey)) {
      return server.getWorld(worldKey)!!
    }

    return createDim(server, map, worldKey)
  }


  fun getDimensionOptions(world: ServerWorld): DimensionOptions {
    val type: RegistryEntry<DimensionType> =
      world.server.registryManager.get(Registry.DIMENSION_TYPE_KEY)
        .entryOf(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, Identifier(MOD_ID, "arena")))

    return DimensionOptions(type, world.chunkManager.chunkGenerator)
  }


  private fun createDim(
    server: MinecraftServer,
    map: MutableMap<RegistryKey<World>, ServerWorld>,
    worldKey: RegistryKey<World>,
  ): ServerWorld {
    val overworld = server.overworld

    val newDimIdentifier = worldKey.value

    val dimensionKey = RegistryKey.of(Registry.DIMENSION_KEY, newDimIdentifier)


    val dimension = getDimensionOptions(overworld)


    val chunkProgressListener: WorldGenerationProgressListener =
      server.worldGenerationProgressListenerFactory.create(11)
    val executor: Executor = server.workerExecutor
    val anvilConverter: LevelStorage.Session = server.session
    val worldData: SaveProperties = server.saveProperties
    val worldGenSettings: GeneratorOptions = worldData.generatorOptions
    val derivedLevelData =
      UnmodifiableLevelProperties(worldData, worldData.mainWorldProperties)

    val dimensionRegistry: Registry<DimensionOptions> = worldGenSettings.dimensions


    if (dimensionRegistry is MutableRegistry<DimensionOptions>) {
      dimensionRegistry.add(dimensionKey, dimension, Lifecycle.stable())
    } else {
      throw IllegalStateException("Unable to register dimension! Registry not writable!")
    }

    val newWorld = ServerWorld(
      server,
      executor,
      anvilConverter,
      derivedLevelData,
      worldKey,
      dimension,
      chunkProgressListener,
      worldGenSettings.isDebugWorld,
      worldGenSettings.seed,
      ImmutableList.of(),
      false
    )

    map[worldKey] = newWorld

    overworld.worldBorder.addListener(WorldBorderListener.WorldBorderSyncer(newWorld.worldBorder))

    ServerWorldEvents.LOAD.invoker().onWorldLoad(server, newWorld)


    for (player in server.playerManager.playerList) {
      logger.info("sending packet to ${player.name.string}")
      MY_CHANNEL.serverHandle(player).send(DimensionListChange(newDimIdentifier))
    }

    return newWorld
  }

}