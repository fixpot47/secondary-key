package dev.fixpot47.secondarykey;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public final class SecondaryKeysScreen extends Screen {
    private final Screen parent;
    private KeyMapping[] mappings = new KeyMapping[0];
    private KeyMapping selecting;
    private int page;
    private int rowsPerPage;

    public SecondaryKeysScreen(Screen parent) {
        super(Component.translatable("secondarykey.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.mappings = Arrays.copyOf(this.minecraft.options.keyMappings, this.minecraft.options.keyMappings.length);
        Arrays.sort(this.mappings);

        this.rowsPerPage = Math.max(4, Math.min(8, (this.height - 105) / 24));
        int pageCount = getPageCount();
        if (this.page >= pageCount) {
            this.page = Math.max(0, pageCount - 1);
        }

        int left = this.width / 2 - 165;
        int startY = 52;
        int startIndex = this.page * this.rowsPerPage;
        int endIndex = Math.min(this.mappings.length, startIndex + this.rowsPerPage);

        for (int i = startIndex; i < endIndex; i++) {
            KeyMapping mapping = this.mappings[i];
            int y = startY + (i - startIndex) * 24;

            Button bindButton = Button.builder(getBindingLabel(mapping), button -> {
                this.selecting = mapping;
                this.rebuildWidgets();
            }).bounds(left, y, 270, 20).build();
            this.addRenderableWidget(bindButton);

            Button clearButton = Button.builder(Component.translatable("secondarykey.clear"), button -> {
                SecondaryKeyConfig.clearSecondaryKey(mapping);
                if (this.selecting == mapping) {
                    this.selecting = null;
                }
                this.rebuildWidgets();
            }).bounds(left + 276, y, 54, 20).build();
            clearButton.active = SecondaryKeyConfig.hasSecondaryKey(mapping);
            this.addRenderableWidget(clearButton);
        }

        int footerY = this.height - 28;
        Button previous = Button.builder(Component.translatable("secondarykey.previous"), button -> {
            this.page--;
            this.selecting = null;
            this.rebuildWidgets();
        }).bounds(this.width / 2 - 155, footerY, 90, 20).build();
        previous.active = this.page > 0;
        this.addRenderableWidget(previous);

        Button done = Button.builder(Component.translatable("gui.done"), button -> this.onClose())
            .bounds(this.width / 2 - 50, footerY, 100, 20).build();
        this.addRenderableWidget(done);

        Button next = Button.builder(Component.translatable("secondarykey.next"), button -> {
            this.page++;
            this.selecting = null;
            this.rebuildWidgets();
        }).bounds(this.width / 2 + 65, footerY, 90, 20).build();
        next.active = this.page + 1 < pageCount;
        this.addRenderableWidget(next);
    }

    private Component getBindingLabel(KeyMapping mapping) {
        String action = Component.translatable(mapping.getName()).getString();
        if (mapping == this.selecting) {
            return Component.literal(action + "  →  ").append(Component.translatable("secondarykey.press_key"));
        }

        InputConstants.Key secondary = SecondaryKeyConfig.getSecondaryKey(mapping);
        Component keyName = secondary == null
            ? Component.translatable("secondarykey.none")
            : secondary.getDisplayName();

        return Component.literal(action + "  →  ").append(keyName);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.selecting != null) {
            if (event.isEscape()) {
                this.selecting = null;
            } else {
                SecondaryKeyConfig.setSecondaryKey(this.selecting, InputConstants.getKey(event));
                this.selecting = null;
            }
            this.rebuildWidgets();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.selecting != null) {
            SecondaryKeyConfig.setSecondaryKey(this.selecting, InputConstants.Type.MOUSE.getOrCreate(event.button()));
            this.selecting = null;
            this.rebuildWidgets();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 16, -1);
        graphics.centeredText(this.font, Component.translatable("secondarykey.screen.hint"), this.width / 2, 32, -8355712);
        graphics.centeredText(
            this.font,
            Component.translatable("secondarykey.page", this.page + 1, getPageCount()),
            this.width / 2,
            this.height - 42,
            -8355712
        );
    }

    @Override
    public void onClose() {
        SecondaryKeyConfig.save();
        this.minecraft.gui.setScreen(this.parent);
    }

    private int getPageCount() {
        if (this.rowsPerPage <= 0) {
            return 1;
        }
        return Math.max(1, (this.mappings.length + this.rowsPerPage - 1) / this.rowsPerPage);
    }
}
