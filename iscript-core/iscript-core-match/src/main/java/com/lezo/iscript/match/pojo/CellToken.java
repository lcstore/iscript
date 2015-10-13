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
    private String token;
    private String creator;

    @Override
    public String toString() {
        return "CellToken [index=" + index + ", token=" + token + ", creator=" + creator + "]";
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
        return new EqualsBuilder().append(getIndex(), other.getIndex()).append(getToken(), other.getToken()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getIndex()).append(getToken()).toHashCode();
    }

}
