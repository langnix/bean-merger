package de.harm.test.mergetest;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@Data
public class MergeBean<S, T> {
  private final Class<S> leftClass;
  private final Class<T> rightClass;
  private final AccessFunctionBuilder accessFunctionBuilder = new AccessFunctionBuilder();
  private Collection<MergePropertyDefinition> mergePropertyDefinitions = new ArrayList<>();

  public void addMapping(String leftProp, String rightProp) {
    Function<S, ?> rd = accessFunctionBuilder.getReader(leftClass, leftProp);

    mergePropertyDefinitions.add(new MergePropertyDefinition(leftProp, rd, rightProp));
  }


  public void merge(S source, T target) {
    mergePropertyDefinitions.forEach(pd -> merge(pd, source, target));
  }

  //
  private void merge(MergePropertyDefinition pd, S source, T target) {
//        try {
    Object value = pd.getLeftRead().apply(source);
//            pd.getRightWrite().invoke(target, value);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new IllegalArgumentException("Unable to copy:" + pd.getLeftProp() + "->" + pd.getRightProp());
//        }
  }
}
