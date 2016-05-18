package com.faunadb.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faunadb.client.query.Expr;
import com.faunadb.client.query.Path;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Ignore;
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

  //TODO: confirm if its needed
  @Test
  @Ignore
  public void serializeQuote() throws Exception {

  }

  @Test
  @Ignore
  public void testSerializeSetExpr() throws Exception {
  }

  @Test
  @Ignore
  public void validateNull() throws Exception {
  }

  private void assertJson(Expr expr, String jsonString) throws JsonProcessingException {
    assertThat(json.writeValueAsString(expr),
      equalTo(jsonString));
  }

//  public void serializeComplexValues() throws JsonProcessingException {
//    ArrayV value1 = ArrayV(LongV(1), StringV("test"));
//    assertThat(json.writeValueAsString(value1), is("[1,\"test\"]"));
//    ArrayV value2 = ArrayV(ArrayV(ObjectV("test", StringV("value")), LongV(2323), BooleanV.True), StringV("hi"), ObjectV("test", StringV("yo"), "test2", NullV.Null));
//    assertThat(json.writeValueAsString(value2), is("[[{\"test\":\"value\"},2323,true],\"hi\",{\"test\":\"yo\",\"test2\":null}]"));
//    ObjectV obj1 = ObjectV("test", LongV(1), "test2", Ref("some/ref"));
//    assertThat(json.writeValueAsString(obj1), is("{\"test\":1,\"test2\":{\"@ref\":\"some/ref\"}}"));
//  }
//
//  @Test
//  public void serializeBasicForms() throws JsonProcessingException {
//    Value letAndVar = Let(ImmutableMap.<String, Value>of("x", LongV(1), "y", StringV("2")), Var("x"));
//    assertThat(json.writeValueAsString(letAndVar), is("{\"let\":{\"x\":1,\"y\":\"2\"},\"in\":{\"var\":\"x\"}}"));
//
//    Value ifForm = If(BooleanV.True, StringV("was true"), StringV("was false"));
//    assertThat(json.writeValueAsString(ifForm), is("{\"if\":true,\"then\":\"was true\",\"else\":\"was false\"}"));
//
//    Value doForm = Do(
//      Create(Ref("some/ref/1"), Quote(ObjectV("data", ObjectV("name", StringV("Hen Wen"))))),
//      Get(Ref("some/ref/1")));
//    assertThat(json.writeValueAsString(doForm), is("{\"do\":[{\"create\":{\"@ref\":\"some/ref/1\"},\"params\":{\"quote\":{\"data\":{\"name\":\"Hen Wen\"}}}},{\"get\":{\"@ref\":\"some/ref/1\"}}]}"));
//
//    Value select = Select(Path(Path.Object("favorites"), Path.Object("foods"), Path.Array(1)),
//      Quote(ObjectV("favorites", ObjectV("foods", ArrayV(StringV("crunchings"), StringV("munchings"), StringV("lunchings"))))));
//
//    assertThat(json.writeValueAsString(select), is("{\"select\":[\"favorites\",\"foods\",1],\"from\":{\"quote\":{\"favorites\":{\"foods\":[\"crunchings\",\"munchings\",\"lunchings\"]}}}}"));
//
//    Value quote = Quote(ObjectV("name", StringV("Hen Wen"), "Age", Add(LongV(100), LongV(10))));
//  }
//
//  @Test
//  public void serializeCollections() throws JsonProcessingException {
//    Value map = Map(Lambda("munchings", Var("munchings")), ArrayV(LongV(1), LongV(2), LongV(3)));
//    assertEquals(json.writeValueAsString(map), "{\"map\":{\"lambda\":\"munchings\",\"expr\":{\"var\":\"munchings\"}},\"collection\":[1,2,3]}");
//
//    Value foreach = Foreach(Lambda("creature", Create(Ref("some/ref"), Object(ObjectV("data", Object(ObjectV("some", Var("creature"))))))), ArrayV(Ref("another/ref/1"), Ref("another/ref/2")));
//    assertEquals(json.writeValueAsString(foreach), "{\"foreach\":{\"lambda\":\"creature\",\"expr\":{\"create\":{\"@ref\":\"some/ref\"},\"params\":{\"object\":{\"data\":{\"object\":{\"some\":{\"var\":\"creature\"}}}}}}},\"collection\":[{\"@ref\":\"another/ref/1\"},{\"@ref\":\"another/ref/2\"}]}");
//
//    Value filter = Filter(Lambda("i", Equals(LongV(1L), Var("i"))), ArrayV(LongV(1L), LongV(2L), LongV(3L)));
//    assertThat(json.writeValueAsString(filter), is("{\"filter\":{\"lambda\":\"i\",\"expr\":{\"equals\":[1,{\"var\":\"i\"}]}},\"collection\":[1,2,3]}"));
//
//    Value take = Take(LongV(2L), ArrayV(LongV(1L),LongV(2L),LongV(3L)));
//    assertThat(json.writeValueAsString(take), is("{\"take\":2,\"collection\":[1,2,3]}"));
//
//    Value drop = Drop(LongV(2L), ArrayV(LongV(1L), LongV(2L), LongV(3L)));
//    assertThat(json.writeValueAsString(drop), is("{\"drop\":2,\"collection\":[1,2,3]}"));
//
//    Value prepend = Prepend(ArrayV(LongV(1L), LongV(2L), LongV(3L)), ArrayV(LongV(4L), LongV(5L), LongV(6L)));
//    assertThat(json.writeValueAsString(prepend), is("{\"prepend\":[1,2,3],\"collection\":[4,5,6]}"));
//
//    Value append = Append(ArrayV(LongV(4L), LongV(5L), LongV(6L)), ArrayV(LongV(1L), LongV(2L), LongV(3L)));
//    assertThat(json.writeValueAsString(append), is("{\"append\":[4,5,6],\"collection\":[1,2,3]}"));
//  }
//
//  @Test
//  public void serializeResourceRetrieval() throws JsonProcessingException {
//    Ref ref = Ref("some/ref/1");
//    Value get = Get(ref);
//
//    assertThat(json.writeValueAsString(get), is("{\"get\":{\"@ref\":\"some/ref/1\"}}"));
//
//    Value paginate1 = Paginate(Union(
//      Match(StringV("term"), Ref("indexes/some_index")),
//      Match(StringV("term2"), Ref("indexes/some_index")))).build();
//
//    assertThat(json.writeValueAsString(paginate1), is("{\"paginate\":{\"union\":[{\"match\":\"term\",\"index\":{\"@ref\":\"indexes/some_index\"}},{\"match\":\"term2\",\"index\":{\"@ref\":\"indexes/some_index\"}}]}}"));
//
//    Value paginate2 = Paginate(Union(
//      Match(StringV("term"), Ref("indexes/some_index")),
//      Match(StringV("term2"), Ref("indexes/some_index")))).withSources(true).build();
//
//    assertThat(json.writeValueAsString(paginate2), is("{\"paginate\":{\"union\":[{\"match\":\"term\",\"index\":{\"@ref\":\"indexes/some_index\"}},{\"match\":\"term2\",\"index\":{\"@ref\":\"indexes/some_index\"}}]},\"sources\":true}"));
//
//    Value paginate3 = Paginate(Union(
//      Match(StringV("term"), Ref("indexes/some_index")),
//      Match(StringV("term2"), Ref("indexes/some_index")))).withEvents(true).build();
//
//    assertThat(json.writeValueAsString(paginate3), is("{\"paginate\":{\"union\":[{\"match\":\"term\",\"index\":{\"@ref\":\"indexes/some_index\"}},{\"match\":\"term2\",\"index\":{\"@ref\":\"indexes/some_index\"}}]},\"events\":true}"));
//
//    Value paginate4 = Paginate(Union(
//      Match(StringV("term"), Ref("indexes/some_index")),
//      Match(StringV("term2"), Ref("indexes/some_index"))))
//      .withCursor(Before(Ref("some/ref/1")))
//      .withSize(4).build();
//
//    assertThat(json.writeValueAsString(paginate4), is("{\"paginate\":{\"union\":[{\"match\":\"term\",\"index\":{\"@ref\":\"indexes/some_index\"}},{\"match\":\"term2\",\"index\":{\"@ref\":\"indexes/some_index\"}}]},\"before\":{\"@ref\":\"some/ref/1\"},\"size\":4}"));
//
//    Value count = Count(Match(StringV("fire"), Ref("indexes/spells_by_element")));
//    assertThat(json.writeValueAsString(count), is("{\"count\":{\"match\":\"fire\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}}}"));
//  }
//
//  @Test
//  public void serializeResourceModification() throws JsonProcessingException {
//    Ref ref = Ref("classes/spells");
//    ObjectV params = ObjectV("name", StringV("Mountainous Thunder"), "element", StringV("air"), "cost", LongV(15));
//    Value create = Create(ref, Quote(ObjectV("data", params)));
//    assertThat(json.writeValueAsString(create), is("{\"create\":{\"@ref\":\"classes/spells\"},\"params\":{\"quote\":{\"data\":{\"name\":\"Mountainous Thunder\",\"element\":\"air\",\"cost\":15}}}}"));
//
//    Value update = Update(Ref("classes/spells/123456"), Quote(ObjectV("data", ObjectV("name", StringV("Mountain's Thunder"), "cost", NullV.Null))));
//    assertThat(json.writeValueAsString(update), is("{\"update\":{\"@ref\":\"classes/spells/123456\"},\"params\":{\"quote\":{\"data\":{\"name\":\"Mountain's Thunder\",\"cost\":null}}}}"));
//
//    Value replace = Replace(Ref("classes/spells/123456"), Quote(ObjectV("data", ObjectV("name", StringV("Mountain's Thunder"), "element", ArrayV(StringV("air"), StringV("earth")), "cost", LongV(10)))));
//    assertThat(json.writeValueAsString(replace), is("{\"replace\":{\"@ref\":\"classes/spells/123456\"},\"params\":{\"quote\":{\"data\":{\"name\":\"Mountain's Thunder\",\"element\":[\"air\",\"earth\"],\"cost\":10}}}}"));
//
//    Value delete = Delete(Ref("classes/spells/123456"));
//    assertThat(json.writeValueAsString(delete), is("{\"delete\":{\"@ref\":\"classes/spells/123456\"}}"));
//
//    Value insert = Insert(Ref("classes/spells/123456"), 1L, Action.CREATE, Quote(ObjectV("data", ObjectV("name", StringV("Mountain's Thunder"), "cost", LongV(10), "element", ArrayV(StringV("air"), StringV("earth"))))));
//    assertThat(json.writeValueAsString(insert), is("{\"insert\":{\"@ref\":\"classes/spells/123456\"},\"ts\":1,\"action\":\"create\",\"params\":{\"quote\":{\"data\":{\"name\":\"Mountain's Thunder\",\"cost\":10,\"element\":[\"air\",\"earth\"]}}}}"));
//
//    Value remove = Remove(Ref("classes/spells/123456"), 1L, Action.DELETE);
//    assertThat(json.writeValueAsString(remove), is("{\"remove\":{\"@ref\":\"classes/spells/123456\"},\"ts\":1,\"action\":\"delete\"}"));
//  }
//
//  @Test
//  public void serializeSets() throws JsonProcessingException {
//    Value match = Match(StringV("fire"), Ref("indexes/spells_by_elements"));
//    assertThat(json.writeValueAsString(match), is("{\"match\":\"fire\",\"index\":{\"@ref\":\"indexes/spells_by_elements\"}}"));
//
//    Value union = Union(
//      Match(StringV("fire"), Ref("indexes/spells_by_element")),
//      Match(StringV("water"), Ref("indexes/spells_by_element"))
//    );
//
//    assertThat(json.writeValueAsString(union), is("{\"union\":[{\"match\":\"fire\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}},{\"match\":\"water\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}}]}"));
//
//    Value intersection = Intersection(
//      Match(StringV("fire"), Ref("indexes/spells_by_element")),
//      Match(StringV("water"), Ref("indexes/spells_by_element"))
//    );
//
//    assertThat(json.writeValueAsString(intersection), is("{\"intersection\":[{\"match\":\"fire\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}},{\"match\":\"water\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}}]}"));
//
//    Value difference = Difference(
//      Match(StringV("fire"), Ref("indexes/spells_by_element")),
//      Match(StringV("water"), Ref("indexes/spells_by_element"))
//    );
//
//    assertThat(json.writeValueAsString(difference), is("{\"difference\":[{\"match\":\"fire\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}},{\"match\":\"water\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}}]}"));
//
//    Value join = Join(Match(StringV("fire"), Ref("indexes/spells_by_element")),
//      Lambda("spell", Get(Var("spell"))));
//
//    assertThat(json.writeValueAsString(join), is("{\"join\":{\"match\":\"fire\",\"index\":{\"@ref\":\"indexes/spells_by_element\"}},\"with\":{\"lambda\":\"spell\",\"expr\":{\"get\":{\"var\":\"spell\"}}}}"));
//  }
//
//  @Test
//  public void serializeAuthentication() throws JsonProcessingException {
//    Value login = Login(Ref("classes/characters/104979509695139637"), Quote(ObjectV("password", StringV("abracadabra"))));
//    assertThat(json.writeValueAsString(login), is("{\"login\":{\"@ref\":\"classes/characters/104979509695139637\"},\"params\":{\"quote\":{\"password\":\"abracadabra\"}}}"));
//
//    Value logout = Logout(true);
//    assertThat(json.writeValueAsString(logout), is("{\"logout\":true}"));
//
//    Value identify = Identify(Ref("classes/characters/104979509695139637"), StringV("abracadabra"));
//    assertThat(json.writeValueAsString(identify), is("{\"identify\":{\"@ref\":\"classes/characters/104979509695139637\"},\"password\":\"abracadabra\"}"));
//  }
//
//  @Test
//  public void serializeTsAndDateValues() throws JsonProcessingException {
//    Value ts = TsV(Instant.EPOCH.plus(5, ChronoUnit.MINUTES));
//    assertThat(json.writeValueAsString(ts), is("{\"@ts\":\"1970-01-01T00:05:00Z\"}"));
//
//    Value date = DateV(LocalDate.ofEpochDay(2));
//    assertThat(json.writeValueAsString(date), is("{\"@date\":\"1970-01-03\"}"));
//  }
//
//  @Test
//  public void serializeDateAndTime() throws JsonProcessingException {
//    Value time = Time(StringV("1970-01-01T00:00:00+00:00"));
//    assertThat(json.writeValueAsString(time), is("{\"time\":\"1970-01-01T00:00:00+00:00\"}"));
//
//    Value epoch = Epoch(LongV(10L), TimeUnit.SECOND);
//    assertThat(json.writeValueAsString(epoch), is("{\"epoch\":10,\"unit\":\"second\"}"));
//
//    Value epoch2 = Epoch(LongV(10L), "millisecond");
//    assertThat(json.writeValueAsString(epoch2), is("{\"epoch\":10,\"unit\":\"millisecond\"}"));
//
//    Value date = Date(StringV("1970-01-02"));
//    assertThat(json.writeValueAsString(date), is("{\"date\":\"1970-01-02\"}"));
//  }
//
//  @Test
//  public void serializeMiscAndMath() throws JsonProcessingException {
//    Value equals = Equals(StringV("fire"), StringV("fire"));
//    assertThat(json.writeValueAsString(equals), is("{\"equals\":[\"fire\",\"fire\"]}"));
//
//    Value concat = Concat(StringV("Hen"), StringV("Wen"));
//    assertThat(json.writeValueAsString(concat), is("{\"concat\":[\"Hen\",\"Wen\"]}"));
//
//    Value concat2 = Concat(ImmutableList.<Value>of(StringV("Hen"), StringV("Wen")), " ");
//    assertThat(json.writeValueAsString(concat2), is("{\"concat\":[\"Hen\",\"Wen\"],\"separator\":\" \"}"));
//
//    Value add = Add(LongV(1L), LongV(2L));
//    assertThat(json.writeValueAsString(add), is("{\"add\":[1,2]}"));
//
//    Value multiply = Multiply(LongV(1L), LongV(2L));
//    assertThat(json.writeValueAsString(multiply), is("{\"multiply\":[1,2]}"));
//
//    Value subtract = Subtract(LongV(1L), LongV(2L));
//    assertThat(json.writeValueAsString(subtract), is("{\"subtract\":[1,2]}"));
//
//    Value divide = Divide(LongV(1L), LongV(2L));
//    assertThat(json.writeValueAsString(divide), is("{\"divide\":[1,2]}"));
//
//    Value modulo = Modulo(LongV(1L), LongV(2L));
//    assertThat(json.writeValueAsString(modulo), is("{\"modulo\":[1,2]}"));
//
//    Value and = And(BooleanV(true), BooleanV(false));
//    assertThat(json.writeValueAsString(and), is("{\"and\":[true,false]}"));
//
//    Value or = Or(BooleanV(true), BooleanV(false));
//    assertThat(json.writeValueAsString(or), is("{\"or\":[true,false]}"));
//
//    Value not = Not(BooleanV(false));
//    assertThat(json.writeValueAsString(not), is("{\"not\":false}"));
//  }

}
