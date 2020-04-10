package com.codetaylor.mc.artisanautomation.modules.automator.gui;

import com.codetaylor.mc.artisanautomation.modules.automator.ModuleAutomator;
import com.codetaylor.mc.artisanautomation.modules.automator.TooltipUtil;
import com.codetaylor.mc.artisanautomation.modules.automator.gui.element.*;
import com.codetaylor.mc.artisanautomation.modules.automator.gui.slot.InventorySlot;
import com.codetaylor.mc.artisanautomation.modules.automator.gui.slot.TableSlot;
import com.codetaylor.mc.artisanautomation.modules.automator.tile.TileAutomator;
import com.codetaylor.mc.athenaeum.gui.GuiContainerBase;
import com.codetaylor.mc.athenaeum.gui.GuiHelper;
import com.codetaylor.mc.athenaeum.gui.Texture;
import com.codetaylor.mc.athenaeum.gui.element.GuiElementTextureRectangle;
import com.codetaylor.mc.athenaeum.gui.element.GuiElementTitle;
import com.codetaylor.mc.athenaeum.packer.PackAPI;
import com.codetaylor.mc.athenaeum.packer.PackedData;
import com.codetaylor.mc.athenaeum.util.TooltipHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutomatorGuiContainer
    extends GuiContainerBase {

  public static final int TEXT_OUTLINE_COLOR = new Color(133, 90, 49).getRGB();

  private final TileAutomator tile;
  private final AutomatorContainer container;

  public AutomatorGuiContainer(TileAutomator tile, AutomatorContainer container, int width, int height) {

    super(container, width, height);
    this.tile = tile;
    this.container = container;

    this.createBackgroundElement();
    this.createContainerTextElements();
    this.createDeselectedTabElements();
    this.createPanelElement();
    this.createPowerPanelElements();
    this.createPatternPanelElements();
    this.createInventoryPanelElements();
    this.createFluidPanelElements();
    this.createToolPanelElements();
    this.createSelectedTabElements();
  }

  private void createToolPanelElements() {

    for (int i = 0; i < 6; i++) {
      this.guiContainerElementAdd(new GuiElementToolCap(
          this::getContainerState,
          AutomatorContainer.EnumState.Tool,
          () -> !this.tile.getToolboxStackHandler().getStackInSlot(0).isEmpty(),
          this,
          new Texture[]{this.getTexture("tool-upgrade-cap")},
          8 + i * 18,
          56
      ));
    }
  }

  private void createFluidPanelElements() {

    for (int i = 0; i < 3; i++) {
      int fluidIndex = i;
      this.guiContainerElementAdd(new GuiElementFluidTank(
          this.tile.getPos(),
          fluidIndex,
          this,
          this.tile.getFluidHandler(i),
          64, 39 + 18 * i,
          102, 14
      ));
      this.guiContainerElementAdd(new GuiElementButtonFluidLockMode(
          this.tile.getPos(),
          fluidIndex,
          () -> this.tile.isFluidLocked(fluidIndex),
          this::getContainerState,
          this,
          new Texture[]{
              // Expects regular textures first, then hovered textures
              this.getTexture("inventory-button-lock-unlocked"),
              this.getTexture("inventory-button-lock-locked"),
              this.getTexture("inventory-button-lock-unlocked-hovered"),
              this.getTexture("inventory-button-lock-locked-hovered")
          },
          44, 38 + i * 18
      ));
      this.guiContainerElementAdd(new GuiElementButtonFluidMode(
          this.tile.getPos(),
          fluidIndex,
          () -> this.tile.getFluidMode(fluidIndex),
          this::getContainerState,
          this,
          new Texture[]{
              // Expects regular textures first, then hovered textures
              this.getTexture("fluid-button-mode-fill"),
              this.getTexture("fluid-button-mode-drain"),
              this.getTexture("fluid-button-mode-fill-hovered"),
              this.getTexture("fluid-button-mode-drain-hovered")
          },
          26, 38 + i * 18
      ));
    }

    this.guiContainerElementAdd(new GuiElementTextureRectangle(
        this,
        this.getTexture("panel-fluid-glass"),
        62, 74 - 18 * 2,
        106, 52
    ) {

      @Override
      public boolean elementIsVisible(int mouseX, int mouseY) {

        AutomatorContainer.EnumState containerState;
        containerState = AutomatorGuiContainer.this.getContainerState();
        return (containerState == AutomatorContainer.EnumState.Fluid);
      }
    });
  }

  private void createInventoryPanelElements() {

    for (int i = 0; i < 26; i++) {
      int x = i % 9;
      int y = i / 9;
      int slotIndex = i;
      this.guiContainerElementAdd(new GuiElementInventoryGhostItem(
          slotIndex,
          this.tile.getPos(),
          this::getContainerState,
          () -> this.tile.getInventoryGhostItemStackHandler().getStackInSlot(slotIndex),
          this,
          8 + (x * 18), 38 + (y * 18)
      ));
    }

    this.guiContainerElementAdd(new GuiElementButtonInventoryLockMode(
        this.tile.getPos(),
        this.tile::isInventoryLocked,
        this::getContainerState,
        this,
        new Texture[]{
            // Expects regular textures first, then hovered textures
            this.getTexture("inventory-button-lock-unlocked"),
            this.getTexture("inventory-button-lock-locked"),
            this.getTexture("inventory-button-lock-unlocked-hovered"),
            this.getTexture("inventory-button-lock-locked-hovered")
        },
        8 + (8 * 18), 38 + (2 * 18)
    ));
  }

  private void createPatternPanelElements() {

    for (int i = 0; i < 9; i++) {
      int slotIndex = i;
      this.guiContainerElementAdd(new GuiElementButtonOutputMode(
          this.tile.getPos(),
          slotIndex,
          () -> this.tile.getOutputMode(slotIndex),
          this::getContainerState,
          this,
          new Texture[]{
              // Expects regular textures first, then hovered textures
              this.getTexture("pattern-button-keep"),
              this.getTexture("pattern-button-manual"),
              this.getTexture("pattern-button-inventory"),
              this.getTexture("pattern-button-export"),
              this.getTexture("pattern-button-keep-hovered"),
              this.getTexture("pattern-button-manual-hovered"),
              this.getTexture("pattern-button-inventory-hovered"),
              this.getTexture("pattern-button-export-hovered")
          },
          8 + (18 * i), 74,
          16, 16
      ));
    }
  }

  private void createSelectedTabElements() {

    // selected gear tab
    this.guiContainerElementAdd(new GuiElementButtonAutomatorTabSelected(
        AutomatorContainer.EnumState.Gear,
        this,
        this.getTexture("tab-gear-selected"),
        12, 17,
        20, 19
    ));

    // selected pattern tab
    this.guiContainerElementAdd(new GuiElementButtonAutomatorTabSelected(
        AutomatorContainer.EnumState.Pattern,
        this,
        this.getTexture("tab-pattern-selected"),
        12 + 20, 17,
        20, 19
    ));

    // selected inventory tab
    this.guiContainerElementAdd(new GuiElementButtonAutomatorTabSelected(
        AutomatorContainer.EnumState.Inventory,
        this,
        this.getTexture("tab-inventory-selected"),
        12 + 20 * 2, 17,
        20, 19
    ));

    // selected fluid tab
    this.guiContainerElementAdd(new GuiElementButtonAutomatorTabSelected(
        AutomatorContainer.EnumState.Fluid,
        this,
        this.getTexture("tab-fluid-selected"),
        12 + 20 * 3, 17,
        20, 19
    ));

    // selected tool tab
    this.guiContainerElementAdd(new GuiElementButtonAutomatorTabSelected(
        AutomatorContainer.EnumState.Tool,
        this,
        this.getTexture("tab-tool-selected"),
        12 + 20 * 4, 17,
        20, 19
    ));
  }

  private void createPowerPanelElements() {

    // lit power bar
    this.guiContainerElementAdd(new GuiElementPowerBar(
        this.tile::getEnergyAmount,
        this.tile::getEnergyCapacity,
        this,
        this.getTexture("power-power-bar-lit"),
        74, 49,
        83, 7
    ));

    // lit duration bar
    this.guiContainerElementAdd(new GuiElementDurationBar(
        this.tile::getProgress,
        this,
        this.getTexture("power-duration-bar-lit"),
        74, 57,
        83, 4
    ));
  }

  private void createPanelElement() {

    // panel texture
    this.guiContainerElementAdd(new GuiElementAutomatorPanel(
        this,
        new Texture[]{
            this.getTexture("background#panel-power"),
            this.getTexture("panel-pattern"),
            this.getTexture("panel-inventory"),
            this.getTexture("panel-fluid"),
            this.getTexture("panel-tool")
        },
        5, 35,
        166, 58
    ));
  }

  private void createBackgroundElement() {

    // background texture
    this.guiContainerElementAdd(new GuiElementTextureRectangle(
        this,
        this.getTexture("background#all"),
        0,
        0,
        this.xSize,
        this.ySize
    ));
  }

  private void createContainerTextElements() {

    // title
    this.guiContainerElementAdd(new GuiElementTitle(
        this,
        "tile.artisanautomation.automator.name",
        8,
        7
    ));

    // inventory title
    this.guiContainerElementAdd(new GuiElementTitle(
        this,
        "container.inventory",
        8,
        this.ySize - 93
    ));
  }

  private void createDeselectedTabElements() {

    // deselected gear tab
    {
      Texture texture = this.getTexture("tab-gear-dark");
      this.guiContainerElementAdd(new GuiElementButtonAutomatorTab(
          AutomatorContainer.EnumState.Gear,
          this,
          texture,
          12, 21,
          20, 18
      ));
    }

    // deselected pattern tab
    {
      Texture texture = this.getTexture("tab-pattern-dark");
      this.guiContainerElementAdd(new GuiElementButtonAutomatorTab(
          AutomatorContainer.EnumState.Pattern,
          this,
          texture,
          12 + 20, 21,
          20, 18
      ));
    }

    // deselected inventory tab
    {
      Texture texture = this.getTexture("tab-inventory-dark");
      this.guiContainerElementAdd(new GuiElementButtonAutomatorTab(
          AutomatorContainer.EnumState.Inventory,
          this,
          texture,
          12 + 20 * 2, 21,
          20, 18
      ));
    }

    // deselected fluid tab
    {
      Texture texture = this.getTexture("tab-fluid-dark");
      this.guiContainerElementAdd(new GuiElementButtonAutomatorTab(
          AutomatorContainer.EnumState.Fluid,
          this,
          texture,
          12 + 20 * 3, 21,
          20, 18
      ));
    }
    // deselected tool tab
    {
      Texture texture = this.getTexture("tab-tool-dark");
      this.guiContainerElementAdd(new GuiElementButtonAutomatorTab(
          AutomatorContainer.EnumState.Tool,
          this,
          texture,
          12 + 20 * 4, 21,
          20, 18
      ));
    }
  }

  public AutomatorContainer.EnumState getContainerState() {

    return this.container.getState();
  }

  @Override
  public void drawString(String translateKey, int x, int y) {

    FontRenderer fontRenderer = this.fontRenderer;
    GuiHelper.drawStringOutlined(translateKey, x, y, fontRenderer, TEXT_OUTLINE_COLOR);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {

    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void renderHoveredToolTip(int x, int y) {

    if (this.mc.player.inventory.getItemStack().isEmpty()) {
      Slot slotUnderMouse = this.getSlotUnderMouse();

      if (slotUnderMouse instanceof InventorySlot
          && !slotUnderMouse.getHasStack()) {
        this.renderInventoryTooltip(x, y, ((InventorySlot) slotUnderMouse).getIndex());

      } else if (slotUnderMouse != null
          && slotUnderMouse.getHasStack()) {
        this.renderToolTip(slotUnderMouse.getStack(), x, y);
      }
    }
  }

  @Override
  protected void renderToolTip(ItemStack stack, int x, int y) {

    Slot slotUnderMouse = this.getSlotUnderMouse();

    if (slotUnderMouse instanceof TableSlot) {
      this.renderTableTooltip(stack, x, y);

    } else {
      super.renderToolTip(stack, x, y);
    }
  }

  private void renderInventoryTooltip(int x, int y, int index) {

    ItemStack ghostStack = this.tile.getInventoryGhostItemStackHandler().getStackInSlot(index);

    if (!ghostStack.isEmpty()
        && this.tile.getInventoryItemStackHandler().getStackInSlot(index).isEmpty()) {
      List<String> tooltip = new ArrayList<>();
      tooltip.add(ghostStack.getRarity().rarityColor + ghostStack.getDisplayName());
      tooltip.add(I18n.format(
          "tooltip.artisanautomation.automator.clear.ghost.item",
          TextFormatting.DARK_GRAY,
          TextFormatting.DARK_GRAY
      ));
      FontRenderer font = ghostStack.getItem().getFontRenderer(ghostStack);
      this.drawHoveringText(tooltip, x, y, (font == null ? this.fontRenderer : font));
      net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }
  }

  private void renderTableTooltip(ItemStack stack, int x, int y) {

    List<String> tooltip = new ArrayList<>();

    tooltip.add(stack.getRarity().rarityColor + stack.getDisplayName());

    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
        || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {

      TileAutomator.Stats stats = this.tile.getStats();

      int speedModifier = (int) (stats.getSpeed().get() * 100);
      tooltip.add(TextFormatting.GRAY + TooltipUtil.getSpeedString(speedModifier, false));

      int energyUsageModifier = (int) (stats.getEnergyUsage().get() * 100);
      tooltip.add(TextFormatting.GRAY + TooltipUtil.getEnergyUsageString(energyUsageModifier, false));

      int fluidCapacityModifier = (int) (stats.getFluidCapacity().get() * 100);
      tooltip.add(TextFormatting.GRAY + TooltipUtil.getFluidCapacityString(fluidCapacityModifier, false));

      int energyCapacityModifier = (int) (stats.getEnergyCapacity().get() * 100);
      tooltip.add(TextFormatting.GRAY + TooltipUtil.getEnergyCapacityString(energyCapacityModifier, false));

      boolean autoImportItems = stats.getAutoImportItems().get();
      tooltip.add(TextFormatting.GRAY + TooltipUtil.getAutoImportItemsString(autoImportItems));

      boolean autoExportItems = stats.getAutoExportItems().get();
      tooltip.add(TextFormatting.GRAY + TooltipUtil.getAutoExportItemsString(autoExportItems));

      boolean autoImportFluids = stats.getAutoImportFluids().get();
      tooltip.add(TextFormatting.GRAY + TooltipUtil.getAutoImportFluidsString(autoImportFluids));

    } else {
      tooltip.add(TooltipHelper.tooltipHoldShiftStringGet());
    }

    FontRenderer font = stack.getItem().getFontRenderer(stack);
    this.drawHoveringText(tooltip, x, y, (font == null ? this.fontRenderer : font));
    net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
  }

  private Texture getTexture(String path) {

    PackedData.ImageData imageData = PackAPI.getImageData(new ResourceLocation(ModuleAutomator.MOD_ID, path));
    ResourceLocation atlasResourceLocation = new ResourceLocation(ModuleAutomator.MOD_ID, imageData.atlas);
    PackedData.AtlasData atlasData = PackAPI.getAtlasData(atlasResourceLocation);
    return new Texture(atlasResourceLocation, imageData.u, imageData.v, atlasData.width, atlasData.height);
  }
}
