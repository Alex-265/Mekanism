package mekanism.client.sound;

import mekanism.common.Mekanism;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class MekanismSoundProvider extends BaseSoundProvider {

    public MekanismSoundProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {
        super(gen, existingFileHelper, Mekanism.MODID);
    }

    @Override
    protected void addSoundEvents() {
        addTileSoundEvents();
        addItemSoundEvents();
        addHolidaySoundEvents();
        addSoundEvent(SoundEventBuilder.create(MekanismSounds.CJ_EASTER_EGG).addSounds(this::createSoundBuilder,
              Mekanism.rl("cj/cj_1"),
              Mekanism.rl("cj/cj_2"),
              Mekanism.rl("cj/cj_3"),
              Mekanism.rl("cj/cj_4"),
              Mekanism.rl("cj/cj_5"),
              Mekanism.rl("cj/cj_6"),
              Mekanism.rl("cj/cj_7"),
              Mekanism.rl("cj/cj_8")
        ));
    }

    private void addTileSoundEvents() {
        String basePath = "tile/";
        addSoundEvent(MekanismSounds.CHARGEPAD, Mekanism.rl(basePath + "chargepad"));
        addSoundEvent(MekanismSounds.CHEMICAL_CRYSTALLIZER, Mekanism.rl(basePath + "chemical_crystallizer"));
        addSoundEvent(MekanismSounds.CHEMICAL_DISSOLUTION_CHAMBER, Mekanism.rl(basePath + "chemical_dissolution_chamber"));
        addSoundEvent(MekanismSounds.CHEMICAL_INFUSER, Mekanism.rl(basePath + "chemical_infuser"));
        addSoundEvent(MekanismSounds.CHEMICAL_INJECTION_CHAMBER, Mekanism.rl(basePath + "chemical_injection_chamber"));
        addSoundEvent(MekanismSounds.CHEMICAL_OXIDIZER, Mekanism.rl(basePath + "chemical_oxidizer"));
        addSoundEvent(MekanismSounds.CHEMICAL_WASHER, Mekanism.rl(basePath + "chemical_washer"));
        addSoundEvent(MekanismSounds.COMBINER, Mekanism.rl(basePath + "combiner"));
        addSoundEvent(MekanismSounds.OSMIUM_COMPRESSOR, Mekanism.rl(basePath + "compressor"));
        addSoundEvent(MekanismSounds.CRUSHER, Mekanism.rl(basePath + "crusher"));
        addSoundEvent(MekanismSounds.ELECTROLYTIC_SEPARATOR, Mekanism.rl(basePath + "electrolytic_separator"));
        addSoundEvent(MekanismSounds.ENRICHMENT_CHAMBER, Mekanism.rl(basePath + "enrichment_chamber"));
        addSoundEvent(MekanismSounds.LASER, Mekanism.rl(basePath + "laser"));
        addSoundEvent(MekanismSounds.LOGISTICAL_SORTER, Mekanism.rl(basePath + "logistical_sorter"));
        addSoundEvent(MekanismSounds.METALLURGIC_INFUSER, Mekanism.rl(basePath + "metallurgic_infuser"));
        addSoundEvent(MekanismSounds.PRECISION_SAWMILL, Mekanism.rl(basePath + "precision_sawmill"));
        addSoundEvent(MekanismSounds.PRESSURIZED_REACTION_CHAMBER, Mekanism.rl(basePath + "pressurized_reaction_chamber"));
        addSoundEvent(MekanismSounds.PURIFICATION_CHAMBER, Mekanism.rl(basePath + "purification_chamber"));
        addSoundEvent(MekanismSounds.RESISTIVE_HEATER, Mekanism.rl(basePath + "resistive_heater"));
        addSoundEvent(MekanismSounds.ROTARY_CONDENSENTRATOR, Mekanism.rl(basePath + "rotary_condensentrator"));
        addSoundEvent(MekanismSounds.ENERGIZED_SMELTER, Mekanism.rl(basePath + "energized_smelter"));
        addSoundEvent(MekanismSounds.ISOTOPIC_CENTRIFUGE, Mekanism.rl(basePath + "isotopic_centrifuge"));
        addSoundEvent(MekanismSounds.NUTRITIONAL_LIQUIFIER, Mekanism.rl(basePath + "nutritional_liquifier"));
        addSoundEvent(MekanismSounds.INDUSTRIAL_ALARM, Mekanism.rl(basePath + "industrial_alarm"));
        addSoundEvent(MekanismSounds.ANTIPROTONIC_NUCLEOSYNTHESIZER, Mekanism.rl(basePath + "antiprotonic_nucleosynthesizer"));
    }

    private void addItemSoundEvents() {
        String basePath = "item/";
        addSoundEvent(MekanismSounds.HYDRAULIC, Mekanism.rl(basePath + "hydraulic"));
        addSoundEvent(MekanismSounds.FLAMETHROWER_IDLE, Mekanism.rl(basePath + "flamethrower_idle"));
        addSoundEvent(MekanismSounds.FLAMETHROWER_ACTIVE, Mekanism.rl(basePath + "flamethrower_active"));
        addSoundEvent(MekanismSounds.SCUBA_MASK, Mekanism.rl(basePath + "scuba_mask"));
        addSoundEvent(MekanismSounds.JETPACK, Mekanism.rl(basePath + "jetpack"));
        addSoundEvent(MekanismSounds.GRAVITATIONAL_MODULATION_UNIT, Mekanism.rl(basePath + "gravitational_modulation_unit"));

        addSoundEvent(MekanismSounds.GEIGER_SLOW, Mekanism.rl(basePath + "geiger_slow"));
        addSoundEvent(MekanismSounds.GEIGER_MEDIUM, Mekanism.rl(basePath + "geiger_medium"));
        addSoundEvent(MekanismSounds.GEIGER_ELEVATED, Mekanism.rl(basePath + "geiger_elevated"));
        addSoundEvent(MekanismSounds.GEIGER_FAST, Mekanism.rl(basePath + "geiger_fast"));
    }

    private void addHolidaySoundEvents() {
        String basePath = "holiday/";
        addSoundEvent(MekanismSounds.CHRISTMAS1, Mekanism.rl(basePath + "nutcracker1"));
        addSoundEvent(MekanismSounds.CHRISTMAS2, Mekanism.rl(basePath + "nutcracker2"));
        addSoundEvent(MekanismSounds.CHRISTMAS3, Mekanism.rl(basePath + "nutcracker3"));
        addSoundEvent(MekanismSounds.CHRISTMAS4, Mekanism.rl(basePath + "nutcracker4"));
        addSoundEvent(MekanismSounds.CHRISTMAS5, Mekanism.rl(basePath + "nutcracker5"));
    }
}