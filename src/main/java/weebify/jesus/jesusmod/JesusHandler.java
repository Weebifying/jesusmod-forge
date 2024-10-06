package weebify.jesus.jesusmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import weebify.jesus.jesusmod.mixins.GuiAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JesusHandler {
    public final List<ResourceLocation> IMAGES = new ArrayList<>();
    public ResourceLocation currentImage = new ResourceLocation(EternalJesus.MODID, "textures/jesus0.png");
    public final ResourceLocation SOUND = new ResourceLocation(EternalJesus.MODID, "jesus_bell");
    public static final Random random = new Random();

    private long startTime = -1;
    private long lastTime = -1;
    private boolean fading = false;
    private boolean scheduleFading = false;
    private boolean soundPlayed = false;
    private static final long FADE_DURATION = 500;
    private static final long DURATION = FADE_DURATION * 2;

    public JesusHandler() {
        IMAGES.add(new ResourceLocation(EternalJesus.MODID, "textures/jesus0.png"));
        IMAGES.add(new ResourceLocation(EternalJesus.MODID, "textures/jesus1.png"));
        IMAGES.add(new ResourceLocation(EternalJesus.MODID, "textures/jesus2.png"));
        IMAGES.add(new ResourceLocation(EternalJesus.MODID, "textures/jesus3.png"));
        IMAGES.add(new ResourceLocation(EternalJesus.MODID, "textures/jesus4.png"));
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && EternalJesus.enabled) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.thePlayer;
            long currentTime = System.currentTimeMillis();

            scheduleFading = player.getHealth() <= 4.f
//                    && player.getEntityWorld() != null
                    && player.isEntityAlive()
                    && mc.playerController.isNotCreative()
                    && !mc.isGamePaused();

            if (scheduleFading) {
                if (currentTime - lastTime >= DURATION) {
                    lastTime = currentTime;
                    fading = false;
                }

                if (!fading) {
                    startTime = currentTime;
                    fading = true;
                    soundPlayed = false;
                    currentImage = IMAGES.get(random.nextInt(IMAGES.size()));
                }
            }

            if (fading) {
                long timeElapsed = currentTime - startTime;
                if (!soundPlayed) {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(SOUND));
                    soundPlayed = true;
                }

                float alpha = Math.max(0.f, 1.f - (float) timeElapsed / FADE_DURATION);
                if (alpha > 0.f) {
                    ScaledResolution scaledResolution = new ScaledResolution(mc);
                    int width = scaledResolution.getScaledWidth();
                    int height = scaledResolution.getScaledHeight();

                    mc.getTextureManager().bindTexture(currentImage);
                    GlStateManager.enableBlend();
                    GlStateManager.color(1.f, 1.f, 1.f, alpha);
                    GuiIngame.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
                    GlStateManager.disableBlend();
                }
            }
        }
    }
}
