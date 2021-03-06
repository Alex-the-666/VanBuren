package com.github.alexthe666.oldworldblues.structure;
import com.github.alexthe666.oldworldblues.world.gen.WorldGenVaultTecVan;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;

public class VillageComponentVan extends StructureVillagePieces.Village {
	private int averageGroundLevel = -1;

	public VillageComponentVan() {
		super();
	}

	public VillageComponentVan(StructureVillagePieces.Start startPiece, int p2, Random random, StructureBoundingBox structureBox, EnumFacing facing) {
		super();
		this.setCoordBaseMode(facing);
		this.boundingBox = structureBox;
	}

	public static VillageComponentVan buildComponent(StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int p5) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 8, 7, 8, facing);
		return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new VillageComponentVan(startPiece, p5, random, structureboundingbox, facing) : null;
	}
	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);
			if (this.averageGroundLevel < 0) {
				return false;
			}
			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
		}
		BlockPos blockpos = new BlockPos(this.getXWithOffset(2, 4), this.getYWithOffset(0), this.getZWithOffset(2, 4));
		return new WorldGenVaultTecVan(this.getCoordBaseMode()).generate(world, random, blockpos.up());
	}

}
