package com.github.alexthe666.oldworldblues.world.gen;

import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nullable;
import java.util.Random;

public class OWBBlockProcessorLoot extends OWBBlockProcessor {

    private ResourceLocation loot_table;

    public OWBBlockProcessorLoot(BlockPos pos, PlacementSettings settings, ResourceLocation loot) {
        super(pos, settings);
        this.loot_table = loot;
    }

    @Nullable
    public Template.BlockInfo processBlock(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
        if(blockInfoIn.blockState.getBlock() instanceof BlockChest){
            Random rand = new Random(worldIn.getSeed() + pos.toLong());
            NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
            tag.setString("LootTable", loot_table.toString());
            tag.setLong("LootTableSeed", rand.nextLong());
            Template.BlockInfo newInfo = new Template.BlockInfo(pos, Blocks.CHEST.getDefaultState(), tag);
            return newInfo;
        }
        return super.processBlock(worldIn, pos, blockInfoIn);
    }
}