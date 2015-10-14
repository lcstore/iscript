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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CellToken other = (CellToken) obj;
        return new EqualsBuilder().append(getIndex(), other.getIndex()).append(getValue(), other.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getIndex()).append(getValue()).toHashCode();
    }

    @Override
    public String toString() {
        return "CellToken [index=" + index + ", value=" + value + ", creator=" + creator + "]";
    }

}
