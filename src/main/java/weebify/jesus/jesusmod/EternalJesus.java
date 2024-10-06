package weebify.jesus.jesusmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;

@Mod(modid = EternalJesus.MODID, version = EternalJesus.VERSION)
public class EternalJesus {
    public static final String MODID = "jesusmod";
    public static final String VERSION = "1.0";

    private File saveFile;
    private long lastSaved;
    public static boolean enabled = true;

    public static KeyBinding keybindToggle = new KeyBinding("Toggle Jesus Mod", Keyboard.KEY_BACKSLASH, "Eternal Jesus");
    private Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.saveFile = new File(mc.mcDataDir + "/config", "weebify_eternaljesus.cfg");
        this.lastSaved = saveFile.lastModified();
        ClientRegistry.registerKeyBinding(keybindToggle);
        this.loadSettings();

        MinecraftForge.EVENT_BUS.register(new JesusHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (this.saveFile.lastModified() != this.lastSaved) {
                System.out.println("Save file modified, loading settings");
                this.lastSaved = this.saveFile.lastModified();
                this.loadSettings();
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        int code = keybindToggle.getKeyCode();
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == code) {
            enabled = !enabled;
            mc.ingameGUI.setRecordPlaying("Jesus Mod is now " + EnumChatFormatting.BOLD + (enabled ?  EnumChatFormatting.GREEN +"ON" : EnumChatFormatting.RED+"OFF"), false);
//            mc.thePlayer.addChatMessage(new ChatComponentText("Jesus Mod is now " + EnumChatFormatting.BOLD + (enabled ?  EnumChatFormatting.GREEN +"ON" : EnumChatFormatting.RED+"OFF")));
            this.saveSettings();
        }
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        int code = keybindToggle.getKeyCode();
        if (Mouse.getEventButtonState() && Mouse.getEventButton() == code + 100) {
            enabled = !enabled;
            mc.ingameGUI.setRecordPlaying("Jesus Mod is now " + EnumChatFormatting.BOLD + (enabled ?  EnumChatFormatting.GREEN +"ON" : EnumChatFormatting.RED+"OFF"), false);
//            mc.thePlayer.addChatMessage(new ChatComponentText("Jesus Mod is now " + EnumChatFormatting.BOLD + (enabled ?  EnumChatFormatting.GREEN +"ON" : EnumChatFormatting.RED+"OFF")));
            this.saveSettings();
        }
    }

    public void saveSettings() {
        Configuration c = new Configuration(this.saveFile);
        this.updateSettings(c, true);
        c.save();
    }

    public void loadSettings() {
        Configuration c = new Configuration(this.saveFile);
        c.load();
        this.updateSettings(c, false);
    }

    private void updateSettings(Configuration config, boolean save) {
        Property prop = config.get("EternalJesus", "enabled", true);
        if (save) prop.set(enabled);
        else enabled = prop.getBoolean();
    }
}
