package me.piguy.dimensions

import io.wispforest.owo.network.OwoNetChannel
import io.wispforest.owo.network.serialization.PacketBufSerializer
import me.piguy.dimensions.commands.Commands
import me.piguy.dimensions.packets.DimensionListChange
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.RegistryKey
import org.slf4j.LoggerFactory


object DimensionsMod : ModInitializer {
  val logger = LoggerFactory.getLogger("dimensions")
  const val MOD_ID = "dimensions"
  val MY_CHANNEL = OwoNetChannel.create(Identifier(MOD_ID, "main"))

  override fun onInitialize() {
    MY_CHANNEL.registerClientboundDeferred(DimensionListChange::class.java)

    Commands.register()
  }
}