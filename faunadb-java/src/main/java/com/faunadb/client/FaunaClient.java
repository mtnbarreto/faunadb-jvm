package com.faunadb.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.faunadb.client.errors.*;
import com.faunadb.client.types.LazyValue;
import com.faunadb.client.types.Value;
import com.faunadb.common.Connection;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * The Java native client for FaunaDB.
 *
 * <p>This client is asynchronous, so all methods that perform latent operations return a {@link ListenableFuture}.</p>
 *
 * <p>Queries are constructed by using the static helpers in the {@link com.faunadb.client.query.Language} package.</p>
 *
 * <p><b>Example</b>:</p>
 * <pre>{@code
 * import static com.faunadb.client.query.Language.*;
 * FaunaClient client = FaunaClient.create(Connection.builder().withAuthToken("someAuthToken").build());
 * client.query(Get(Ref("some/ref")));
 * }
 * </pre>
 *
 * @see com.faunadb.client.query.Language
 */
public class FaunaClient {

  private static final Charset UTF8 = Charset.forName("UTF-8");

  /**
   * Returns a new {@link FaunaClient} instance.
   *
   * @param connection the underlying {@link Connection} adapter for the client to use.
   */
  public static FaunaClient create(Connection connection) {
    ObjectMapper json = new ObjectMapper();
    json.registerModule(new GuavaModule());
    return new FaunaClient(connection, json);
  }

  /**
   * Returns a new {@link FaunaClient} instance.
   *
   * @param connection the underlying {@link Connection} adapter for the client to use.
   * @param json a custom {@link ObjectMapper} to customize JSON serialization and deserialization behavior.
   */
  public static FaunaClient create(Connection connection, ObjectMapper json) {
    return new FaunaClient(connection, json.copy().registerModule(new GuavaModule()));
  }

  private final Connection connection;
  private final ObjectMapper json;

  FaunaClient(Connection connection, ObjectMapper json) {
    this.connection = connection;
    this.json = json;
  }

  /**
   * Frees any resources held by the client. Also closes the underlying {@link Connection}.
   */
  public void close() {
    connection.close();
  }

  /**
   * Issues a Query to FaunaDB.
   *
   * <p>Queries are modeled through the FaunaDB query language, represented by the helper functions in the
   * {@link com.faunadb.client.query} package. See {@link com.faunadb.client.query.Language} for helpers
   * and examples.
   *
   * <p>Responses are modeled as a general response tree. Each node is a {@link Value}, and
   * can be coerced to structured types through various methods on that class.
   *
   * @param expr The query expression to be sent to FaunaDB.
   * @return A {@link ListenableFuture} containing the root node of the Response tree.
   * @see Value
   * @see com.faunadb.client.query.Language
   *
   */
  public ListenableFuture<Value> query(Value expr) {
    JsonNode body = json.valueToTree(expr);
    try {
      return handleNetworkExceptions(Futures.transform(connection.post("/", body), new Function<Response, Value>() {
        @Override
        public Value apply(Response response) {
          try {
            handleQueryErrors(response);

            JsonNode responseBody = parseResponseBody(response);
            JsonNode resource = responseBody.get("resource");
            return json.treeToValue(resource, LazyValue.class);
          } catch (IOException ex) {
            throw new AssertionError(ex);
          }
        }
      }));
    } catch (IOException ex) {
      return Futures.immediateFailedFuture(ex);
    }
  }

  /**
   * Issues multiple queries to FaunaDB.
   *
   * <p>These queries are sent to FaunaDB in a single request, and are evaluated. The list of response nodes is returned
   * in the same order as the issued queries.
   *
   * See {@link FaunaClient#query(Value)} for more information on the individual queries.
   *
   * @param exprs the list of query expressions to be sent to FaunaDB.
   * @return a {@link ListenableFuture} containing an ordered list of root response nodes.
   */
  public <T extends Value> ListenableFuture<ImmutableList<Value>> query(ImmutableList<T> exprs) {
    JsonNode body = json.valueToTree(exprs);
    try {
      return handleNetworkExceptions(Futures.transform(connection.post("/", body), new Function<Response, ImmutableList<Value>>() {
        @Override
        public ImmutableList<Value> apply(Response resp) {
          try {
            handleQueryErrors(resp);
            JsonNode responseBody = parseResponseBody(resp);
            ArrayNode resources = ((ArrayNode) responseBody.get("resource"));
            ImmutableList.Builder<Value> responseNodeBuilder = ImmutableList.builder();

            for (JsonNode resource : resources) {
              responseNodeBuilder.add(json.treeToValue(resource, LazyValue.class));
            }

            return responseNodeBuilder.build();
          } catch (IOException ex) {
            throw new AssertionError(ex);
          }
        }
      }));
    } catch (IOException ex) {
      return Futures.immediateFailedFuture(ex);
    }
  }

  private void handleQueryErrors(Response response) throws IOException, FaunaException {
    int status = response.getStatusCode();
    if (status >= 300) {
      try {
        ArrayNode errors = (ArrayNode) parseResponseBody(response).get("errors");
        ImmutableList.Builder<HttpResponses.QueryError> errorBuilder = ImmutableList.builder();

        for (JsonNode errorNode : errors) {
          errorBuilder.add(json.treeToValue(errorNode, HttpResponses.QueryError.class));
        }

        HttpResponses.QueryErrorResponse errorResponse = HttpResponses.QueryErrorResponse.create(status, errorBuilder.build());

        switch (status) {
          case 400:
            throw new BadRequestException(errorResponse);
          case 401:
            throw new UnauthorizedException(errorResponse);
          case 404:
            throw new NotFoundException(errorResponse);
          case 500:
            throw new InternalException(errorResponse);
          case 503:
            throw new UnavailableException(errorResponse);
          default:
            throw new UnknownException(errorResponse);
        }
      } catch (IOException ex) {
        switch (status) {
          case 503:
            throw new UnavailableException("Service Unavailable: Unparseable response.");
          default:
            throw new UnknownException("Unparseable service " + status + "response.");
        }
      }
    }
  }

  private <V> ListenableFuture<V> handleNetworkExceptions(ListenableFuture<V> f) {
    ListenableFuture<V> f1 = Futures.catching(f, ConnectException.class, new Function<ConnectException, V>() {
      @Override
      public V apply(ConnectException input) {
        throw new UnavailableException(input.getMessage());
      }
    });

    return Futures.catching(f1, TimeoutException.class, new Function<TimeoutException, V>() {
      @Override
      public V apply(TimeoutException input) {
        throw new UnavailableException(input.getMessage());
      }
    });
  }

  private JsonNode parseResponseBody(Response response) throws IOException {
    return json.readTree(response.getResponseBody(UTF8));
  }
}
