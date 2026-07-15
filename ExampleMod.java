package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

@Mod(modid = "triggerbot", version = "1.0")
public class ExampleMod extends GuiScreen {
    
    private static KeyBinding toggleKey = new KeyBinding("Toggle TriggerBot", Keyboard.KEY_R, "TriggerBot");
    private static boolean isActive = false;
    private static long lastClick = 0;
    
    private static int cps = 12;
    private static boolean onlyWhenLooking = true;
    private static int reachDistance = 6;
    
    private int tempCps = cps;
    private boolean tempOnlyWhenLooking = onlyWhenLooking;
    private int tempReach = reachDistance;
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(toggleKey);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        
        while (toggleKey.isPressed()) {
            isActive = !isActive;
            String status = isActive ? "§aON" : "§cOFF";
            Minecraft.getMinecraft().thePlayer.sendChatMessage("§6[TriggerBot] §f" + status);
        }
        
        if (isActive && Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown()) {
            if (canAttack()) {
                long now = System.currentTimeMillis();
                if (now - lastClick >= 1000 / cps) {
                    Minecraft.getMinecraft().clickMouse();
                    lastClick = now;
                }
            }
        }
        
        // Открытие GUI по клавише L
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            Minecraft.getMinecraft().displayGuiScreen(new ExampleMod());
        }
    }
    
    private boolean canAttack() {
        if (onlyWhenLooking) {
            if (Minecraft.getMinecraft().objectMouseOver == null) return false;
            if (Minecraft.getMinecraft().objectMouseOver.entityHit == null) return false;
            
            double dist = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(
                Minecraft.getMinecraft().objectMouseOver.entityHit
            );
            if (dist > reachDistance) return false;
        }
        return true;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int center = this.width / 2;
        
        this.drawCenteredString(this.fontRendererObj, "§6TriggerBot Settings", center, 10, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Status: " + (isActive ? "§aON" : "§cOFF"), center, 30, 0xFFFFFF);
        
        this.drawString(this.fontRendererObj, "CPS: " + tempCps, 20, 70, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "Reach: " + tempReach + " blocks", 20, 100, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "Only when looking: " + (tempOnlyWhenLooking ? "§aON" : "§cOFF"), 20, 130, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int center = this.width / 2;
        
        this.buttonList.add(new GuiButton(0, center - 60, 65, 20, 20, "-"));
        this.buttonList.add(new GuiButton(1, center + 40, 65, 20, 20, "+"));
        
        this.buttonList.add(new GuiButton(2, center - 60, 95, 20, 20, "-"));
        this.buttonList.add(new GuiButton(3, center + 40, 95, 20, 20, "+"));
        
        this.buttonList.add(new GuiButton(4, center - 50, 125, 100, 20, "Toggle Only Looking"));
        
        this.buttonList.add(new GuiButton(6, center - 60, 190, 50, 20, "Apply"));
        this.buttonList.add(new GuiButton(7, center + 10, 190, 50, 20, "Close"));
        
        this.buttonList.add(new GuiButton(8, center - 50, 220, 100, 20, isActive ? "Turn OFF" : "Turn ON"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        switch(button.id) {
            case 0: if (tempCps > 1) tempCps--; break;
            case 1: if (tempCps < 30) tempCps++; break;
            case 2: if (tempReach > 2) tempReach--; break;
            case 3: if (tempReach < 10) tempReach++; break;
            case 4: tempOnlyWhenLooking = !tempOnlyWhenLooking; break;
            case 6:
                cps = tempCps;
                onlyWhenLooking = tempOnlyWhenLooking;
                reachDistance = tempReach;
                Minecraft.getMinecraft().thePlayer.sendChatMessage("§6[TriggerBot] §aSettings applied!");
                break;
            case 7: this.mc.displayGuiScreen(null); break;
            case 8: isActive = !isActive; this.initGui(); break;
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}