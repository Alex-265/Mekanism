package mekanism.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class MekanismRenderType extends RenderType {

    //Ignored
    public MekanismRenderType(String name, VertexFormat format, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable runnablePre, Runnable runnablePost) {
        super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost);
    }

    public static RenderType mek_overlay(ResourceLocation resourceLocation) {
        //TODO: Make a "state" piece for the glow?
        RenderType.State state = RenderType.State.func_228694_a_()
              .func_228724_a_(new RenderState.TextureState(resourceLocation, false, false))//Texture state
              .func_228723_a_(field_228520_l_)//shadeModel(GL11.GL_SMOOTH)
              .func_228713_a_(field_228516_h_)//disableAlphaTest
              .func_228726_a_(field_228515_g_)//enableBled/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
              //.func_228719_a_(field_228529_u_)
              .func_228728_a_(true);
        return RenderType.func_228633_a_("mek_overlay", DefaultVertexFormats.field_227849_i_, 7, 256, true, false, state);
    }
}