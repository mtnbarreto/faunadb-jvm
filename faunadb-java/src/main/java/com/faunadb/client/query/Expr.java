package com.faunadb.client.query;

import com.fasterxml.jackson.annotation.JsonValue;
import com.faunadb.client.types.Value;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class Expr extends Value.ConcreteValue {

  private final Value tree;

  Expr(Value tree) {
    this.tree = tree;
  }

  public static Expr fn(String k1, Expr p1) {
    return new Expr(ObjectV.create(k1, p1));
  }

  public static Expr fn(String k1, Expr p1, String k2, Expr p2) {
    return new Expr(ObjectV.create(k1, p1, k2, p2));
  }

  public static Expr fn(String k1, Expr p1, String k2, Expr p2, String k3, Expr p3) {
    return new Expr(ObjectV.create(k1, p1, k2, p2, k3, p3));
  }

  public static Expr fn(String k1, Expr p1, String k2, Expr p2, String k3, Expr p3, String k4, Expr p4) {
    return new Expr(ObjectV.create(k1, p1, k2, p2, k3, p3, k4, p4));
  }

  public static Expr create(ScalarValue expr) {
    return (expr == null) ? new Expr(Value.NullV.Null) : new Expr(expr);
  }

  public static Expr create(Value expr) {
    if (expr == null) {
      return new Expr(Value.NullV.Null);

    } else if (expr instanceof Expr) {
      return (Expr) expr;

    } else if (expr instanceof Value.ScalarValue) {
      return new Expr(expr);

    } else if (expr instanceof Value.ArrayV) {
      return new Expr(escapedList(((Value.ArrayV) expr).values()));

    } else if (expr instanceof Value.ObjectV) {
      return new Expr(ObjectV.create("object", escapedMap(((Value.ObjectV) expr).values())));

    } else if (expr instanceof Value.NullV) {
      return new Expr(expr);

    } else {
      throw new AssertionError(String.format("Unknown Value type %s", expr));
    }
  }

  static ArrayV escapedList(ImmutableList<Value> l) {
    ImmutableList.Builder<Expr> exprs = ImmutableList.builder();
    for (Value v : l)
      exprs.add(Expr.create(v));

    return new ArrayV(upcast(exprs.build()));
  }

  static ObjectV escapedMap(ImmutableMap<String, Value> m) {
    ImmutableMap.Builder<String, Expr> exprs = ImmutableMap.builder();
    for (Map.Entry<String, Value> p : m.entrySet())
      exprs.put(p.getKey(), create(p.getValue()));

    return new ObjectV(upcast(exprs.build()));
  }

  static ImmutableList<Expr> upcast(ImmutableList<Expr> m) {
    return cast(m);
  }

  static ImmutableMap<String, Value> upcast(ImmutableMap<String, Expr> m) {
    return cast(m);
  }

  @SuppressWarnings("unchecked")
  private static <T> T cast(final Object o) {
    return (T) o;
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null &&
      (obj instanceof Expr) &&
      tree.equals(((Expr) obj).tree());
  }

  @Override
  public int hashCode() {
    return tree.hashCode();
  }

  @JsonValue
  public Value tree() {
    return tree;
  }
}
