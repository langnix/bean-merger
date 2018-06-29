package de.harm.test.mergetest;

import static org.junit.Assert.assertThat;

import de.harm.test.mergetest.model.SrcContainer;
import de.harm.test.mergetest.model.SrcNested;
import java.util.function.Function;
import org.hamcrest.Matchers;
import org.junit.Test;

public class AccessFunctionBuilderTest {

  @Test
  public void simpleReaderStep() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    Function rd = cut.getReader(SrcContainer.class, "name");
    SrcContainer obj = new SrcContainer();
    assertThat(rd.apply(obj), Matchers.nullValue());

    obj.setName("one");
    assertThat(rd.apply(obj), Matchers.is("one"));

  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownReaderProperty() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    cut.getReader(SrcContainer.class, "unknown");
  }


  @Test
  public void simpleNestedReader() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    Function rd = cut.getReader(SrcNested.class, "srcContainer.name");
    SrcNested obj = new SrcNested();

    assertThat("missing container on path", rd.apply(obj), Matchers.nullValue());

    obj.setSrcContainer(new SrcContainer());
    assertThat("null on container", rd.apply(obj), Matchers.nullValue());

    obj.getSrcContainer().setName("hansi");
    assertThat(rd.apply(obj), Matchers.is("hansi"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownNestedReaderProperty() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    cut.getReader(SrcNested.class, "srcContainer.unknown");
  }


}