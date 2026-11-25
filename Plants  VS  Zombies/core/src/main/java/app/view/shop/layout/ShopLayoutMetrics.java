package app.view.shop.layout;

import app.view.shop.config.ShopConfig;

public final class ShopLayoutMetrics {
    private final float stageWidth;
    private final float stageHeight;
    private final float scaleX;
    private final float scaleY;
    private final float uniformScale;
    private final float marginLeft;
    private final float marginRight;
    private final float marginTop;
    private final float marginBottom;
    private final float contentWidth;
    private final float contentHeight;
    private final float leftPanelWidth;
    private final float rightPanelWidth;
    private final float panelHeight;

    public ShopLayoutMetrics(float stageWidth, float stageHeight, ShopConfig config) {
        this(stageWidth, stageHeight, config, -1f);
    }

    public ShopLayoutMetrics(float stageWidth, float stageHeight, ShopConfig config, float topMarginOverride) {
        this.stageWidth = Math.max(1f, stageWidth);
        this.stageHeight = Math.max(1f, stageHeight);
        ShopConfig.LayoutConfig layout = config.layout == null ? new ShopConfig.LayoutConfig() : config.layout;
        float refW = layout.referenceWidth > 0f ? layout.referenceWidth : 1280f;
        float refH = layout.referenceHeight > 0f ? layout.referenceHeight : 720f;
        this.scaleX = this.stageWidth / refW;
        this.scaleY = this.stageHeight / refH;
        this.uniformScale = Math.min(scaleX, scaleY);

        this.marginLeft = sx(layout.marginLeft);
        this.marginRight = sx(layout.marginRight);
        this.marginTop = topMarginOverride >= 0f ? topMarginOverride : sy(layout.marginTop);
        this.marginBottom = sy(layout.marginBottom);
        this.contentWidth = Math.max(0f, this.stageWidth - marginLeft - marginRight);
        this.contentHeight = Math.max(0f, this.stageHeight - marginTop - marginBottom);

        float configuredLeft = sx(config.leftPanel.width);
        if (config.layout != null && config.layout.leftPanelWidthRatio > 0f) {
            configuredLeft = contentWidth * config.layout.leftPanelWidthRatio;
        }
        if (config.layout != null && config.layout.leftPanelMaxWidth > 0f) {
            configuredLeft = Math.min(configuredLeft, sx(config.layout.leftPanelMaxWidth));
        }
        float separatorSpace = sx(config.leftPanel.separatorWidth + config.leftPanel.separatorPadding * 2f);
        this.leftPanelWidth = Math.min(configuredLeft, Math.max(120f, contentWidth - separatorSpace - 200f));
        this.rightPanelWidth = Math.max(0f, contentWidth - leftPanelWidth - separatorSpace);
        this.panelHeight = contentHeight;
    }

    public float stageWidth() {
        return stageWidth;
    }

    public float stageHeight() {
        return stageHeight;
    }

    public float scaleX() {
        return scaleX;
    }

    public float scaleY() {
        return scaleY;
    }

    public float uniformScale() {
        return uniformScale;
    }

    public float sx(float ref) {
        return ref * scaleX;
    }

    public float sy(float ref) {
        return ref * scaleY;
    }

    public float su(float ref) {
        return ref * uniformScale;
    }

    public float marginLeft() {
        return marginLeft;
    }

    public float marginRight() {
        return marginRight;
    }

    public float marginTop() {
        return marginTop;
    }

    public float marginBottom() {
        return marginBottom;
    }

    public float contentWidth() {
        return contentWidth;
    }

    public float contentHeight() {
        return contentHeight;
    }

    public float leftPanelWidth() {
        return leftPanelWidth;
    }

    public float rightPanelWidth() {
        return rightPanelWidth;
    }

    public float panelHeight() {
        return panelHeight;
    }

    public float anchorX(float anchorX, float width) {
        return marginLeft + anchorX * contentWidth - anchorX * width;
    }

    public float anchorY(float anchorY, float height) {
        return marginBottom + anchorY * contentHeight + (1f - anchorY) * (contentHeight - height);
    }
}
