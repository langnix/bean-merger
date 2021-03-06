package de.harm.java.beanmerge;

import de.harm.java.beanmerge.model.SrcContainer;
import de.harm.java.beanmerge.model.SrcNested;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class AccessFunctionBuilderTest {

  @Test
  public void simpleReaderStep() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    AccessReader rd = cut.getReader(SrcContainer.class, "name");
    SrcContainer obj = new SrcContainer();
    assertThat(rd.apply(obj), Matchers.nullValue());

    obj.setName("one");
    assertThat(rd.apply(obj), Matchers.is("one"));
    assertThat("valueclass is the leaf class..", rd.getValueClass().toString(), Matchers.is(String.class.toString()));

  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownReaderProperty() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    cut.getReader(SrcContainer.class, "unknown");
  }


  @Test
  public void simpleNestedReader() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    AccessReader rd = cut.getReader(SrcNested.class, "srcContainer.name");
    SrcNested obj = new SrcNested();

    assertThat("missing container on path", rd.apply(obj), Matchers.nullValue());

    obj.setSrcContainer(new SrcContainer());
    assertThat("null on container", rd.apply(obj), Matchers.nullValue());

    obj.getSrcContainer().setName("hansi");
    assertThat(rd.apply(obj), Matchers.is("hansi"));
    assertThat("valueclass is the leaf class..", rd.getValueClass().toString(), Matchers.is(String.class.toString()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownNestedReaderProperty() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    cut.getReader(SrcNested.class, "srcContainer.unknown");
  }

  @Test
  public void simpleWrite() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    AccessWriter wr = cut.getWriter(SrcContainer.class, "name");
    SrcContainer obj = new SrcContainer();
    wr.accept(obj, "maxi");
    assertThat(obj.getName(), Matchers.is("maxi"));
    assertThat("valueclass is the leaf class..", wr.getValueClass().toString(), Matchers.is(String.class.toString()));

  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownNestedWriterProperty() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    cut.getWriter(SrcNested.class, "srcContainer.unknown");
  }


  @Test
  public void simpleWriteNested() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    AccessWriter wr = cut.getWriter(SrcNested.class, "srcContainer.name");
    SrcNested obj = new SrcNested();
    obj.setSrcContainer(new SrcContainer());
    obj.getSrcContainer().setNumber(42);
    obj.getSrcContainer().setName("jsfgjsdfgh");
    wr.accept(obj, "maxi");
    assertThat("override in existing conatiner", obj.getSrcContainer().getName(), Matchers.is("maxi"));
    assertThat("keep in existing conatiner", obj.getSrcContainer().getNumber(), Matchers.is(42));
    assertThat("valueclass is the leaf class..", wr.getValueClass().toString(), Matchers.is(String.class.toString()));

  }


  @Test
  public void writeNestedWithoutIntermediate() {
    AccessFunctionBuilder cut = new AccessFunctionBuilder();
    AccessWriter wr = cut.getWriter(SrcNested.class, "srcContainer.name");
    SrcNested obj = new SrcNested();
    obj.setSrcContainer(null); // NOT THERE!!!

    wr.accept(obj, "maxi");
    assertThat("container creataed", obj.getSrcContainer(), Matchers.notNullValue());
    assertThat("set in container ", obj.getSrcContainer().getName(), Matchers.is("maxi"));
    assertThat("valueclass is the leaf class..", wr.getValueClass().toString(), Matchers.is(String.class.toString()));

  }
}