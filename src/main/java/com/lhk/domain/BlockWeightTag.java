package com.lhk.domain;


public class BlockWeightTag extends BlockTag implements Comparable<BlockWeightTag> {
    private static final long serialVersionUID = 1L;
    private String category = "OTHER";
    private double weight;

    public BlockWeightTag(String code, String aName, double theWeight, String source, String level, String order) {
        super(code, aName, source, level, order);
        this.setWeight(theWeight);
    }

    public BlockWeightTag(String code, String aName, String source, String level, String order) {
        super(code, aName, source, level, order);
        this.setWeight(0);
    }

    public BlockWeightTag() {
        super();
    }

    public double getWeight() {
        return this.weight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    //	public int getCount() {
//	    return this.count;
//    }

    public void addWeight(double theWeight) {
        this.weight = this.weight + theWeight;
    }

    public void updateWeight(double weight) {
        this.setWeight(weight);
    }

    @Override
    public int compareTo(BlockWeightTag o) {
        return Double.valueOf(this.weight - o.weight).intValue();
    }


    @Override
    public String toString() {
        return this.getTagName() + ":" + this.weight;
    }

    private void setWeight(double theWeight) {
        this.weight = theWeight;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            BlockWeightTag typedObject = (BlockWeightTag) anObject;
            equalObjects = this.getTagCode().equals(typedObject.getTagCode()) && this.getTagName().equals(typedObject.getTagName());
        }

        return equalObjects;
    }
}
