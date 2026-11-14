package com.l7cor1s.myleveling.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Soft Curios bridge via reflection — no hard dependency.
 */
public final class CuriosCompat {
    private static Boolean available;
    private static Method getCuriosInventory;
    private static Method getStacksHandler;
    private static Method getStacks;
    private static Method getSlots;
    private static Method getStackInSlot;

    private CuriosCompat() {
    }

    public static boolean available() {
        if (available == null) {
            available = CompatMods.isLoaded(CompatMods.CURIOS);
            if (available) {
                try {
                    Class<?> api = Class.forName("top.theillusivec4.curios.api.CuriosApi");
                    getCuriosInventory = api.getMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
                } catch (ReflectiveOperationException e) {
                    available = false;
                }
            }
        }
        return Boolean.TRUE.equals(available);
    }

    public static List<ItemStack> equippedCurios(Player player) {
        List<ItemStack> out = new ArrayList<>();
        if (!available()) {
            return out;
        }
        try {
            Object optionalInv = getCuriosInventory.invoke(null, player);
            if (!(optionalInv instanceof Optional<?> opt) || opt.isEmpty()) {
                return out;
            }
            Object inv = opt.get();
            if (getStacksHandler == null) {
                getStacksHandler = inv.getClass().getMethod("getCurios");
            }
            Object map = getStacksHandler.invoke(inv);
            if (!(map instanceof java.util.Map<?, ?> curios)) {
                return out;
            }
            for (Object handler : curios.values()) {
                if (getStacks == null) {
                    getStacks = handler.getClass().getMethod("getStacks");
                }
                Object stacks = getStacks.invoke(handler);
                if (getSlots == null) {
                    getSlots = stacks.getClass().getMethod("getSlots");
                }
                if (getStackInSlot == null) {
                    getStackInSlot = stacks.getClass().getMethod("getStackInSlot", int.class);
                }
                int slots = (int) getSlots.invoke(stacks);
                for (int i = 0; i < slots; i++) {
                    Object stackObj = getStackInSlot.invoke(stacks, i);
                    if (stackObj instanceof ItemStack stack && !stack.isEmpty()) {
                        out.add(stack);
                    }
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // Soft fail — inventory fallback still works.
        }
        return out;
    }
}
