package de.harm.test.mergetest;

import lombok.Data;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class MergeBean <S,T> {
    private final  Class<S> leftClass;
    private final  Class<T> rightClass;

    private Collection<MergePropertyDefinition> mergePropertyDefinitions=new ArrayList<>();

    public void addMapping(String leftProp, String rightProp) {
        mergePropertyDefinitions.add(new MergePropertyDefinition(leftProp, rightProp));
    }

    public void merge(S source , T target) {

    }
}
