package github.muhsenerdev.commons.core.vo;

import java.util.Objects;

public abstract class SingleVO<T> {

    public abstract T getValue();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SingleVO<?> singleVO = (SingleVO<?>) o;
        return Objects.equals(getValue(), singleVO.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return getValue() != null ? getValue().toString() : "";
    }
}
