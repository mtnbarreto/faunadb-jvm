package com.faunadb.client.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;

/**
 * A FaunaDB set literal.
 *
 * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values-special_types">FaunaDB Special Types</a></p>
 *
 */
@JsonDeserialize(using=Codec.SetRefDeserializer.class)
public class SetRef extends Value.ScalarValue {
  public static SetRef create(ImmutableMap<String, Value> parameters) {
    return new SetRef(parameters);
  }

  private final ImmutableMap<String, Value> parameters;

  SetRef(ImmutableMap<String, Value> parameters) {
    this.parameters = parameters;
  }

  @Override
  public ImmutableMap<String, Value> asObject() {
    return parameters;
  }

  @Override
  public Value get(String key) {
    return parameters.get(key);
  }

  @Override
  public String toString() {
    return "SetRef(" + parameters.toString() + ")";
  }
}
