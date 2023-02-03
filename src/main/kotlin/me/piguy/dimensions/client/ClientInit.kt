package me.piguy.dimensions.client

import me.piguy.dimensions.DimensionsMod.MY_CHANNEL
import me.piguy.dimensions.DimensionsMod.logger
import me.piguy.dimensions.packets.DimensionListChange
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

class ClientInit : ClientModInitializer {
  val mc = MinecraftClient.getInstance()
  override fun onInitializeClient() {
    MY_CHANNEL.registerClientbound(DimensionListChange::class.java) { message, _ ->
      logger.info("I got a message")
      val world = RegistryKey.of(Registry.WORLD_KEY, message.world)
      val cmdSuggLvl = mc.player!!.networkHandler.worldKeys
      cmdSuggLvl.add(world)
    }
  }
}