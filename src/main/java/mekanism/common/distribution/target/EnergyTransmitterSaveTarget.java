package mekanism.common.distribution.target;

import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.content.transmitter.EnergyNetwork;
import mekanism.common.distribution.SplitInfo;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.util.Direction;

public class EnergyTransmitterSaveTarget extends Target<TileEntityTransmitter<IStrictEnergyHandler, EnergyNetwork, FloatingLong>, FloatingLong, FloatingLong> {

    private FloatingLong currentStored = FloatingLong.ZERO;

    @Override
    protected void acceptAmount(TileEntityTransmitter<IStrictEnergyHandler, EnergyNetwork, FloatingLong> transmitter, SplitInfo<FloatingLong> splitInfo, FloatingLong amount) {
        amount = amount.min(transmitter.getCapacityAsFloatingLong().subtract(currentStored));
        currentStored = currentStored.plusEqual(amount);
        splitInfo.send(amount);
    }

    @Override
    protected FloatingLong simulate(TileEntityTransmitter<IStrictEnergyHandler, EnergyNetwork, FloatingLong> transmitter, FloatingLong energyToSend) {
        return energyToSend.copy().min(transmitter.getCapacityAsFloatingLong().subtract(currentStored));
    }

    public void saveShare(Direction handlerDirection) {
        TileEntityTransmitter<IStrictEnergyHandler, EnergyNetwork, FloatingLong> transmitter = handlers.get(handlerDirection);
        if (transmitter instanceof TileEntityUniversalCable) {
            TileEntityUniversalCable cable = (TileEntityUniversalCable) transmitter;
            if (!currentStored.isZero() || !cable.lastWrite.isZero()) {
                cable.lastWrite = currentStored;
                cable.markDirty(false);
            }
        }
    }
}