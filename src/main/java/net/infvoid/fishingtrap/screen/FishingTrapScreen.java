package net.infvoid.fishingtrap.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FishingTrapScreen extends HandledScreen<FishingTrapScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("fishingtrap", "textures/gui/fishing_trap_gui.png");

    private final PlayerInventory playerInventory;

    public FishingTrapScreen(FishingTrapScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 8; // Align left like chests/barrels
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {




        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

    }


    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.getMatrices().push();

        float scale = 0.9f; // You can tweak this
        context.getMatrices().scale(scale, scale, 1);

        // Title text (Fishing Trap) — top left
        context.drawText(
                this.textRenderer,
                this.title,
                (int) (8 / scale),
                (int) (6 / scale),
                4210752,
                false
        );

        // Player Inventory label — above hotbar
        context.drawText(
                this.textRenderer,
                this.playerInventory.getDisplayName(),
                (int) (8 / scale),
                (int) ((this.backgroundHeight - 94) / scale),
                4210752,
                false
        );

        context.getMatrices().pop(); // Restore matrix
    }


}


