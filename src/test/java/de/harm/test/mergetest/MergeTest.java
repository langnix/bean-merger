package de.harm.test.mergetest;

import lombok.Data;
import org.hamcrest.Matchers;
import org.junit.Test;

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
        private String name;
        private Integer number;
        private int cnt;
        private List<String> roles;
    }
    @Test
    public void simpleCopy() {
        MergeBean<SrcContainer,TargetContainer> cut=new MergeBean<>(SrcContainer.class,TargetContainer.class);

        cut.addMapping("name","name");
        SrcContainer c1=new SrcContainer();

        c1.setName("One");
        c1.setNumber(5);

        TargetContainer t1=new TargetContainer();
        cut.merge(c1,t1);
        assertThat(t1.getName()).isEqualTo(c1.getName());

    }
}
