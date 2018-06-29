package de.harm.test.mergetest;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
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

  private Function getReader(Class srcClass, List<String> path) {
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
          throw new IllegalStateException("Unable to read on:" + srcClass + " :" + top);
        }
      }
    };

  }

  public BiConsumer getWriter(Class targetClass, String path) {
    return getWriter(targetClass, convert2PathElements(path));
  }

  private BiConsumer getWriter(Class targetClass, List<String> path) {
    return getWriter(targetClass, path.get(0), path.subList(1, path.size()));
  }

  private BiConsumer getWriter(Class targetClass, String top, List<String> rest) {
    PropertyDescriptor pdTop = BeanUtils.getPropertyDescriptor(targetClass, top);
    if (pdTop == null) {
      throw new IllegalArgumentException("Unknown property on:" + targetClass + " :" + top);
    }
    if (rest.isEmpty()) {
      // do it ...
      Method topWriter = pdTop.getWriteMethod();
      if (topWriter == null) {
        throw new IllegalArgumentException("No writer access on:" + targetClass + " :" + top);
      }
      return new BiConsumer() {
        @Override
        public void accept(Object obj, Object value) {
          try {
            topWriter.invoke(obj, value);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to set on:" + targetClass + " :" + top + " =" + value, e);
          }
        }
      };
    } else {
      // read the prop
      Method topReader = pdTop.getReadMethod();
      if (topReader == null) {
        throw new IllegalArgumentException("No read access on:" + targetClass + " :" + top);
      }

      BiConsumer restConsumer = getWriter(topReader.getReturnType(), rest.get(0), rest.subList(1, rest.size()));
      return new BiConsumer() {
        @Override
        public void accept(Object obj, Object value) {
          try {
            Object nextObj = topReader.invoke(obj);
            restConsumer.accept(nextObj, value);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to set on:" + targetClass + " :" + top + " =" + value, e);
          }
        }
      };
    }
  }


}