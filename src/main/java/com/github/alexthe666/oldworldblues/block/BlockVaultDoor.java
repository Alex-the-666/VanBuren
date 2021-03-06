package com.github.alexthe666.oldworldblues.block;

import com.github.alexthe666.oldworldblues.OldWorldBlues;
import com.github.alexthe666.oldworldblues.block.entity.TileEntityVaultDoor;
import com.github.alexthe666.oldworldblues.init.OWBBlocks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockVaultDoor extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public Item itemBlock;

    public BlockVaultDoor() {
        super(Material.IRON);
        this.setHardness(100000.0F);
        this.setResistance(100000.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(OldWorldBlues.TAB);
        this.setTranslationKey("oldworldblues.vault_door");
        this.setRegistryName(OldWorldBlues.MODID, "oldworldblues.vault_door");
        GameRegistry.registerTileEntity(TileEntityVaultDoor.class, "vault_door");
        this.setLightOpacity(1);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityVaultDoor();
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public boolean openDoor(World worldIn, BlockPos pos) {
        if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityVaultDoor){
            TileEntityVaultDoor doorEntity = (TileEntityVaultDoor)worldIn.getTileEntity(pos);
            boolean preOpen = doorEntity.open;
            doorEntity.open = !preOpen;
        }
        return true;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    }


    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state){
        for(int y = 0; y < 6; y++){
            BlockPos ySearch = pos.up(y);
            for(int xz = 0; xz < 6; xz++) {
                BlockPos xzSearch = ySearch.offset(state.getValue(FACING).rotateYCCW(), xz);
                if(worldIn.getBlockState(xzSearch).getBlock().isReplaceable(worldIn, pos)){
                    worldIn.setBlockState(xzSearch, OWBBlocks.VAULT_DOOR_FRAME.getDefaultState().withProperty(BlockVaultDoorFrame.FACING, state.getValue(FACING)));
                }
            }
        }
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityVaultDoor) {
            int vaultNumber = stack.getTagCompound() != null ? stack.getTagCompound().getInteger("Number") : 1000;
            ((TileEntityVaultDoor) world.getTileEntity(pos)).number = vaultNumber;
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        for(int y = 0; y < 6; y++){
            BlockPos ySearch = pos.up(y);
            for(int xz = 0; xz < 6; xz++) {
                BlockPos xzSearch = ySearch.offset(state.getValue(FACING).rotateYCCW(), xz);
                if(worldIn.getBlockState(xzSearch).getBlock() instanceof BlockVaultDoorFrame || worldIn.getBlockState(xzSearch).getBlock() instanceof BlockVaultDoor){
                    worldIn.setBlockToAir(xzSearch);
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if(canPlaceHere(placer, world, pos)){
            return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        }else{
            return Blocks.AIR.getDefaultState();
        }
    }
    public boolean canPlaceHere(EntityLivingBase placer, World world, BlockPos pos){
        for(int y = 0; y < 6; y++){
            BlockPos ySearch = pos.up(y);
            for(int xz = 0; xz < 6; xz++) {
                BlockPos xzSearch = ySearch.offset(placer.getHorizontalFacing().rotateYCCW(), -xz);
                if(world.getBlockState(xzSearch).getBlock() != Blocks.AIR){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState blockstate) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState blockstate) {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }
}
