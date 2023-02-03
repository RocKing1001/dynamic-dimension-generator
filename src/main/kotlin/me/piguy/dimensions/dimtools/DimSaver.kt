package me.piguy.dimensions.dimtools

import net.minecraft.SharedConstants
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.Identifier
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionOptions
import java.util.function.Function
import java.util.function.Supplier

class DimSaver : PersistentState() {
  fun save() {
    setDirty()
  }

  fun setDirty() {
    this.isDirty = true
  }

  fun getData(
    world: World,
    loader: Function<NbtCompound, PersistentState>,
    supplier: Supplier<PersistentState>,
    name: String
  ) {
    if (world.isClient) return
    if (world.server?.overworld?.persistentStateManager == null) return

    val storage = world.server!!.overworld.persistentStateManager

    storage.getOrCreate(loader, supplier, name)
  }

  override fun writeNbt(nbt: NbtCompound): NbtCompound {
    return nbt
  }
}