package de.harm.test.mergetest;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

public class AccessFunctionBuilder {

  private List<String> convert2PathElements(String path) {
    return Arrays.asList(StringUtils.tokenizeToStringArray(path, "."));
  }

  public Function getReader(Class srcClass, String path) {
    return getReader(srcClass, convert2PathElements(path));

  }

  public Function getReader(Class srcClass, List<String> path) {
    if (path.isEmpty()) {
      return Function.identity();
    }
    String top = path.get(0);
    PropertyDescriptor pdTop = BeanUtils.getPropertyDescriptor(srcClass, top);
    if (pdTop == null) {
      throw new IllegalArgumentException("Unknown property on:" + srcClass + " :" + top);
    }
    Method topReader = pdTop.getReadMethod();

    Function remFct = getReader(topReader.getReturnType(), path.subList(1, path.size()));

    return new Function() {
      @Override
      public Object apply(Object c) {
        try {
          Object next = topReader.invoke(c);
          if (next == null) {
            return null;
          } else {
            return remFct.apply(next);
          }
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new IllegalStateException(e);
        }
      }
    };

  }

}
