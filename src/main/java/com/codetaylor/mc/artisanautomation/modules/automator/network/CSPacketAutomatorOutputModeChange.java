package com.codetaylor.mc.artisanautomation.modules.automator.network;

import com.codetaylor.mc.artisanautomation.modules.automator.tile.automator.TileAutomator;
import com.codetaylor.mc.athenaeum.spi.packet.SPacketTileEntityBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CSPacketAutomatorOutputModeChange
    extends SPacketTileEntityBase<CSPacketAutomatorOutputModeChange> {

  private int outputSlotIndex;

  @SuppressWarnings("unused")
  public CSPacketAutomatorOutputModeChange() {
    // serialization
  }

  public CSPacketAutomatorOutputModeChange(BlockPos pos, int slotIndex) {

    super(pos);
    this.outputSlotIndex = slotIndex;
  }

  @Override
  public void fromBytes(ByteBuf buf) {

    super.fromBytes(buf);
    this.outputSlotIndex = buf.readByte();
  }

  @Override
  public void toBytes(ByteBuf buf) {

    super.toBytes(buf);
    buf.writeByte(this.outputSlotIndex);
  }

  @Override
  protected IMessage onMessage(
      CSPacketAutomatorOutputModeChange message,
      MessageContext ctx,
      TileEntity tileEntity
  ) {

    if (tileEntity instanceof TileAutomator) {
      ((TileAutomator) tileEntity).cycleOutputMode(message.outputSlotIndex);
    }

    return null;
  }
}
