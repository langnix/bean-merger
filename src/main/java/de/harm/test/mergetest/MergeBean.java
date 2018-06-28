package de.harm.test.mergetest;

import lombok.Data;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Data
public class MergeBean<S, T> {
    private final Class<S> leftClass;
    private final Class<T> rightClass;

    private Collection<MergePropertyDefinition> mergePropertyDefinitions = new ArrayList<>();

    public void addMapping(String leftProp, String rightProp) {
        Function<S,?>  rd= getReader(leftClass,convert2Path(leftProp));

        mergePropertyDefinitions.add(new MergePropertyDefinition( leftProp,rd, rightProp));
    }

    private List<String> convert2Path(String leftProp) {
       return Arrays.asList(StringUtils.split(leftProp,"."));
    }

    private  Function getReader(Class srcClass, List<String> path) {
        if(path.isEmpty()) return Function.identity();
        String top=path.get(0);
        PropertyDescriptor pdTop = BeanUtils.getPropertyDescriptor(srcClass, top);
        if (pdTop==null) {
            throw new IllegalArgumentException("Unknown property on:" + srcClass + " :"+ top);
        }
        Method topReader = pdTop.getReadMethod();

        Function remFct = getReader(topReader.getReturnType(), path.subList(1, path.size()));

        return new Function() {
            @Override
            public Object apply(Object c) {
                try {
                    Object next = topReader.invoke(c);
                    if (next==null) return null;
                    else return remFct.apply(next);
                } catch (IllegalAccessException |InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        };

    }

    public void merge(S source, T target) {
        mergePropertyDefinitions.forEach(pd -> merge(pd, source, target));
    }

    //
    private void merge(MergePropertyDefinition pd, S source, T target) {
        try {
            Object value = pd.getLeftRead().invoke(source);
            pd.getRightWrite().invoke(target, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to copy:" + pd.getLeftProp() + "->" + pd.getRightProp());
        }
    }
}
