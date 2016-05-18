package com.faunadb.client.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.NullNode;
import com.faunadb.client.query.Expr;
import com.faunadb.client.query.Language;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Represents any scalar or non-scalar value in the FaunaDB query language. FaunaDB value types consist of
 * all of the JSON value types, as well as the FaunaDB-specific types, {@link Ref} and {@link Set}.
 *
 * <p>Scalar values are {@link LongV}, {@link StringV}, {@link DoubleV}, {@link BooleanV}, {@link NullV},
 * {@link Ref}, and {@link Set}.
 *
 * <p>Non-scalar values are {@link ObjectV} and {@link ArrayV}.</p>
 *
 * <p>This interface itself does not have any directly accessible data. It must first be coerced into a type before
 * its data can be accessed.
 *
 * <p>Coercion functions will return null if this node cannot be transformed into the requested type.
 *
 * <p><b>Example</b>: Consider the {@code Value node} modeling the root of the tree:</p>
 * <pre>
 * {
 *   "ref": { "@ref": "some/ref" },
 *   "data": { "someKey": "string1", "someKey2": 123 }
 * }</pre>
 *
 * <p>The result tree can be accessed using:</p>
 *
 * <pre>
 *   node.get("ref").asRef(); // {@link Ref}("some/ref")
 *   node.get("data").get("someKey").asString() // "string1"
 * </pre>
 *
 * <p><i>Reference</i>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Value Types</a></p>
 */
public interface Value {
  /**
   * Coerces this node into a {@link String}.
   * @return the string value of this node, or null.
   */
  String asString();

  /**
   * Coerces this node into a {@link Boolean}.
   * @return the boolean value of this node, or null.
   */
  Boolean asBoolean();

  /**
   * Coerces this node into a {@link Long}.
   * @return the long value of this node, or null.
   */
  Long asLong();

  /**
   * Coerces this node into a {@link Double}.
   * @return the double value of this node, or null.
   */
  Double asDouble();

  /**
   * Coerces this node into a {@link Instant}, if it is a {@link TsV}.
   * @return the instant value of this node, or null.
   */
  Instant asTs();

  /**
   * Coerces this node into a {@link LocalDate}, if it is a {@link DateV}.
   * @return the date value of this node, or null.
   */
  LocalDate asDate();

  /**
   * Coerces this node into an ordered list of nodes.
   * @return an ordered list of response nodes, or null.
   */
  ImmutableList<Value> asArray();

  /**
   * Coerces this node into a dictionary of nodes.
   * @return a dictionary of nodes, or null.
   */
  ImmutableMap<String, Value> asObject();

  /**
   * Coerces this node into a {@link Ref}.
   * @return a Ref, or null.
   */
  Ref asRef();

  /**
   * Coerces this node into a {@link SetRef}.
   * @return a SetRef, or null.
   */
  SetRef asSetRef();

  Value get(String key);
  Value get(int index);

  abstract class ConcreteValue implements Value {
    @Override
    public String asString() {
      return null;
    }

    @Override
    public Boolean asBoolean() {
      return null;
    }

    @Override
    public Long asLong() {
      return null;
    }

    @Override
    public Double asDouble() {
      return null;
    }

    @Override
    public Instant asTs() {
      return null;
    }

    @Override
    public LocalDate asDate() {
      return null;
    }

    @Override
    public ImmutableList<Value> asArray() {
      return null;
    }

    @Override
    public ImmutableMap<String, Value> asObject() {
      return null;
    }

    @Override
    public Ref asRef() {
      return null;
    }

    @Override
    public Value get(int index) {
      return null;
    }

    @Override
    public Value get(String key) {
      return null;
    }

    @Override
    public SetRef asSetRef() {
      return null;
    }
  }

  abstract class ScalarValue extends ConcreteValue {}

  /**
   * Represents an Object value in the FaunaDB query language. Objects are polymorphic dictionaries.
   *
   * @see Language#ObjectV
   */
  @JsonDeserialize(using=Codec.ObjectDeserializer.class)
  final class ObjectV extends ConcreteValue {
    private final ImmutableMap<String, Value> values;

    @Override
    public ImmutableMap<String, Value> asObject() {
      return values;
    }

    /**
     * Constructs an empty object value.
     * @see Language#ObjectV()
     */
    public static ObjectV empty() {
      return new ObjectV(ImmutableMap.<String, Value>of());
    }

    /**
     * Constructs an object value containing the specified key/value pair.
     * @see Language#ObjectV(String, Value)
     */
    public static ObjectV create(String k1, Value v1) {
      return new ObjectV(ImmutableMap.of(k1, v1));
    }

    /**
     * Constructs an object value containing the specified key/value pairs.
     * @see Language#ObjectV(String, Value, String, Value)
     */
    public static ObjectV create(String k1, Value v1, String k2, Value v2) {
      return new ObjectV(ImmutableMap.of(k1, v1, k2, v2));
    }

    /**
     * Constructs an object value containing the specified key/value pairs.
     * @see Language#ObjectV(String, Value, String, Value, String, Value)
     */
    public static ObjectV create(String k1, Value v1, String k2, Value v2, String k3, Value v3) {
      return new ObjectV(ImmutableMap.of(k1, v1, k2, v2, k3, v3));
    }

    /**
     * Constructs an object value containing the specified key/value pairs.
     * @see Language#ObjectV(String, Value, String, Value, String, Value, String, Value)
     */
    public static ObjectV create(String k1, Value v1, String k2, Value v2, String k3, Value v3, String k4, Value v4) {
      return new ObjectV(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4));
    }

    /**
     * Constructs an object value containing the specified key/value pairs.
     * @see Language#ObjectV(String, Value, String, Value, String, Value, String, Value, String, Value)
     */
    public static ObjectV create(String k1, Value v1, String k2, Value v2, String k3, Value v3, String k4, Value v4, String k5, Value v5) {
      return new ObjectV(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
    }

    /**
     * Constructs an object value wrapping the given dictionary.
     * @see Language#ObjectV(ImmutableMap)
     */
    public static ObjectV create(Map<String, Value> values) {
      return new ObjectV(ImmutableMap.copyOf(values));
    }

    public ObjectV(ImmutableMap<String, Value> values) {
      this.values = values;
    }

    @JsonValue
    public ImmutableMap<String, Value> values() {
      return values;
    }

    @Override
    public Value get(String key) {
      return values.get(key);
    }

    @Override
    public int hashCode() {
      return values.hashCode();
    }

    @Override
    public String toString() {
      return "ObjectV(" + values() + ")";
    }
  }

  /**
   * Represents an array value in the FaunaDB query language. Arrays are polymorphic ordered lists of other values.
   */
  final class ArrayV extends ConcreteValue {
    private final ImmutableList<Expr> values;

    @Override
    public ImmutableList<Value> asArray() {
      ImmutableList.Builder<Value> result = ImmutableList.builder();
      for (Expr expr : values)
        result.add(expr.tree());

      return result.build();
    }

    /**
     * Returns an empty array value.
     *
     * @see Language#ArrayV()
     */
    public static ArrayV empty() {
      return new ArrayV(ImmutableList.<Expr>of());
    }

    /**
     * Constructs an array value containing the specified value.
     *
     * @see Language#ArrayV(Value...)
     */
    public static ArrayV create(Expr... values) {
      return new ArrayV(ImmutableList.copyOf(values));
    }

    /**
     * Constructs an array value wrapping the provided list of values.
     *
     * @see Language#ArrayV(ImmutableList)
     */
    public static ArrayV create(List<Expr> values) {
      return new ArrayV(values);
    }

    public ArrayV(List<Expr> values) {
      this.values = ImmutableList.copyOf(values);
    }

    @Override
    public Value get(int index) {
      return values.get(index);
    }

    @JsonValue
    public ImmutableList<Value> values() {
      return asArray();
    }

    @Override
    public int hashCode() {
      return values.hashCode();
    }
  }

  /**
   * Represents a Boolean value in the FaunaDB query language.
   *
   * @see Language#BooleanV(boolean)
   */
  final class BooleanV extends ScalarValue {
    private final Boolean value;

    public final static BooleanV True = new BooleanV(true);
    public final static BooleanV False = new BooleanV(false);

    public static BooleanV create(boolean value) {
      return value ? True : False;
    }

    @Override
    public boolean equals(Object obj) {
      return super.equals(obj);
    }

    @Override
    public Boolean asBoolean() {
      return value;
    }

    BooleanV(boolean value) {
      this.value = value;
    }

    @JsonValue
    public boolean value() {
      return value;
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }
  }

  /**
   * Represents a Double value in the FaunadB query language.
   *
   * @see Language#DoubleV(double)
   */
  final class DoubleV extends ScalarValue {
    private final Double value;

    public static DoubleV create(double value) {
      return new DoubleV(value);
    }

    @Override
    public Double asDouble() {
      return value;
    }

    DoubleV(double value) {
      this.value = value;
    }

    @JsonValue
    public double value() {
      return value;
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }
  }

  final class LongV extends ScalarValue {
    private final Long value;

    public static LongV create(long value) {
      return new LongV(value);
    }

    @Override
    public Long asLong() {
      return value;
    }

    LongV(long value) {
      this.value = value;
    }

    @JsonValue
    public long value() {
      return value;
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }
  }

  final class StringV extends ScalarValue {
    private final String value;

    public static StringV create(String value) {
      return new StringV(value);
    }

    @Override
    public String asString() {
      return value;
    }

    StringV(String value) {
      this.value = value;
    }

    @JsonValue
    public String value() {
      return value;
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }
  }

  final class NullV extends ConcreteValue {
    public static final NullV Null = new NullV();

    NullV() { }

    @Override
    public String toString() {
      return "null";
    }

    @JsonValue
    public NullNode value() {
      return NullNode.getInstance();
    }
  }

  final class TsV extends ScalarValue {
    private final Instant value;

    public static TsV create(Instant value) {
      return new TsV(value);
    }

    @JsonProperty("@ts")
    private String strValue() {
      return value.toString();
    }

    @JsonCreator
    TsV(@JsonProperty("@ts") String value) {
      this.value = ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
    }

    TsV(Instant ts) {
      this.value = ts;
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public Instant asTs() {
      return value;
    }
  }

  final class DateV extends ScalarValue {
    private final LocalDate value;

    public static DateV create(LocalDate value) {
      return new DateV(value);
    }

    DateV(LocalDate value) {
      this.value = value;
    }

    DateV(@JsonProperty("@date") String value) {
      this.value = LocalDate.parse(value);
    }

    @JsonProperty("@date")
    private String strValue() {
      return value.toString();
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }

    @Override
    public LocalDate asDate() {
      return value;
    }
  }
}
