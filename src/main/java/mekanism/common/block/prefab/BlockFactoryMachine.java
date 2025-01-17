package mekanism.common.block.prefab;

import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.content.blocktype.Factory;
import mekanism.common.content.blocktype.Machine.FactoryMachine;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;

public class BlockFactoryMachine<TILE extends TileEntityMekanism, MACHINE extends FactoryMachine<TILE>> extends BlockTile<TILE, MACHINE> {

    public BlockFactoryMachine(MACHINE machineType) {
        super(machineType);
    }

    public static class BlockFactoryMachineModel<TILE extends TileEntityMekanism, MACHINE extends FactoryMachine<TILE>> extends BlockFactoryMachine<TILE, MACHINE> implements IStateFluidLoggable {

        public BlockFactoryMachineModel(MACHINE machineType) {
            super(machineType);
        }
    }

    public static class BlockFactory<TILE extends TileEntityFactory<?>> extends BlockFactoryMachineModel<TILE, Factory<TILE>> {

        public BlockFactory(Factory<TILE> factoryType) {
            super(factoryType);
        }
    }
}
