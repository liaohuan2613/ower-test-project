package com.lhk.domain;


public class BlockTag extends ValueObject {
    private static final long serialVersionUID = 1L;
    private String tagCode;
    private String tagName;
    private String source;
    private String level;
    private String order;

    public BlockTag() {
        super();
    }

    public BlockTag(String code, String aName, String source, String level, String order) {
        super();
        this.setTagCode(code);
        this.setTagName(aName);
        this.setSource(source);
        this.setLevel(level);
        this.setOrder(order);
    }

    public String getTagCode() {
        return this.tagCode;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tagCode == null) ? 0 : tagCode.hashCode());
        result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BlockTag other = (BlockTag) obj;
        if (tagCode == null) {
            return other.tagCode == null;
        } else if (!tagCode.equals(other.tagCode)) {
            return false;
        }
        if (tagName == null) {
            return other.tagName == null;
        } else {
            return tagName.equals(other.tagName);
        }
    }


}
