package com.faunadb.client.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;

/**
 * A FaunaDB set literal.
 *
 * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values-special_types">FaunaDB Special Types</a></p>
 *
 */
@JsonDeserialize(using=Codec.SetRefDeserializer.class)
public class SetRef {
  public static SetRef create(ImmutableMap<String, Value> parameters) {
    return new SetRef(parameters);
  }

  private final ImmutableMap<String, Value> parameters;

  SetRef(ImmutableMap<String, Value> parameters) {
    this.parameters = parameters;
  }

  public ImmutableMap<String, Value> parameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return "SetRef(" + parameters().toString() + ")";
  }
}
