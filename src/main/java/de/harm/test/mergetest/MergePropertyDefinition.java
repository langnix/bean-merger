package de.harm.test.mergetest;

import jdk.nashorn.internal.runtime.arrays.IteratorAction;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;

@Data
public class MergePropertyDefinition<S, T> {
    private final MergeBean<S, T> mergeBean;
    private final String leftProp;
    private final String rightProp;

    private final Method leftRead;
    private final Method rightWrite;

    public MergePropertyDefinition(MergeBean<S, T> mergeBean, String leftProp, String rightProp) {
        this.mergeBean = mergeBean;
        this.leftProp = leftProp;
        this.rightProp = rightProp;
        this.leftRead = BeanUtils.getPropertyDescriptor(mergeBean.getLeftClass(), leftProp).getReadMethod();
        this.rightWrite = BeanUtils.getPropertyDescriptor(mergeBean.getRightClass(), rightProp).getWriteMethod();
        if (leftRead == null)
            throw new IllegalArgumentException("No read on source: " + mergeBean.getLeftClass() + "." + leftProp);
        if (rightWrite == null)
            throw new IllegalArgumentException("No write on target: " + mergeBean.getRightClass() + "." + rightProp);
    }


}