package me.piguy.dimensions.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.piguy.dimensions.dimtools.DimCreator
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object Commands {
  private val dispatcher = CommandRegistrationCallback.EVENT
  private val creator = DimCreator()

  fun register() {
    dispatcher.register { disp, _, _ ->
      disp.register(
        LiteralArgumentBuilder.literal<ServerCommandSource>("bd")
          .requires { source -> source.hasPermissionLevel(2) }
          .then(CommandManager.argument("name", StringArgumentType.string()).executes { c ->
            val name = StringArgumentType.getString(c, "name")
            c.source.sendMessage(Text.literal("creating dimension $name"))
            creator.createNewVoid(c.source.world, name)
            1
          })
      )
    }
  }
}