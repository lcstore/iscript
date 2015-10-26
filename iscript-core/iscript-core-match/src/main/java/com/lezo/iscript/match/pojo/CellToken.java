package com.lezo.iscript.match.pojo;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
public class CellToken {
    private int index;
    private String origin;
    private String value;
    private String creator;
    /**
     * 是否稳定，可靠
     */
    private boolean stable = false;

    public void setValue(String value) {
        if (isStable()) {
            throw new RuntimeException("forbid to set stable token,oldVal:" + this.value + ",newVal:" + value);
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CellToken other = (CellToken) obj;
        return new EqualsBuilder().append(getIndex(), other.getIndex()).append(getValue(), other.getValue())
                .append(isStable(), other.isStable()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getIndex()).append(getValue()).append(isStable()).toHashCode();
    }

    @Override
    public String toString() {
        return "CellToken [index=" + index + ", value=" + value + ", creator=" + creator + "]";
    }

}
