package com.calendly.verticle;
import com.calendly.model.User;
import com.calendly.repository.UserRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class UserVerticle extends AbstractVerticle {

    private UserRepository userRepository = new UserRepository();
    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/users/:id").handler(this::handleGetUser);
        router.post("/users").handler(this::handleCreateUser);
        router.put("/users/:id").handler(this::handleUpdateUser);
        router.delete("/users/:id").handler(this::handleDeleteUser);

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    private void handleGetUser(RoutingContext routingContext) {
        String userId = routingContext.request().getParam("id");
        userRepository.findById(userId).ifPresentOrElse(
            user -> routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(user)),
            () -> routingContext.response().setStatusCode(404).end()
        );
    }

    private void handleCreateUser(RoutingContext routingContext) {
        User user = Json.decodeValue(routingContext.getBodyAsString(), User.class);
        User createdUser = userRepository.save(user);
        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(201)
            .end(Json.encodePrettily(createdUser));
    }

    private void handleUpdateUser(RoutingContext routingContext) {
        String userId = routingContext.request().getParam("id");
        User user = Json.decodeValue(routingContext.getBodyAsString(), User.class);
        user.setId(userId);

        userRepository.findById(userId).ifPresentOrElse(
            existingUser -> {
                userRepository.save(user);
                routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(user));
            },
            () -> routingContext.response().setStatusCode(404).end()
        );
    }

    private void handleDeleteUser(RoutingContext routingContext) {
        String userId = routingContext.request().getParam("id");
        userRepository.deleteById(userId);
        routingContext.response().setStatusCode(204).end();
    }
}