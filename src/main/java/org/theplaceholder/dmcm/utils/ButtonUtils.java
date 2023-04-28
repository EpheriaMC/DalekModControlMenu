package org.theplaceholder.dmcm.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;

public class ButtonUtils {

    public static void pressButton(Hand hand, BlockRayTraceResult rayTraceResult){
        Minecraft.getInstance().getConnection().send(new CPlayerTryUseItemOnBlockPacket(hand, rayTraceResult));
        sleep();
    }

    public static void sleep(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
