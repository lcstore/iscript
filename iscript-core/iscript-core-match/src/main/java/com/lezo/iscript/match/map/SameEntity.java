package com.lezo.iscript.match.map;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
public class SameEntity {
    /**
     * 标准值
     */
    private String value;
    /**
     * 同义词组
     */
    private Set<String> sameSet;

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(value).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SameEntity other = (SameEntity) obj;
        return new EqualsBuilder().append(getValue(), other.getValue()).isEquals();
    }
}
