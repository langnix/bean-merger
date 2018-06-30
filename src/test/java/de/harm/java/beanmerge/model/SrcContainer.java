package de.harm.java.beanmerge.model;

import lombok.Data;

import java.util.List;

@Data
public class SrcContainer {

  private String name;
  private Integer number;

  private List<String> roles;
}
