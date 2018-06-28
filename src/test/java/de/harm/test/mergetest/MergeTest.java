package de.harm.test.mergetest;

import lombok.Data;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;

public class MergeTest {
    @Data
    public static class SrcContainer {
        private String name;
        private Integer number;

        private List<String> roles;
    }

    @Data
    public static class TargetContainer {
        private String another;
        private Integer count;
        private int cnt;
        private List<String> bones;
    }

    @Data
    public static class SrcNested {
        private String fire;

        private SrcContainer srcContainer;
    }
    @Data
    public static class TargetNested {
        private String one;

        private TargetContainer targetContainer;
    }
    @Test
    public void simpleCopy() {
        MergeBean<SrcContainer,TargetContainer> cut=new MergeBean<>(SrcContainer.class,TargetContainer.class);

        cut.addMapping("name","another");
        cut.addMapping("roles","bones");
        SrcContainer c1=new SrcContainer();

        c1.setName("One");
        c1.setNumber(5);
        c1.setRoles(Collections.singletonList("Hans"));
        TargetContainer t1=new TargetContainer();
        t1.setAnother("kjasdkajshdkajs");
        cut.merge(c1,t1);
        assertThat(t1.getAnother()).isEqualTo(c1.getName());
        assertThat(t1.getCount()).isNull();
        assertThat(t1.getBones()).isEqualTo(c1.getRoles());
    }

    @Test
    public void mssingNestedOnSource() {
        MergeBean<SrcNested, TargetNested> cut = new MergeBean<>(SrcNested.class, TargetNested.class);
        cut.addMapping("fire","targetContainer.another");
        cut.addMapping("srcContainer.name","one");
        cut.addMapping("srcContainer.roles","targetContainer.bones");

        SrcNested src=new SrcNested();
        src.setFire("fire");
        src.setSrcContainer(null);

        TargetNested target=new TargetNested();
        target.setOne("sfasfasf");

        cut.merge(src,target);

        assertThat(target.getOne()).isNull(); // null on path leads to null
        assertThat(target.getTargetContainer().getAnother()).isEqualTo(src.getFire());



    }

    @Test
    public void nested2nested() {
        MergeBean<SrcNested, TargetNested> cut = new MergeBean<>(SrcNested.class, TargetNested.class);
        cut.addMapping("fire","targetContainer.another");
        cut.addMapping("srcContainer.name","one");
        cut.addMapping("srcContainer.roles","targetContainer.bones");

        SrcNested src=new SrcNested();
        src.setFire("fire");
        SrcContainer srcContainer = new SrcContainer();
        srcContainer.setName("national");
        srcContainer.setRoles(Collections.singletonList("Rifle"));
        src.setSrcContainer(srcContainer);

        TargetNested target=new TargetNested();

        cut.merge(src,target);
        assertThat(target.getTargetContainer().getAnother()).isEqualTo(src.getFire());
        assertThat(target.getOne()).isEqualTo(src.getSrcContainer().getName());
        assertThat(target.getTargetContainer().getBones()).isEqualTo(src.getSrcContainer().getRoles());
    }
}
