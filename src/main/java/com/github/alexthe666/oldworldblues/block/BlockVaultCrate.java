package com.github.alexthe666.oldworldblues.block;

import com.github.alexthe666.oldworldblues.CommonProxy;
import com.github.alexthe666.oldworldblues.OldWorldBlues;
import com.github.alexthe666.oldworldblues.block.entity.TileEntityOWBStorage;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockVaultCrate extends BlockContainer implements IBlockStackable, IDecorationBlock {

    protected static AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625D, 0D, 0.125D, 0.9375D, 0.75D, 0.875D);
    protected static AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625D, 0D, 0.125D, 0.9375D, 0.75D, 0.875D);
    protected static AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.125D, 0D, 0.0625D, 0.875D, 0.75D, 0.9375D);
    protected static AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.125D, 0D, 0.0625D, 0.875D, 0.75D, 0.9375D);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockVaultCrate(String color) {
        super(Material.ROCK);
        this.setHardness(5F);
        this.setSoundType(SoundType.METAL);
        this.setResistance(Float.MAX_VALUE);
        this.setCreativeTab(OldWorldBlues.TAB);
        this.setTranslationKey("oldworldblues.vault_crate_" + color);
        this.setRegistryName(OldWorldBlues.MODID, "vault_crate_" + color);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        switch ((EnumFacing) state.getValue(FACING)) {
            case NORTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_NORTH.offset(this.getOffset(state, worldIn, pos)));
                break;
            case SOUTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_SOUTH.offset(this.getOffset(state, worldIn, pos)));
                break;
            case WEST:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_EAST.offset(this.getOffset(state, worldIn, pos)));
                break;
            case EAST:
            default:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WEST.offset(this.getOffset(state, worldIn, pos)));
                break;
        }
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB aabb;
        switch ((EnumFacing) state.getValue(FACING)) {
            case NORTH:
                aabb = AABB_NORTH;
                break;
            case SOUTH:
                aabb = AABB_SOUTH;
                break;
            case WEST:
                aabb = AABB_WEST;
                break;
            case EAST:
            default:
                aabb = AABB_EAST;
                break;
        }
        return aabb.offset(this.getOffset(state, source, pos));
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Deprecated
    public Vec3d getOffset(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.down()).getBlock() instanceof IBlockStackable) {
            IBlockState underBlock = worldIn.getBlockState(pos.down());
            AxisAlignedBB underBB = underBlock.getBoundingBox(worldIn, pos.down());
            if(underBB.maxY - 1D > -0.75D){
                return new Vec3d(0, underBB.maxY - 1D, 0);
            }
        }
        return super.getOffset(state, worldIn, pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        } else {
            player.openGui(OldWorldBlues.INSTANCE, CommonProxy.GUI_OWB_STORAGE, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityOWBStorage();
    }
}
