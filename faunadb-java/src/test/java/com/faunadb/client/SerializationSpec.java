package com.faunadb.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faunadb.client.query.Expr;
import com.faunadb.client.query.Path;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;

import static com.faunadb.client.query.Language.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SerializationSpec {

  private ObjectMapper json;

  @Before
  public void setUp() {
    json = new ObjectMapper();
  }

  @Test
  public void shouldSerializeLiteralValues() throws Exception {
    assertJson(Value(Long.MAX_VALUE), String.valueOf(Long.MAX_VALUE));
    assertJson(Value("a string"), "\"a string\"");
    assertJson(Value(10), "10");
    assertJson(Value(1.0), "1.0");
    assertJson(Value(true), "true");
    assertJson(Value(false), "false");
    assertJson(Null(), "null");
  }

  @Test
  public void shouldSerializeAnArray() throws Exception {
    assertJson(
      Arr(
        Value("a string"),
        Value(10)
      ), "[\"a string\",10]");

    assertJson(
      Arr(ImmutableList.of(
        Value("other string"),
        Value(42)
      )), "[\"other string\",42]");
  }

  @Test
  public void shouldSerializeAnObject() throws Exception {
    assertJson(Obj(), "{\"object\":{}}");

    assertJson(
      Obj("k1", Value("v1")),
      "{\"object\":{\"k1\":\"v1\"}}");

    assertJson(Obj("k1", Value("v1"), "k2", Value("v2")),
      "{\"object\":{\"k1\":\"v1\",\"k2\":\"v2\"}}");

    assertJson(
      Obj(
        "k1", Value("v1"),
        "k2", Value("v2"),
        "k3", Value("v3")
      ),
      "{\"object\":{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\"}}");

    assertJson(
      Obj(
        "k1", Value("v1"),
        "k2", Value("v2"),
        "k3", Value("v3"),
        "k4", Value("v4")
      ),
      "{\"object\":{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\",\"k4\":\"v4\"}}");

    assertJson(
      Obj(
        "k1", Value("v1"),
        "k2", Value("v2"),
        "k3", Value("v3"),
        "k4", Value("v4"),
        "k5", Value("v5")
      ),
      "{\"object\":{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\",\"k4\":\"v4\",\"k5\":\"v5\"}}");

    assertJson(
      Obj(ImmutableMap.of(
        "k1", Value("v1"),
        "k2", Value("v2"))
      ),
      "{\"object\":{\"k1\":\"v1\",\"k2\":\"v2\"}}");
  }

  @Test
  public void shouldSerializeRef() throws Exception {
    assertJson(Ref("classes"), "{\"@ref\":\"classes\"}");
    assertJson(Ref(Ref("classes/people"), "id1"), "{\"@ref\":\"classes/people/id1\"}");
  }

  @Test
  public void shouldSerializeInstantValue() throws Exception {
    assertJson(Value(Instant.EPOCH), "{\"@ts\":\"1970-01-01T00:00:00Z\"}");
  }

  @Test
  public void shouldSerializeDateValue() throws Exception {
    assertJson(Value(LocalDate.of(2015, 1, 15)), "{\"@date\":\"2015-01-15\"}");
  }

  @Test
  public void shouldSerializeLet() throws Exception {
    assertJson(
      Let(
        "v1", Value("x1")
      ).in(
        Value("x")
      ), "{\"let\":{\"v1\":\"x1\"},\"in\":\"x\"}");

    assertJson(
      Let(
        "v1", Value("x1"),
        "v2", Value("x2")
      ).in(
        Value("x")
      ), "{\"let\":{\"v1\":\"x1\",\"v2\":\"x2\"},\"in\":\"x\"}");

    assertJson(
      Let(
        "v1", Value("x1"),
        "v2", Value("x2"),
        "v3", Value("x3")
      ).in(
        Value("x")
      ), "{\"let\":{\"v1\":\"x1\",\"v2\":\"x2\",\"v3\":\"x3\"},\"in\":\"x\"}");

    assertJson(
      Let(
        "v1", Value("x1"),
        "v2", Value("x2"),
        "v3", Value("x3"),
        "v4", Value("x4")
      ).in(
        Value("x")
      ), "{\"let\":{\"v1\":\"x1\",\"v2\":\"x2\",\"v3\":\"x3\",\"v4\":\"x4\"},\"in\":\"x\"}");

    assertJson(
      Let(
        "v1", Value("x1"),
        "v2", Value("x2"),
        "v3", Value("x3"),
        "v4", Value("x4"),
        "v5", Value("x5")
      ).in(
        Value("x")
      ), "{\"let\":{\"v1\":\"x1\",\"v2\":\"x2\",\"v3\":\"x3\",\"v4\":\"x4\",\"v5\":\"x5\"},\"in\":\"x\"}");

    assertJson(
      Let(ImmutableMap.of(
        "v1", Value("x1"),
        "v2", Value("x2")
        )
      ).in(
        Value("x")
      ), "{\"let\":{\"v1\":\"x1\",\"v2\":\"x2\"},\"in\":\"x\"}");
  }

  @Test
  public void shouldSerializeVar() throws Exception {
    assertJson(Var("x"), "{\"var\":\"x\"}");
  }

  @Test
  public void shouldSerializeIf() throws Exception {
    assertJson(
      If(Value(true), Value(true), Value(false)),
      "{\"if\":true,\"then\":true,\"else\":false}");
  }

  @Test
  public void shouldSerializeDo() throws Exception {
    assertJson(
      Do(
        If(Value(true), Value("x"), Value("y")),
        Value(42)
      ), "{\"do\":[{\"if\":true,\"then\":\"x\",\"else\":\"y\"},42]}");

    assertJson(
      Do(ImmutableList.of(
        If(Value(true), Value("xx"), Value("yy")),
        Value(45)
      )), "{\"do\":[{\"if\":true,\"then\":\"xx\",\"else\":\"yy\"},45]}");
  }

  @Test
  public void shouldSerializeLambda() throws Exception {
    assertJson(
      Lambda("x",
        If(Var("x"), Value(42), Value(45))
      ), "{\"lambda\":\"x\",\"expr\":{\"if\":{\"var\":\"x\"},\"then\":42,\"else\":45}}");
  }

  @Test
  public void shouldSerializeMap() throws Exception {
    assertJson(
      Map(
        Lambda("x", Var("x")),
        Arr(Value(1), Value(2), Value(3))
      ), "{\"map\":{\"lambda\":\"x\",\"expr\":{\"var\":\"x\"}},\"collection\":[1,2,3]}");
  }

  @Test
  public void shouldSerializeForeach() throws Exception {
    assertJson(
      Foreach(
        Lambda("x", Var("x")),
        Arr(Value(1), Value(2), Value(3))
      ), "{\"foreach\":{\"lambda\":\"x\",\"expr\":{\"var\":\"x\"}},\"collection\":[1,2,3]}");
  }

  @Test
  public void shouldSerializeFilter() throws Exception {
    assertJson(
      Filter(
        Lambda("x", Var("x")),
        Arr(Value(true), Value(false))
      ), "{\"filter\":{\"lambda\":\"x\",\"expr\":{\"var\":\"x\"}},\"collection\":[true,false]}");
  }

  @Test
  public void shouldSerializeTake() throws Exception {
    assertJson(
      Take(
        Value(2),
        Arr(Value(1), Value(2), Value(3))
      ), "{\"take\":2,\"collection\":[1,2,3]}"
    );
  }

  @Test
  public void shouldSerializeDrop() throws Exception {
    assertJson(
      Drop(
        Value(2),
        Arr(Value(1), Value(2), Value(3))
      ), "{\"drop\":2,\"collection\":[1,2,3]}"
    );
  }

  @Test
  public void shouldSerializePrepend() throws Exception {
    assertJson(
      Prepend(
        Arr(Value(1), Value(2), Value(3)),
        Arr(Value(4), Value(5), Value(6))
      ), "{\"prepend\":[1,2,3],\"collection\":[4,5,6]}"
    );
  }

  @Test
  public void shouldSerializeAppend() throws Exception {
    assertJson(
      Append(
        Arr(Value(4), Value(5), Value(6)),
        Arr(Value(1), Value(2), Value(3))
      ), "{\"append\":[4,5,6],\"collection\":[1,2,3]}"
    );
  }

  @Test
  public void shouldSerializeGet() throws Exception {
    assertJson(
      Get(Ref("classes/spells/104979509692858368")),
      "{\"get\":{\"@ref\":\"classes/spells/104979509692858368\"}}"
    );
  }

  //FIXME: should review the builder. Make it more composable?
  @Test
  public void shouldSerializePaginate() throws Exception {
    assertJson(
      Paginate(Ref("databases")).build(),
      "{\"paginate\":{\"@ref\":\"databases\"}}"
    );

    assertJson(
      Paginate(Ref("databases"))
        .withCursor(After(Ref("databases/test")))
        .withEvents(true)
        .withSources(true)
        .withTs(10L)
        .withSize(2)
        .build(),
      "{\"paginate\":{\"@ref\":\"databases\"},\"ts\":10,\"after\":{\"@ref\":\"databases/test\"}," +
        "\"size\":2,\"events\":true,\"sources\":true}"
    );

    assertJson(
      Paginate(Ref("databases"))
        .withCursor(Before(Ref("databases/test")))
        .withEvents(false)
        .withSources(false)
        .withTs(10L)
        .withSize(2)
        .build(),
      "{\"paginate\":{\"@ref\":\"databases\"},\"ts\":10,\"before\":{\"@ref\":\"databases/test\"},\"size\":2}"
    );
  }

  @Test
  public void shouldSerializeExists() throws Exception {
    assertJson(
      Exists(Ref("classes/spells/104979509692858368")),
      "{\"exists\":{\"@ref\":\"classes/spells/104979509692858368\"}}"
    );

    assertJson(
      Exists(Ref("classes/spells/104979509692858368"), Value(Instant.EPOCH)),
      "{\"exists\":{\"@ref\":\"classes/spells/104979509692858368\"},\"ts\":{\"@ts\":\"1970-01-01T00:00:00Z\"}}"
    );
  }

  @Test
  public void shouldSerializeCount() throws Exception {
    assertJson(
      Count(Ref("databases")),
      "{\"count\":{\"@ref\":\"databases\"}}"
    );

    assertJson(
      Count(Ref("databases"), Value(true)),
      "{\"count\":{\"@ref\":\"databases\"},\"events\":true}"
    );
  }

  @Test
  public void shouldSerializeCreate() throws Exception {
    assertJson(
      Create(
        Ref("databases"),
        Obj("name", Value("annuvin"))
      ), "{\"create\":{\"@ref\":\"databases\"},\"params\":{\"object\":{\"name\":\"annuvin\"}}}");

  }

  @Test
  public void shouldSerializeUpdate() throws Exception {
    assertJson(
      Update(
        Ref("databases/annuvin"),
        Obj("name", Value("llyr"))
      ), "{\"update\":{\"@ref\":\"databases/annuvin\"},\"params\":{\"object\":{\"name\":\"llyr\"}}}");

  }

  @Test
  public void shouldSerializeReplace() throws Exception {
    assertJson(
      Replace(
        Ref("classes/spells/104979509696660483"),
        Obj("data",
          Obj("name", Value("Mountain's Thunder")))
      ), "{\"replace\":{\"@ref\":\"classes/spells/104979509696660483\"}," +
        "\"params\":{\"object\":{\"data\":{\"object\":{\"name\":\"Mountain's Thunder\"}}}}}");

  }

  @Test
  public void shouldSerializeDelete() throws Exception {
    assertJson(
      Delete(Ref("classes/spells/104979509696660483")),
      "{\"delete\":{\"@ref\":\"classes/spells/104979509696660483\"}}"
    );
  }

  //FIXME: review Action usage
  @Test
  public void shouldSerializeInsert() throws Exception {
    assertJson(
      Insert(
        Ref("classes/spells/104979509696660483"),
        Value(Instant.EPOCH),
        Action.CREATE,
        Obj("data", Obj("name", Value("test")))
      ),
      "{\"insert\":{\"@ref\":\"classes/spells/104979509696660483\"},\"ts\":{\"@ts\":\"1970-01-01T00:00:00Z\"}," +
        "\"action\":\"create\",\"params\":{\"object\":{\"data\":{\"object\":{\"name\":\"test\"}}}}}"
    );
  }

  @Test
  public void shouldSerializeRemove() throws Exception {
    assertJson(
      Remove(
        Ref("classes/spells/104979509696660483"),
        Value(Instant.EPOCH),
        Action.DELETE
      ),
      "{\"remove\":{\"@ref\":\"classes/spells/104979509696660483\"}," +
        "\"ts\":{\"@ts\":\"1970-01-01T00:00:00Z\"},\"action\":\"delete\"}"
    );
  }

  @Test
  public void shouldSerializeMatchFunction() throws Exception {
    assertJson(
      Match(Ref("indexes/all_users")),
      "{\"match\":{\"@ref\":\"indexes/all_users\"}}"
    );

    assertJson(
      Match(Ref("indexes/spells_by_element"), Value("fire")),
      "{\"match\":{\"@ref\":\"indexes/spells_by_element\"},\"terms\":\"fire\"}"
    );
  }

  @Test
  public void shouldSerializeUnion() throws Exception {
    assertJson(
      Union(Ref("databases"), Ref("keys")),
      "{\"union\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );

    assertJson(
      Union(ImmutableList.of(Ref("databases"), Ref("keys"))),
      "{\"union\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );
  }

  @Test
  public void shouldSerializeIntersection() throws Exception {
    assertJson(
      Intersection(Ref("databases"), Ref("keys")),
      "{\"intersection\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );

    assertJson(
      Intersection(ImmutableList.of(Ref("databases"), Ref("keys"))),
      "{\"intersection\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );
  }

  @Test
  public void shouldSerializeDifference() throws Exception {
    assertJson(
      Difference(Ref("databases"), Ref("keys")),
      "{\"difference\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );

    assertJson(
      Difference(ImmutableList.of(Ref("databases"), Ref("keys"))),
      "{\"difference\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );
  }

  @Test
  public void shouldSerializeDistinct() throws Exception {
    assertJson(
      Distinct(Ref("databases"), Ref("keys")),
      "{\"distinct\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );

    assertJson(
      Distinct(ImmutableList.of(Ref("databases"), Ref("keys"))),
      "{\"distinct\":[{\"@ref\":\"databases\"},{\"@ref\":\"keys\"}]}"
    );
  }

  @Test
  public void shouldSerializeJoin() throws Exception {
    assertJson(
      Join(
        Match(Ref("indexes/spellbooks_by_owner"), Ref("classes/characters/104979509695139637")),
        Ref("indexes/spells_by_spellbook")
      ),
      "{\"join\":{\"match\":{\"@ref\":\"indexes/spellbooks_by_owner\"}," +
        "\"terms\":{\"@ref\":\"classes/characters/104979509695139637\"}}," +
        "\"with\":{\"@ref\":\"indexes/spells_by_spellbook\"}}"
    );
  }

  @Test
  public void shouldSerializeLogin() throws Exception {
    assertJson(
      Login(
        Ref("classes/characters/104979509695139637"),
        Obj("password", Value("abracadabra"))
      ),
      "{\"login\":{\"@ref\":\"classes/characters/104979509695139637\"}," +
        "\"params\":{\"object\":{\"password\":\"abracadabra\"}}}"
    );
  }

  @Test
  public void shouldSerializeLogout() throws Exception {
    assertJson(Logout(Value(true)), "{\"logout\":true}");
  }

  @Test
  public void shouldSerializeIdentify() throws Exception {
    assertJson(
      Identify(Ref("classes/characters/104979509695139637"), Value("abracadabra")),
      "{\"identify\":{\"@ref\":\"classes/characters/104979509695139637\"},\"password\":\"abracadabra\"}"
    );
  }

  @Test
  public void shouldSerializeConcat() throws Exception {
    assertJson(
      Concat(Value("Hen"), Value("Wen")),
      "{\"concat\":[\"Hen\",\"Wen\"]}"
    );

    assertJson(
      Concat(
        ImmutableList.of(
          Value("Hen"),
          Value("Wen")
        )
      ), "{\"concat\":[\"Hen\",\"Wen\"]}"
    );

    assertJson(
      Concat(
        ImmutableList.of(
          Value("Hen"),
          Value("Wen")
        ),
        Value(" ")
      ), "{\"concat\":[\"Hen\",\"Wen\"],\"separator\":\" \"}"
    );
  }

  @Test
  public void shouldSerializeCasefold() throws Exception {
    assertJson(Casefold(Value("Hen Wen")), "{\"casefold\":\"Hen Wen\"}");
  }

  @Test
  public void shouldSerializeTime() throws Exception {
    assertJson(
      Time(Value("1970-01-01T00:00:00+00:00")),
      "{\"time\":\"1970-01-01T00:00:00+00:00\"}"
    );
  }

  //FIXME: check time unit use case. Is it composable?
  @Test
  public void shouldSerializeEpoch() throws Exception {
    assertJson(Epoch(Value(0), TimeUnit.SECOND), "{\"epoch\":0,\"unit\":\"second\"}");
  }

  @Test
  public void shouldSerializeDate() throws Exception {
    assertJson(Date(Value("1970-01-01")), "{\"date\":\"1970-01-01\"}");
  }

  @Test
  public void shouldSerializeNextId() throws Exception {
    assertJson(NextId(), "{\"next_id\":null}");
  }

  @Test
  public void shouldSerializeEquals() throws Exception {
    assertJson(Equals(Value("fire"), Value("fire")), "{\"equals\":[\"fire\",\"fire\"]}");
    assertJson(Equals(ImmutableList.of(Value("fire"), Value("fire"))), "{\"equals\":[\"fire\",\"fire\"]}");
  }

  //FIXME: check path use case. Is it composable?
  @Test
  public void shouldSerializeContains() throws Exception {
    assertJson(
      Contains(
        Path(Path.Object("favorites"), Path.Object("foods")),
        Obj("favorites",
          Obj("foods", Arr(
            Value("crunchings"),
            Value("munchings"),
            Value("lunchings")
          ))
        )
      ),
      "{\"contains\":[\"favorites\",\"foods\"],\"in\":" +
        "{\"object\":{\"favorites\":{\"object\":{\"foods\":[\"crunchings\",\"munchings\",\"lunchings\"]}}}}}");
  }

  @Test
  public void shouldSerializeSelect() throws Exception {
    assertJson(
      Select(
        Path(Path.Object("favorites"), Path.Object("foods"), Path.Array(1)),
        Obj("favorites",
          Obj("foods", Arr(
            Value("crunchings"),
            Value("munchings"),
            Value("lunchings")
          ))
        )
      ),
      "{\"select\":[\"favorites\",\"foods\",1],\"from\":" +
        "{\"object\":{\"favorites\":{\"object\":{\"foods\":[\"crunchings\",\"munchings\",\"lunchings\"]}}}}}"
    );
  }

  @Test
  public void shouldSerializeAdd() throws Exception {
    assertJson(Add(Value(100), Value(10)), "{\"add\":[100,10]}");
    assertJson(Add(ImmutableList.of(Value(100), Value(10))), "{\"add\":[100,10]}");
  }

  @Test
  public void shouldSerializeMultiply() throws Exception {
    assertJson(Multiply(Value(100), Value(10)), "{\"multiply\":[100,10]}");
    assertJson(Multiply(ImmutableList.of(Value(100), Value(10))), "{\"multiply\":[100,10]}");
  }

  @Test
  public void shouldSerializeSubtract() throws Exception {
    assertJson(Subtract(Value(100), Value(10)), "{\"subtract\":[100,10]}");
    assertJson(Subtract(ImmutableList.of(Value(100), Value(10))), "{\"subtract\":[100,10]}");
  }

  @Test
  public void shouldSerializeDivide() throws Exception {
    assertJson(Divide(Value(100), Value(10)), "{\"divide\":[100,10]}");
    assertJson(Divide(ImmutableList.of(Value(100), Value(10))), "{\"divide\":[100,10]}");
  }

  @Test
  public void shouldSerializeModulo() throws Exception {
    assertJson(Modulo(Value(100), Value(10)), "{\"modulo\":[100,10]}");
    assertJson(Modulo(ImmutableList.of(Value(100), Value(10))), "{\"modulo\":[100,10]}");
  }

  @Test
  public void shouldSerializeLT() throws Exception {
    assertJson(LT(Value(1), Value(2), Value(3)), "{\"lt\":[1,2,3]}");
    assertJson(LT(ImmutableList.of(Value(1), Value(2), Value(3))), "{\"lt\":[1,2,3]}");
  }

  @Test
  public void shouldSerializeLTE() throws Exception {
    assertJson(LTE(Value(1), Value(2), Value(2)), "{\"lte\":[1,2,2]}");
    assertJson(LTE(ImmutableList.of(Value(1), Value(2), Value(2))), "{\"lte\":[1,2,2]}");
  }

  @Test
  public void shouldSerializeGT() throws Exception {
    assertJson(GT(Value(3), Value(2), Value(1)), "{\"gt\":[3,2,1]}");
    assertJson(GT(ImmutableList.of(Value(3), Value(2), Value(1))), "{\"gt\":[3,2,1]}");
  }

  @Test
  public void shouldSerializeGTE() throws Exception {
    assertJson(GTE(Value(3), Value(2), Value(2)), "{\"gte\":[3,2,2]}");
    assertJson(GTE(ImmutableList.of(Value(3), Value(2), Value(2))), "{\"gte\":[3,2,2]}");
  }

  @Test
  public void shouldSerializeAnd() throws Exception {
    assertJson(And(Value(true), Value(true), Value(false)), "{\"and\":[true,true,false]}");
    assertJson(And(ImmutableList.of(Value(true), Value(true), Value(false))), "{\"and\":[true,true,false]}");
  }

  @Test
  public void shouldSerializeOr() throws Exception {
    assertJson(Or(Value(true), Value(true), Value(false)), "{\"or\":[true,true,false]}");
    assertJson(Or(ImmutableList.of(Value(true), Value(true), Value(false))), "{\"or\":[true,true,false]}");
  }

  @Test
  public void shouldSerializeNot() throws Exception {
    assertJson(Not(Value(true)), "{\"not\":true}");
  }

  private void assertJson(Expr expr, String jsonString) throws JsonProcessingException {
    assertThat(json.writeValueAsString(expr),
      equalTo(jsonString));
  }

}
