//package net.doubledoordev.placeableTools.network;
//
//import net.doubledoordev.placeableTools.block.ToolTE;
//import net.minecraft.entity.player.EntityPlayer;
//
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//
//public class PacketHandler implements IPacketHandler
//{
//    @Override
//    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
//    {
//        try
//        {
//            if (packet.channel.equals(CHANNEL_SIGN_UPDATE))
//            {
//                ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
//                DataInputStream stream = new DataInputStream(streambyte);
//
//                ((ToolTE) ((EntityPlayer) player).worldObj.getBlockTileEntity(stream.readInt(), stream.readInt(), stream.readInt())).setText(stream.readBoolean(), new String[] {stream.readUTF(), stream.readUTF(), stream.readUTF(), stream.readUTF()});
//
//                stream.close();
//                streambyte.close();
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//}
