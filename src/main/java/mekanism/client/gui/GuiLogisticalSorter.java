package mekanism.client.gui;

import mekanism.api.TileNetworkList;
import mekanism.client.gui.button.ColorButton;
import mekanism.client.gui.button.DisableableImageButton;
import mekanism.client.gui.button.TranslationButton;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.client.sound.SoundHandler;
import mekanism.common.HashList;
import mekanism.common.Mekanism;
import mekanism.common.content.filter.IItemStackFilter;
import mekanism.common.content.filter.IMaterialFilter;
import mekanism.common.content.filter.IModIDFilter;
import mekanism.common.content.filter.IOreDictFilter;
import mekanism.common.content.transporter.TransporterFilter;
import mekanism.common.inventory.container.tile.filter.list.LogisticalSorterContainer;
import mekanism.common.network.PacketGuiButtonPress;
import mekanism.common.network.PacketGuiButtonPress.ClickedTileButton;
import mekanism.common.network.PacketTileEntity;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import mekanism.common.util.text.TextComponentUtil;
import mekanism.common.util.text.Translation;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class GuiLogisticalSorter extends GuiFilterHolder<TransporterFilter, TileEntityLogisticalSorter, LogisticalSorterContainer> {

    public GuiLogisticalSorter(LogisticalSorterContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        // Add common Mekanism gui elements
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource));
        addGuiElement(new GuiSecurityTab<>(this, tileEntity, resource));
    }

    private boolean overUpArrow(double xAxis, double yAxis, int arrowX, int yStart) {
        return xAxis >= arrowX && xAxis <= arrowX + 10 && yAxis >= yStart + 14 && yAxis <= yStart + 20;
    }

    private boolean overDownArrow(double xAxis, double yAxis, int arrowX, int yStart) {
        return xAxis >= arrowX && xAxis <= arrowX + 10 && yAxis >= yStart + 21 && yAxis <= yStart + 27;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        // Get mouse position relative to gui
        double xAxis = mouseX - guiLeft;
        double yAxis = mouseY - guiTop;

        if (button == 0) {
            // Check for scrollbar interaction
            if (xAxis >= 154 && xAxis <= 166 && yAxis >= getScroll() + 18 && yAxis <= getScroll() + 18 + 15) {
                if (needsScrollBars()) {
                    dragOffset = (int) (yAxis - (getScroll() + 18));
                    isDragging = true;
                } else {
                    scroll = 0;
                }
            }

            //Check for filter interaction
            HashList<TransporterFilter> filters = tileEntity.getFilters();
            for (int i = 0; i < 4; i++) {
                int index = getFilterIndex() + i;
                TransporterFilter filter = filters.get(index);
                if (filter != null) {
                    int yStart = i * filterH + filterY;
                    if (xAxis >= filterX && xAxis <= filterX + filterW && yAxis >= yStart && yAxis <= yStart + filterH) {
                        //Check for sorting button
                        int arrowX = filterX + filterW - 12;
                        if (index > 0 && overUpArrow(xAxis, yAxis, arrowX, yStart)) {
                            //Process up button click
                            sendDataFromClick(TileNetworkList.withContents(3, index));
                            return true;
                        }
                        if (index < filters.size() - 1 && overDownArrow(xAxis, yAxis, arrowX, yStart)) {
                            //Process down button click
                            sendDataFromClick(TileNetworkList.withContents(4, index));
                            return true;
                        }
                        if (filter instanceof IItemStackFilter) {
                            Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedTileButton.LS_FILTER_ITEMSTACK, tileEntity.getPos(), index));
                            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                        } else if (filter instanceof IOreDictFilter) {
                            Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedTileButton.LS_FILTER_TAG, tileEntity.getPos(), index));
                            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                        } else if (filter instanceof IMaterialFilter) {
                            Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedTileButton.LS_FILTER_MATERIAL, tileEntity.getPos(), index));
                            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                        } else if (filter instanceof IModIDFilter) {
                            Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedTileButton.LS_FILTER_MOD_ID, tileEntity.getPos(), index));
                            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "logistical_sorter.png");
    }

    @Override
    public void init() {
        super.init();
        // Add buttons to gui
        addButton(new TranslationButton(guiLeft + filterX, guiTop + 136, filterW, 20, "gui.newFilter",
              onPress -> Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedTileButton.LS_SELECT_FILTER_TYPE, tileEntity.getPos()))));
        addButton(new DisableableImageButton(guiLeft + 12, guiTop + 58, 14, 14, 204, 14, -14, getGuiLocation(),
              onPress -> Mekanism.packetHandler.sendToServer(new PacketTileEntity(tileEntity, TileNetworkList.withContents(5))),
              getOnHover("mekanism.gui.logisticalSorter.singleItem.tooltip")));
        addButton(new DisableableImageButton(guiLeft + 12, guiTop + 84, 14, 14, 190, 14, -14, getGuiLocation(),
              onPress -> Mekanism.packetHandler.sendToServer(new PacketTileEntity(tileEntity, TileNetworkList.withContents(2))),
              getOnHover("mekanism.gui.logisticalSorter.roundRobin.tooltip")));
        addButton(new DisableableImageButton(guiLeft + 12, guiTop + 110, 14, 14, 176, 14, -14, getGuiLocation(),
              onPress -> Mekanism.packetHandler.sendToServer(new PacketTileEntity(tileEntity, TileNetworkList.withContents(1))),
              getOnHover("mekanism.gui.logisticalSorter.autoEject.tooltip")));
        addButton(new ColorButton(guiLeft + 13, guiTop + 137, 16, 16, this, () -> tileEntity.color,
              onPress -> Mekanism.packetHandler.sendToServer(new PacketTileEntity(tileEntity, TileNetworkList.withContents(0, InputMappings.isKeyDown(minecraft.mainWindow.getHandle(),
                    GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 0))),
              onRightClick -> Mekanism.packetHandler.sendToServer(new PacketTileEntity(tileEntity, TileNetworkList.withContents(0, 1)))));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Get mouse position relative to gui
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;

        HashList<TransporterFilter> filters = tileEntity.getFilters();
        // Write to info display
        drawString(tileEntity.getName(), 43, 6, 0x404040);
        drawString(TextComponentUtil.build(Translation.of("mekanism.gui.filters"), ":"), 11, 19, 0x00CD00);
        drawString("T: " + filters.size(), 11, 28, 0x00CD00);
        drawString(TextComponentUtil.build(Translation.of("mekanism.gui.logisticalSorter.singleItem"), ":"), 12, 48, 0x00CD00);
        drawString(OnOff.of(tileEntity.singleItem).getTextComponent(), 27, 60, 0x00CD00);
        drawString(TextComponentUtil.build(Translation.of("mekanism.gui.logisticalSorter.roundRobin"), ":"), 12, 74, 0x00CD00);
        drawString(OnOff.of(tileEntity.roundRobin).getTextComponent(), 27, 86, 0x00CD00);
        drawString(TextComponentUtil.build(Translation.of("mekanism.gui.logisticalSorter.autoEject"), ":"), 12, 100, 0x00CD00);
        drawString(OnOff.of(tileEntity.autoEject).getTextComponent(), 27, 112, 0x00CD00);
        drawString(TextComponentUtil.build(Translation.of("mekanism.gui.logisticalSorter.default"), ":"), 12, 126, 0x00CD00);

        //TODO: Convert filters into "proper" buttons/widgets
        // Draw filters
        for (int i = 0; i < 4; i++) {
            TransporterFilter filter = filters.get(getFilterIndex() + i);
            if (filter != null) {
                int yStart = i * filterH + filterY;
                if (filter instanceof IItemStackFilter) {
                    IItemStackFilter itemFilter = (IItemStackFilter) filter;
                    renderItem(itemFilter.getItemStack(), 59, yStart + 3);
                    drawString(TextComponentUtil.translate("gui.itemFilter"), 78, yStart + 2, 0x404040);
                    if (filter.color != null) {
                        drawString(filter.color.getColoredName(), 78, yStart + 11, 0x404040);
                    } else {
                        drawString(TextComponentUtil.translate("mekanism.gui.none"), 78, yStart + 11, 0x404040);
                    }
                } else if (filter instanceof IOreDictFilter) {
                    IOreDictFilter oreFilter = (IOreDictFilter) filter;
                    if (!oreDictStacks.containsKey(oreFilter)) {
                        updateStackList(oreFilter);
                    }
                    renderItem(oreDictStacks.get(filter).renderStack, 59, yStart + 3);
                    drawString(TextComponentUtil.translate("gui.oredictFilter"), 78, yStart + 2, 0x404040);
                    if (filter.color != null) {
                        drawString(filter.color.getColoredName(), 78, yStart + 11, 0x404040);
                    } else {
                        drawString(TextComponentUtil.translate("mekanism.gui.none"), 78, yStart + 11, 0x404040);
                    }
                } else if (filter instanceof IMaterialFilter) {
                    IMaterialFilter itemFilter = (IMaterialFilter) filter;
                    renderItem(itemFilter.getMaterialItem(), 59, yStart + 3);
                    drawString(TextComponentUtil.translate("gui.materialFilter"), 78, yStart + 2, 0x404040);
                    if (filter.color != null) {
                        drawString(filter.color.getColoredName(), 78, yStart + 11, 0x404040);
                    } else {
                        drawString(TextComponentUtil.translate("mekanism.gui.none"), 78, yStart + 11, 0x404040);
                    }
                } else if (filter instanceof IModIDFilter) {
                    IModIDFilter modFilter = (IModIDFilter) filter;
                    if (!modIDStacks.containsKey(modFilter)) {
                        updateStackList(modFilter);
                    }
                    renderItem(modIDStacks.get(filter).renderStack, 59, yStart + 3);
                    drawString(TextComponentUtil.translate("gui.modIDFilter"), 78, yStart + 2, 0x404040);
                    if (filter.color != null) {
                        drawString(filter.color.getColoredName(), 78, yStart + 11, 0x404040);
                    } else {
                        drawString(TextComponentUtil.translate("mekanism.gui.none"), 78, yStart + 11, 0x404040);
                    }
                }

                // Draw hover text for sorting buttons
                int arrowX = filterX + filterW - 12;

                if (getFilterIndex() + i > 0 && overUpArrow(xAxis, yAxis, arrowX, yStart)) {
                    displayTooltip(TextComponentUtil.translate("mekanism.gui.moveUp"), xAxis, yAxis);
                }
                if (getFilterIndex() + i < filters.size() - 1 && overDownArrow(xAxis, yAxis, arrowX, yStart)) {
                    displayTooltip(TextComponentUtil.translate("mekanism.gui.moveDown"), xAxis, yAxis);
                }
            }
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}