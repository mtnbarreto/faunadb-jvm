package com.faunadb.client.query;

import com.faunadb.client.types.Ref;
import com.faunadb.client.types.Value;
import com.faunadb.client.types.Value.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import static java.lang.String.format;

/**
 * Helper methods for the FaunaDB query language. This class is intended to be statically imported into your code:
 * <p>
 * <p>{@code import static com.faunadb.client.query.Language.*;}</p>
 * <p>
 * <p>Each of these helper methods constructs a {@link Value}, which can then be composed with other helper methods.
 * <p>
 * <h3>Examples:</h3>
 * <pre>{@code Value existsValue = Exists(Ref("some/ref"));
 * Value createValue = Create(Ref("classes/some_class"), Quote(ObjectV("data", ObjectV("some", StringV("field")))));
 * }</pre>
 */
public final class Language {

  private Language() {
  }

  /**
   * Enumeration for time units. Used by <a href="https://faunadb.com/documentation/queries#time_functions">FaunaDB Time Functions</a>.
   */
  public static final class TimeUnit {
    private final String value;

    public final String getValue() {
      return value;
    }

    TimeUnit(String value) {
      this.value = value;
    }

    public final static TimeUnit SECOND = new TimeUnit("second");
    public final static TimeUnit MILLISECOND = new TimeUnit("millisecond");
    public final static TimeUnit MICROSECOND = new TimeUnit("microsecond");
    public final static TimeUnit NANOSECOND = new TimeUnit("nanosecond");
  }

  /**
   * Enumeration for event action types.
   */
  public static final class Action {
    private final String value;

    public final String getValue() {
      return value;
    }

    Action(String value) {
      this.value = value;
    }

    public final static Action CREATE = new Action("create");
    public final static Action DELETE = new Action("delete");
  }

  // Values

  /**
   * Creates a new Ref value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Ref(String ref) {
    return Expr.create(Ref.create(ref));
  }

  /**
   * Creates a new Ref value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Ref(Expr classRef, String id) {
    return Expr.create(Ref.create(format("%s/%s", classRef.tree().asRef().value(), id)));
  }

  /**
   * Creates a new String value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Value(String value) {
    return Expr.create(StringV.create(value));
  }

  /**
   * Creates a new Long value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Value(long value) {
    return Expr.create(LongV.create(value));
  }

  /**
   * Create aa new Double value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Value(double value) {
    return Expr.create(DoubleV.create(value));
  }

  /**
   * Creates a new Boolean value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Value(boolean value) {
    return Expr.create(BooleanV.create(value));
  }

  /**
   * Creates a new Timestamp value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Value(Instant value) {
    return Expr.create(TsV.create(value));
  }

  /**
   * Creates a new Date value.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Value(LocalDate value) {
    return Expr.create(DateV.create(value));
  }

  /**
   * Creates a null value
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Null() {
    return Expr.create(NullV.Null);
  }

  // Container Values

  /**
   * Creates a new Object expression, wrapping the provided dictionary of values.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Obj(ImmutableMap<String, Expr> values) {
    ImmutableMap.Builder<String, Value> innerValues = ImmutableMap.builder();
    for (Map.Entry<String, Expr> kv : values.entrySet())
      innerValues.put(kv.getKey(), kv.getValue().tree());

    return Expr.create(ObjectV.create(innerValues.build()));
  }

  /**
   * Creates a new empty Object expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#basic_forms">FaunaDB Basic Forms</a>
   */
  public static Expr Obj() {
    return Expr.create(ObjectV.empty());
  }

  /**
   * Creates a new Object expression containing the given entries.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Obj(String k1, Expr v1) {
    return Expr.create(ObjectV.create(k1, v1));
  }

  /**
   * Creates a new Object expression containing the given entries.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Obj(String k1, Expr v1, String k2, Expr v2) {
    return Expr.create(ObjectV.create(k1, v1, k2, v2));
  }

  /**
   * Creates a new Object expression containing the given entries.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Obj(String k1, Expr v1, String k2, Expr v2, String k3, Expr v3) {
    return Expr.create(ObjectV.create(k1, v1, k2, v2, k3, v3));
  }

  /**
   * Creates a new Object expression containing the given entries.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Obj(String k1, Expr v1, String k2, Expr v2, String k3, Expr v3, String k4, Expr v4) {
    return Expr.create(ObjectV.create(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  /**
   * Creates a new Object expression containing the given entries.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Obj(String k1, Expr v1, String k2, Expr v2, String k3, Expr v3, String k4, Expr v4, String k5, Expr v5) {
    return Expr.create(ObjectV.create(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
  }

  /**
   * Creates a new Array expression containing the provided list of values.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Arr(ImmutableList<Expr> values) {
    ImmutableList.Builder<Value> innerValues = ImmutableList.builder();
    for (Expr value : values)
      innerValues.add(value.tree());

    return Expr.create(ArrayV.create(innerValues.build()));
  }

  /**
   * Creates a new Array expression containing the given entries.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#values">FaunaDB Values</a></p>
   */
  public static Expr Arr(Expr... values) {
    return Arr(ImmutableList.copyOf(values));
  }

  // Basic Forms

  public static class LetBinding {
    private final ImmutableMap<String, Expr> bindings;

    LetBinding(Map<String, Expr> bindings) {
      this.bindings = ImmutableMap.copyOf(bindings);
    }

    public Expr in(Expr in) {
      return new Expr(ObjectV.create("let", Expr.escapedMap(Expr.upcast(bindings)), "in", Expr.create(in)));
    }
  }

  /**
   * Creates a new Let expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#basic_forms">FaunaDB Basic Forms</a>
   */
  public static LetBinding Let(String v1, Expr d1) {
    return new LetBinding(ImmutableMap.of(v1, d1));
  }

  public static LetBinding Let(String v1, Expr d1, String v2, Expr d2) {
    return new LetBinding(ImmutableMap.of(v1, d1, v2, d2));
  }

  public static LetBinding Let(String v1, Expr d1, String v2, Expr d2, String v3, Expr d3) {
    return new LetBinding(ImmutableMap.of(v1, d1, v2, d2, v3, d3));
  }

  public static LetBinding Let(String v1, Expr d1, String v2, Expr d2, String v3, Expr d3, String v4, Expr d4) {
    return new LetBinding(ImmutableMap.of(v1, d1, v2, d2, v3, d3, v4, d4));
  }

  public static LetBinding Let(String v1, Expr d1, String v2, Expr d2, String v3, Expr d3, String v4, Expr d4, String v5, Expr d5) {
    return new LetBinding(ImmutableMap.of(v1, d1, v2, d2, v3, d3, v4, d4, v5, d5));
  }

  public static LetBinding Let(Map<String, Expr> bindings) {
    return new LetBinding(bindings);
  }


  /**
   * Creates a new Var expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#basic_forms">FaunaDB Basic Forms</a>
   */
  public static Expr Var(String variable) {
    return Expr.fn("var", Value(variable));
  }

  /**
   * Creates a new If expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#basic_forms">FaunaDB Basic Forms</a>
   */
  public static Expr If(Expr condition, Expr thenExpr, Expr elseExpr) {
    return Expr.fn("if", condition, "then", thenExpr, "else", elseExpr);
  }

  /**
   * Creates a new Do expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#basic_forms">FaunaDB Basic Forms</a>
   */
  public static Expr Do(ImmutableList<Expr> expressions) {
    return Expr.fn("do", new Expr(ArrayV.create(Expr.upcast(expressions))));
  }

  /**
   * Creates a new Do expression with the given terms.
   */
  public static Expr Do(Expr... exprs) {
    return Do(ImmutableList.copyOf(exprs));
  }

  /**
   * Creates a new Lambda expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#basic_forms">FaunaDB Basic Forms</a>
   */
  public static Expr Lambda(String var, Expr expr) {
    return Expr.fn("lambda", Value(var), "expr", expr);
  }

  /**
   * Creates a new Map expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#collection_functions">FaunaDB Collection Functions</a>
   */
  public static Expr Map(Expr lambda, Expr collection) {
    return Expr.fn("map", lambda, "collection", collection);
  }

  /**
   * Creates a new Foreach expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#collection_functions">FaunaDB Collection Functions</a>
   */
  public static Expr Foreach(Expr lambda, Expr collection) {
    return Expr.fn("foreach", lambda, "collection", collection);
  }

  /**
   * Creates a new Filter expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#collection_functions">FaunaDB Collection Functions</a>
   */
  public static Expr Filter(Expr lambda, Expr collection) {
    return Expr.fn("filter", lambda, "collection", collection);
  }

  /**
   * Creates a new Take expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#collection_functions">FaunaDB Collection Functions</a>
   */
  public static Expr Take(Expr num, Expr collection) {
    return Expr.fn("take", num, "collection", collection);
  }

  /**
   * Creates a new Drop expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#collection_functions">FaunaDB Collection Functions</a>
   */
  public static Expr Drop(Expr num, Expr collection) {
    return Expr.fn("drop", num, "collection", collection);
  }

  /**
   * Creates a new Prepend expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#collection_functions">FaunaDB Collection Functions</a>
   */
  public static Expr Prepend(Expr elements, Expr collection) {
    return Expr.fn("prepend", elements, "collection", collection);
  }

  /**
   * Creates a new Append expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#collection_functions">FaunaDB Collection Functions</a>
   */
  public static Expr Append(Expr elements, Expr collection) {
    return Expr.fn("append", elements, "collection", collection);
  }

  /**
   * Creates a new Get expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#read_functions">FaunaDB Read Functions</a>
   */
  public static Expr Get(Expr ref) {
    return Expr.fn("get", ref);
  }

  /**
   * Creates a new Paginate expression builder.
   *
   * @see PaginateBuilder
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#read_functions">FaunaDB Read Functions</a></p>
   */
  public static PaginateBuilder Paginate(Expr resource) {
    return PaginateBuilder.create(resource);
  }

  /**
   * Creates a new Before cursor.
   *
   * @see Cursor
   */
  public static Cursor.Before Before(Expr value) {
    return Cursor.Before.create(value);
  }

  /**
   * Creates a new After cursor.
   *
   * @see Cursor
   */
  public static Cursor.After After(Expr value) {
    return Cursor.After.create(value);
  }


  /**
   * Creates a new Exists expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#read_functions">FaunaDB Read Functions</a></p>
   */
  public static Expr Exists(Expr ref) {
    return Expr.fn("exists", ref);
  }

  /**
   * Creates a new Exists expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#read_functions">FaunaDB Read Functions</a></p>
   */
  public static Expr Exists(Expr ref, Expr timestamp) {
    return Expr.fn("exists", ref, "ts", timestamp);
  }

  /**
   * Creates a new Count expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#read_functions">FaunaDB Read Functions</a></p>
   */
  public static Expr Count(Expr set) {
    return Expr.fn("count", set);
  }

  /**
   * Creates a new Count expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#read_functions">FaunaDB Read Functions</a></p>
   */
  public static Expr Count(Expr set, Expr countEvents) {
    return Expr.fn("count", set, "events", countEvents);
  }


  /**
   * Creates a new Create expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#write_functions">FaunaDB Write Functions</a></p>
   */
  public static Expr Create(Expr ref, Expr params) {
    return Expr.fn("create", ref, "params", params);
  }

  /**
   * Creates a new Update expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#write_functions">FaunaDB Write Functions</a></p>
   */
  public static Expr Update(Expr ref, Expr params) {
    return Expr.fn("update", ref, "params", params);
  }

  /**
   * Creates a new Replace expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#write_functions">FaunaDB Write Functions</a></p>
   */
  public static Expr Replace(Expr ref, Expr params) {
    return Expr.fn("replace", ref, "params", params);
  }

  /**
   * Creates a new Delete expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#write_functions">FaunaDB Write Functions</a></p>
   */
  public static Expr Delete(Expr ref) {
    return Expr.fn("delete", ref);
  }

  /**
   * Creates a new Insert expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#write_functions">FaunaDB Write Functions</a></p>
   */
  public static Expr Insert(Expr ref, Expr ts, Action action, Expr params) {
    return Expr.fn("insert", ref, "ts", ts, "action", Value(action.getValue()), "params", params);
  }

  /**
   * Creates a new Remove expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#write_functions">FaunaDB Write Functions</a></p>
   */
  public static Expr Remove(Expr ref, Expr ts, Action action) {
    return Expr.fn("remove", ref, "ts", ts, "action", Value(action.getValue()));
  }

  /**
   * Creates a new Match expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#sets">FaunaDB Set Functions</a></p>
   */
  public static Expr Match(Expr index) {
    return Expr.fn("match", index);
  }

  /**
   * Creates a new Match expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#sets">FaunaDB Set Functions</a></p>
   */
  public static Expr Match(Expr index, Expr term) {
    return Expr.fn("match", index, "terms", term);
  }

  /**
   * Creates a new Union expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#sets">FaunaDB Set Functions</a></p>
   */
  public static Expr Union(ImmutableList<Expr> sets) {
    return Expr.fn("union", Arr(sets));
  }

  /**
   * Creates a new Union expression operating on the given sets.
   */
  public static Expr Union(Expr... sets) {
    return Union(ImmutableList.copyOf(sets));
  }

  /**
   * Creates a new Intersection expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#sets">FaunaDB Set Functions</a></p>
   */
  public static Expr Intersection(ImmutableList<Expr> sets) {
    return Expr.fn("intersection", Arr(sets));
  }

  /**
   * Creates a new Intersection set expression operating on the given sets.
   */
  public static Expr Intersection(Expr... sets) {
    return Intersection(ImmutableList.copyOf(sets));
  }

  /**
   * Creates a new Difference set.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#sets">FaunaDB Set Functions</a></p>
   */
  public static Expr Difference(ImmutableList<Expr> sets) {
    return Expr.fn("difference", Arr(sets));
  }

  /**
   * Creates a new Difference set expression operating on the given sets.
   */
  public static Expr Difference(Expr... sets) {
    return Difference(ImmutableList.copyOf(sets));
  }

  /**
   * Creates a new Distinct set.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#sets">FaunaDB Set Functions</a></p>
   */
  public static Expr Distinct(ImmutableList<Expr> sets) {
    return Expr.fn("distinct", Arr(sets));
  }

  /**
   * Creates a new Distinct set expression operating on the given sets.
   */
  public static Expr Distinct(Expr... sets) {
    return Distinct(ImmutableList.copyOf(sets));
  }

  /**
   * Creates a new Join expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#sets">FaunaDB Set Functions</a></p>
   */
  public static Expr Join(Expr source, Expr target) {
    return Expr.fn("join", source, "with", target);
  }

  /**
   * Creates a new Login expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#auth_functions">FaunaDB Authentication Functions</a></p>
   */
  public static Expr Login(Expr ref, Expr params) {
    return Expr.fn("login", ref, "params", params);
  }

  /**
   * Creates a new Logout expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#auth_functions">FaunaDB Authentication Functions</a></p>
   */
  public static Expr Logout(Expr invalidateAll) {
    return Expr.fn("logout", invalidateAll);
  }

  /**
   * Creates a new Identify expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#auth_functions">FaunaDB Authentication Functions</a></p>
   */
  public static Expr Identify(Expr ref, Expr password) {
    return Expr.fn("identify", ref, "password", password);
  }

  /**
   * Creates a new Concat expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
   */
  public static Expr Concat(ImmutableList<Expr> terms) {
    return Expr.fn("concat", Arr(terms));
  }

  public static Expr Concat(ImmutableList<Expr> terms, Expr separator) {
    return Expr.fn("concat", Arr(terms), "separator", separator);
  }

  /**
   * Creates a new Concat expression operating on the given terms.
   */
  public static Expr Concat(Expr... terms) {
    return Concat(ImmutableList.copyOf(terms));
  }

  /**
   * Creates a new Casefolde expression operating on the given terms.
   */
  public static Expr Casefold(Expr str) {
    return Expr.fn("casefold", str);
  }

  /**
   * Creates a new Time expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#time_functions">FaunaDB Time and Date Functions</a></p>
   */
  public static Expr Time(Expr str) {
    return Expr.fn("time", str);
  }

  /**
   * Creates a new Epoch expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#time_functions">FaunaDB Time and Date Functions</a></p>
   */
  public static Expr Epoch(Expr num, TimeUnit unit) {
    return Expr.fn("epoch", num, "unit", Value(unit.getValue()));
  }

  /**
   * Creates a new Date expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#time_functions">FaunaDB Time and Date Functions</a></p>
   */
  public static Expr Date(Expr str) {
    return Expr.fn("date", str);
  }

  public static Expr NextId() {
    return Expr.fn("next_id", Null());
  }

  public static Expr Equals(ImmutableList<Expr> values) {
    return Expr.fn("equals", Arr(values));
  }

  public static Expr Equals(Expr... values) {
    return Equals(ImmutableList.copyOf(values));
  }

  /**
   * Creates a new Contains expression.
   * <p>
   * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
   */
  public static Expr Contains(ImmutableList<Path> path, Expr in) {
    ImmutableList.Builder<Expr> pathValueBuilder = ImmutableList.builder();
    for (Path term : path) {
      pathValueBuilder.add(Expr.create(term.value()));
    }

    return Expr.fn("contains", Arr(pathValueBuilder.build()), "in", in);
  }

  /**
   * Helper for constructing a Path list with the given path terms.
   *
   * @see Path
   */
  public static ImmutableList<Path> Path(Path... terms) {
    return ImmutableList.copyOf(terms);
  }

  // /**
  //  * Creates a new Select expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Select(ImmutableList<Path> path, Value from) {
  //   ImmutableList.Builder<Value> pathValueBuilder = ImmutableList.builder();
  //   for (Path term : path) {
  //     pathValueBuilder.add(term.value());
  //   }

  //   return ObjectV("select", ArrayV(pathValueBuilder.build()), "from", from);
  // }


  // /**
  //  * Creates a new Add expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Add(ImmutableList<Value> terms) {
  //   return ObjectV("add", ArrayV(terms));
  // }

  // /**
  //  * Creates a new Add expression operating on the given terms.
  //  */
  // public static Value Add(Value... terms) {
  //   return Add(ImmutableList.copyOf(terms));
  // }

  // /**
  //  * Creates a new Subtract expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Subtract(ImmutableList<Value> terms) {
  //   return ObjectV("subtract", ArrayV(terms));
  // }

  // /**
  //  * Creates a new Subtract expression operating on the given terms.
  //  */
  // public static Value Subtract(Value... terms) {
  //   return Subtract(ImmutableList.copyOf(terms));
  // }
  // /**
  //  * Creates a new Divide expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Divide(ImmutableList<Value> terms) {
  //   return ObjectV("divide", ArrayV(terms));
  // }

  // /**
  //  * Creates a new Divide expression operating on the given terms.
  //  */
  // public static Value Divide(Value... terms) {
  //   return Divide(ImmutableList.copyOf(terms));
  // }

  // /**
  //  * Creates a new Multiply expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Multiply(ImmutableList<Value> terms) {
  //   return ObjectV("multiply", ArrayV(terms));
  // }

  // /**
  //  * Creates a new Multiply expression operating on the given terms.
  //  */
  // public static Value Multiply(Value... terms) {
  //   return Multiply(ImmutableList.copyOf(terms));
  // }

  // /**
  //  * Creates a new Modulo expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Modulo(ImmutableList<Value> terms) {
  //   return ObjectV("modulo", ArrayV(terms));
  // }

  // /**
  //  * Creates a new Modulo expression operating on the given terms.
  //  */
  // public static Value Modulo(Value... terms) {
  //   return Modulo(ImmutableList.copyOf(terms));
  // }

  // /**
  //  * Creates a new And expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value And(ImmutableList<Value> terms) {
  //   return ObjectV("and", ArrayV(terms));
  // }

  // /**
  //  * Creates a new And expression operating on the given terms.
  //  */
  // public static Value And(Value... terms) {
  //   return And(ImmutableList.copyOf(terms));
  // }

  // /**
  //  * Creates a new Or expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Or(ImmutableList<Value> terms) {
  //   return ObjectV("or", ArrayV(terms));
  // }

  // /**
  //  * Creates a new Or expression operating on the given terms.
  //  */
  // public static Value Or(Value... terms) {
  //   return Or(ImmutableList.copyOf(terms));
  // }

  // /**
  //  * Creates a new Not expression.
  //  *
  //  * <p><b>Reference</b>: <a href="https://faunadb.com/documentation/queries#misc_functions">FaunaDB Miscellaneous Functions</a></p>
  //  */
  // public static Value Not(Value term) {
  //   return ObjectV("not", term);
  // }


}
