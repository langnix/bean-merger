package de.harm.java.beanmerge;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class MergeBean<S, T> {
  private final Class<S> leftClass;
  private final Class<T> rightClass;
  private final AccessFunctionBuilder accessFunctionBuilder = new AccessFunctionBuilder();
  private Collection<MergePropertyDefinition> mergePropertyDefinitions = new ArrayList<>();

  public void addMapping(String leftProp, String rightProp) {
    AccessReader leftRd = accessFunctionBuilder.getReader(leftClass, leftProp);
    AccessWriter rightWr = accessFunctionBuilder.getWriter(rightClass, rightProp);
    mergePropertyDefinitions.add(new MergePropertyDefinition(leftProp, leftRd, rightProp, rightWr));
  }


  public void merge(S source, T target) {
    mergePropertyDefinitions.forEach(pd -> merge(pd, source, target));
  }

  //
  private void merge(MergePropertyDefinition pd, S source, T target) {
//        try {
    Object value = pd.getLeftRead().apply(source);
    pd.getRigthWriter().accept(target, value);
//            pd.getRightWrite().invoke(target, value);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new IllegalArgumentException("Unable to copy:" + pd.getLeftProp() + "->" + pd.getRightProp());
//        }
  }
}
