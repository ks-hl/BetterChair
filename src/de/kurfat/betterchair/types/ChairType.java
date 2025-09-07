package de.kurfat.betterchair.types;

public enum ChairType {
    STAIR(StairChair::new),
    SLAB(SlabChair::new),
    BED(BedChair::new),
    SNOW(SnowChair::new),
    CARPET(CarpetChair::new),
    PATH(PathChair::new),
    LILY_PAD(LilyPadChair::new),

    // catch-all. Needs to be last for iteration order
    BLOCK(BlockChair::new);
    // Don't add any after block

    private final ChairConstructor chairConstructor;

    ChairType(ChairConstructor chairConstructor) {
        this.chairConstructor = chairConstructor;
    }

    public ChairConstructor getChairConstructor() {
        return chairConstructor;
    }
}
