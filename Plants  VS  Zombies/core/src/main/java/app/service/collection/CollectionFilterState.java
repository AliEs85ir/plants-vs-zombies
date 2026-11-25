package app.service.collection;

/**
 * Draft/applied filter selections for one Collection tab.
 * Selection alone does not filter until {@link #applyDraft()} (Apply button).
 */
public final class CollectionFilterState {
    private String draftCategoryId;
    private String appliedCategoryId;
    private CollectionUnlockFilter draftUnlock = CollectionUnlockFilter.ALL;
    private CollectionUnlockFilter appliedUnlock = CollectionUnlockFilter.ALL;
    private boolean draftUpgradeableOnly;
    private boolean appliedUpgradeableOnly;
    private final boolean supportsUpgradeable;

    private CollectionFilterState(boolean supportsUpgradeable) {
        this.supportsUpgradeable = supportsUpgradeable;
    }

    public static CollectionFilterState forPlants() {
        return new CollectionFilterState(true);
    }

    public static CollectionFilterState forZombies() {
        return new CollectionFilterState(false);
    }

    public boolean supportsUpgradeable() {
        return supportsUpgradeable;
    }

    /** Null / blank means "all categories". */
    public String getCategoryId() {
        return appliedCategoryId;
    }

    public String getDraftCategoryId() {
        return draftCategoryId;
    }

    public void setDraftCategoryId(String categoryId) {
        this.draftCategoryId = blankToNull(categoryId);
    }

    public CollectionUnlockFilter getUnlockFilter() {
        return appliedUnlock == null ? CollectionUnlockFilter.ALL : appliedUnlock;
    }

    public CollectionUnlockFilter getDraftUnlockFilter() {
        return draftUnlock == null ? CollectionUnlockFilter.ALL : draftUnlock;
    }

    public void setDraftUnlockFilter(CollectionUnlockFilter filter) {
        this.draftUnlock = filter == null ? CollectionUnlockFilter.ALL : filter;
    }

    public boolean isUpgradeableOnly() {
        return supportsUpgradeable && appliedUpgradeableOnly;
    }

    public boolean isDraftUpgradeableOnly() {
        return supportsUpgradeable && draftUpgradeableOnly;
    }

    public void setDraftUpgradeableOnly(boolean upgradeableOnly) {
        this.draftUpgradeableOnly = supportsUpgradeable && upgradeableOnly;
    }

    public void applyDraft() {
        appliedCategoryId = draftCategoryId;
        appliedUnlock = draftUnlock == null ? CollectionUnlockFilter.ALL : draftUnlock;
        appliedUpgradeableOnly = supportsUpgradeable && draftUpgradeableOnly;
    }

    public boolean hasPendingChanges() {
        return !equalsNullable(draftCategoryId, appliedCategoryId)
            || getDraftUnlockFilter() != getUnlockFilter()
            || isDraftUpgradeableOnly() != isUpgradeableOnly();
    }

    public void reset() {
        draftCategoryId = null;
        appliedCategoryId = null;
        draftUnlock = CollectionUnlockFilter.ALL;
        appliedUnlock = CollectionUnlockFilter.ALL;
        draftUpgradeableOnly = false;
        appliedUpgradeableOnly = false;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static boolean equalsNullable(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }
}
