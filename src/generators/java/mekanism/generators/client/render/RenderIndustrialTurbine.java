package mekanism.generators.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.FluidRenderer;
import mekanism.client.render.FluidRenderer.RenderData;
import mekanism.client.render.MekanismRenderType;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.GeneratorsProfilerConstants;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

@ParametersAreNonnullByDefault
public class RenderIndustrialTurbine extends MekanismTileEntityRenderer<TileEntityTurbineCasing> {

    @Nonnull
    private static FluidStack STEAM = FluidStack.EMPTY;

    public RenderIndustrialTurbine(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityTurbineCasing tile, float partialTick, MatrixStack matrix, IRenderTypeBuffer renderer, int light, int overlayLight, IProfiler profiler) {
        if (tile.clientHasStructure && tile.isRendering && tile.structure != null && tile.structure.complex != null) {
            RenderTurbineRotor.internalRender = true;
            BlockPos pos = tile.getPos();
            BlockPos complexPos = tile.structure.complex.getPos();

            while (true) {
                complexPos = complexPos.down();
                TileEntityTurbineRotor rotor = MekanismUtils.getTileEntity(TileEntityTurbineRotor.class, tile.getWorld(), complexPos);
                if (rotor == null) {
                    break;
                }
                matrix.push();
                matrix.translate(complexPos.getX() - pos.getX(), complexPos.getY() - pos.getY(), complexPos.getZ() - pos.getZ());
                renderDispatcher.renderItem(rotor, matrix, renderer, MekanismRenderer.FULL_LIGHT, overlayLight);
                matrix.pop();
            }

            RenderTurbineRotor.internalRender = false;

            if (!tile.structure.gasTank.isEmpty() && tile.structure.volLength > 0) {
                if (STEAM.isEmpty()) {
                    STEAM = MekanismFluids.STEAM.getFluidStack(1);
                }
                RenderData data = new RenderData();

                data.location = tile.structure.renderLocation;
                data.height = tile.structure.lowerVolume / (tile.structure.volLength * tile.structure.volWidth);
                data.length = tile.structure.volLength;
                data.width = tile.structure.volWidth;
                data.fluidType = STEAM;

                if (data.location != null && data.height >= 1 && !tile.structure.gasTank.isEmpty()) {
                    matrix.push();
                    matrix.translate(data.location.x - pos.getX(), data.location.y - pos.getY(), data.location.z - pos.getZ());
                    //TODO: Maybe make a gas model rather than just using the fluid texture as the textures can technically be different
                    Model3D gasModel = FluidRenderer.getFluidModel(data, 1);
                    MekanismRenderer.renderObject(gasModel, matrix, renderer, MekanismRenderType.renderFluidState(AtlasTexture.LOCATION_BLOCKS_TEXTURE),
                          MekanismRenderer.getColorARGB(tile.structure.gasTank.getStack(), tile.prevSteamScale));
                    matrix.pop();
                }
            }
        }
    }

    @Override
    protected String getProfilerSection() {
        return GeneratorsProfilerConstants.INDUSTRIAL_TURBINE;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityTurbineCasing tile) {
        return tile.clientHasStructure && tile.isRendering && tile.structure != null && tile.structure.complex != null;
    }
}