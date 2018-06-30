package de.harm.java.beanmerge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class AccessFunctionBuilder {

  private List<String> convert2PathElements(String path) {
    return Arrays.asList(StringUtils.tokenizeToStringArray(path, "."));
  }

  public AccessReader getReader(Class srcClass, String path) {
    return getReader(srcClass, convert2PathElements(path));

  }

  private AccessReader getReader(Class srcClass, List<String> path) {
    return getReader(srcClass, path.get(0), path.subList(1, path.size()));

  }

  private AccessReader getReader(Class srcClass, String top, List<String> rest) {
    PropertyDescriptor pdTop = BeanUtils.getPropertyDescriptor(srcClass, top);
    if (pdTop == null) {
      throw new IllegalArgumentException("Unknown property on:" + srcClass + " :" + top);
    }
    Method topReader = pdTop.getReadMethod();
    if (rest.isEmpty()) {
      return new AccessReader((obj) -> {
        try {
          return topReader.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new IllegalStateException("Unable to read on:" + srcClass + " :" + top);
        }
      },
          topReader.getReturnType());
    }
    AccessReader remFct = getReader(topReader.getReturnType(), rest.get(0), rest.subList(1, rest.size()));

    return new AccessReader(new Function() {
      @Override
      public Object apply(Object c) {
        try {
          Object next = topReader.invoke(c);
          if (next == null) {
            return null;
          } else {
            return remFct.getReader().apply(next);
          }
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new IllegalStateException("Unable to read on:" + srcClass + " :" + top);
        }
      }
    }, remFct.getValueClass());


  }

  public AccessWriter getWriter(Class targetClass, String path) {
    return getWriter(targetClass, convert2PathElements(path));
  }

  private AccessWriter getWriter(Class targetClass, List<String> path) {
    return getWriter(targetClass, path.get(0), path.subList(1, path.size()));
  }

  private AccessWriter getWriter(Class targetClass, String top, List<String> rest) {
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
      return new AccessWriter(

          new BiConsumer() {
            @Override
            public void accept(Object obj, Object value) {
              try {
                topWriter.invoke(obj, value);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Unable to set on:" + targetClass + " :" + top + " =" + value, e);
              }
            }
          },
          topWriter.getParameters()[0].getType())
          ;
    } else {
      // read the prop
      Method topReader = pdTop.getReadMethod();
      if (topReader == null) {
        throw new IllegalArgumentException("No read access on:" + targetClass + " :" + top);
      }

      Method topWriter = pdTop.getWriteMethod();
      if (topWriter == null) {
        throw new IllegalArgumentException("No writer access on:" + targetClass + " :" + top);
      }
      Constructor<?> topConstructor;
      try {
        topConstructor = topReader.getReturnType().getConstructor();
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException(
            "Missing (default) constructor off:" + topReader.getReturnType() + " used on:" + targetClass + " :" + top);
      }
      AccessWriter restConsumer = getWriter(topReader.getReturnType(), rest.get(0), rest.subList(1, rest.size()));

      return new AccessWriter(new BiConsumer() {
        @Override
        public void accept(Object obj, Object value) {
          try {
            Object nextObj = topReader.invoke(obj);
            if (nextObj == null) {

              try {
                log.trace("Create intermediate container:{} {}", targetClass, top);
                nextObj = topConstructor.newInstance();
                topWriter.invoke(obj, nextObj);

              } catch (InstantiationException e) {
                throw new IllegalStateException("Unable to create instance of:" + topConstructor.getDeclaringClass(),
                    e);
              }
            }

            restConsumer.accept(nextObj, value);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to set on:" + targetClass + " :" + top + " =" + value, e);
          }
        }
      },
          restConsumer.getValueClass());
    }
  }


}
