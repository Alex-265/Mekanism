package mekanism.common.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.RelativeSide;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.gas.BasicGasTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.NucleosynthesizingRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ItemStackGasToItemStackCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.EnergySlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityBasicMachine;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;

public class TileEntityAntiprotonicNucleosynthesizer extends TileEntityBasicMachine<NucleosynthesizingRecipe> {

    public static final int BASE_TICKS_REQUIRED = 400;
    public static final long MAX_GAS = 10_000;

    public BasicGasTank gasTank;

    protected final IOutputHandler<@NonNull ItemStack> outputHandler;
    protected final IInputHandler<@NonNull ItemStack> itemInputHandler;
    protected final ILongInputHandler<@NonNull GasStack> gasInputHandler;

    private MachineEnergyContainer<TileEntityAntiprotonicNucleosynthesizer> energyContainer;
    private InputInventorySlot inputSlot;
    private OutputInventorySlot outputSlot;
    private EnergyInventorySlot energySlot;

    public FloatingLong clientEnergyUsed = FloatingLong.ZERO;

    public TileEntityAntiprotonicNucleosynthesizer() {
        super(MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER, BASE_TICKS_REQUIRED);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY);

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, inputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, outputSlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
            //Set default config directions
            itemConfig.setDataType(RelativeSide.TOP, DataType.INPUT);
            itemConfig.setDataType(RelativeSide.RIGHT, DataType.OUTPUT);
            itemConfig.setDataType(RelativeSide.BACK, DataType.ENERGY);
        }

        ConfigInfo energyConfig = configComponent.getConfig(TransmissionType.ENERGY);
        if (energyConfig != null) {
            energyConfig.addSlotInfo(DataType.INPUT, new EnergySlotInfo(true, false, energyContainer));
            energyConfig.fill(DataType.INPUT);
            energyConfig.setCanEject(false);
        }

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM, itemConfig);

        itemInputHandler = InputHelper.getInputHandler(inputSlot);
        gasInputHandler = InputHelper.getInputHandler(gasTank);
        outputHandler = OutputHelper.getOutputHandler(outputSlot);
    }

    @Nonnull
    @Override
    protected IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks() {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection, this::getConfig);
        builder.addTank(gasTank = BasicGasTank.input(MAX_GAS, gas -> containsRecipe(recipe -> recipe.getGasInput().testType(gas)), this));
        return builder.build();
    }

    @Nonnull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers() {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this));
        return builder.build();
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory() {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addSlot(inputSlot = InputInventorySlot.at(item -> containsRecipe(recipe -> recipe.getItemInput().testType(item)), this, 26, 40));
        builder.addSlot(outputSlot = OutputInventorySlot.at(this, 152, 40));
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getWorld, this, 173, 69));
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        energySlot.fillContainerOrConvert();
        FloatingLong prev = energyContainer.getEnergy().copyAsConst();
        cachedRecipe = getUpdatedCache(0);
        // always update ticks required based on recipe
        ticksRequired = cachedRecipe == null ? BASE_TICKS_REQUIRED : cachedRecipe.getRecipe().getDuration();
        int toProcess = (int) Math.ceil(Math.sqrt(energyContainer.getMaxEnergy().divide(energyContainer.getEnergyPerTick()).doubleValue()));
        for (int i = 0; i < toProcess; i++) {
            if (cachedRecipe != null) {
                cachedRecipe.process();
            }
        }
        clientEnergyUsed = prev.subtract(energyContainer.getEnergy());
    }

    @Nullable
    @Override
    public CachedRecipe<NucleosynthesizingRecipe> getCachedRecipe(int cacheIndex) {
        return cachedRecipe;
    }

    @Nullable
    @Override
    public NucleosynthesizingRecipe getRecipe(int cacheIndex) {
        ItemStack stack = itemInputHandler.getInput();
        if (stack.isEmpty()) {
            return null;
        }
        GasStack gasStack = gasInputHandler.getInput();
        if (gasStack.isEmpty()) {
            return null;
        }
        return findFirstRecipe(recipe -> recipe.test(stack, gasStack));
    }

    @Nullable
    @Override
    public CachedRecipe<NucleosynthesizingRecipe> createNewCachedRecipe(@Nonnull NucleosynthesizingRecipe recipe, int cacheIndex) {
        return new ItemStackGasToItemStackCachedRecipe<>(recipe, itemInputHandler, gasInputHandler, () -> 0, outputHandler)
              .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
              .setActive(this::setActive)
              .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
              .setRequiredTicks(() -> ticksRequired)
              .setOnFinish(() -> markDirty(false))
              .setOperatingTicksChanged(this::setOperatingTicks);
    }

    public MachineEnergyContainer<TileEntityAntiprotonicNucleosynthesizer> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public MekanismRecipeType<NucleosynthesizingRecipe> getRecipeType() {
        return MekanismRecipeType.NUCLEOSYNTHESIZING;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(() -> clientEnergyUsed, value -> clientEnergyUsed = value));
    }
}